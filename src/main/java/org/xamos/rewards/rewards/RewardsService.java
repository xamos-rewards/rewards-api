package org.xamos.rewards.rewards;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.xamos.rewards.exceptions.InsufficientPointsException;
import org.xamos.rewards.models.Rewards;
import org.xamos.rewards.models.dto.PointsAdjustmentRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
        })).log();
  }

  public Flux<Rewards> getRewards() {
    return rewardsRepository.findAll().log();
  }

  public Mono<Rewards> adjustRewards(String username, Long points, PointsAdjustmentRequest.Operation operation) {
    if (points < 0) {
      return Mono.error(new IllegalArgumentException("Points cannot be negative"));
    }

    return switch (operation) {
      case ADD -> addRewards(username, points);
      case DEDUCT -> deductRewards(username, points);
    };
  }

  private Mono<Rewards> addRewards(String username, Long points) {
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
        })).log();
  }

  private Mono<Rewards> deductRewards(String username, Long points) {
    return rewardsRepository.findById(username)
        .flatMap(rewards -> {
          if (rewards.getPoints() < points) {
            return Mono.error(new InsufficientPointsException("Insufficient points"));
          }
          rewards.setPoints(rewards.getPoints() - points);
          return rewardsRepository.save(rewards);
        }).log();
  }
}
