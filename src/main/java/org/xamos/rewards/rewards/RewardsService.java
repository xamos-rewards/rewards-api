package org.xamos.rewards.rewards;

import lombok.AllArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.xamos.rewards.models.Rewards;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RewardsService {

  private final RewardsRepository rewardsRepository;
  private final R2dbcEntityTemplate template;

  public Mono<Rewards> getRewards(String username) {
    return rewardsRepository.findById(username)
        .switchIfEmpty(Mono.defer(() -> {
          Rewards newRewards = new Rewards(username, 0L);
          return this.template.insert(newRewards);
        }));
  }

  public Flux<Rewards> getRewards() {
    return rewardsRepository.findAll();
  }

  public Mono<Rewards> addRewards(String username, Long points) {
    if (points < 0) {
      return Mono.error(new IllegalArgumentException("Points cannot be negative"));
    }

    return rewardsRepository.findById(username)
        .flatMap(rewards -> {
          rewards.setPoints(rewards.getPoints() + points);
          return rewardsRepository.save(rewards);
        })
        .switchIfEmpty(Mono.defer(() -> { // Mono.defer allows this fallback to only execute if the above is actually empty
          // Without defer, the new Rewards object would be created even if the user already exists,
          // effectively resetting their points
          Rewards newRewards = new Rewards(username, points);
          return rewardsRepository.save(newRewards);
        }));
  }

  public Mono<Rewards> deductRewards(String username, Long points) {
    if (points < 0) {
      return Mono.error(new IllegalArgumentException("Points cannot be negative"));
    }

    return rewardsRepository.findById(username)
        .flatMap(rewards -> {
          if (rewards.getPoints() < points) {
            return Mono.error(new IllegalArgumentException("Insufficient points"));
          }
          rewards.setPoints(rewards.getPoints() - points);
          return rewardsRepository.save(rewards);
        });
  }
}
