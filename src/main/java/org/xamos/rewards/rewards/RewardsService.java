package org.xamos.rewards.rewards;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xamos.rewards.exceptions.InsufficientPointsException;
import org.xamos.rewards.models.Rewards;
import org.xamos.rewards.models.dto.PointsAdjustmentRequest;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RewardsService {

  private final RewardsRepository rewardsRepository;

  @Transactional
  public Rewards getRewards(String username) {
    return rewardsRepository.findById(username)
        .orElseGet(() -> {
          Rewards newRewards = new Rewards(username, 0L);
          return rewardsRepository.save(newRewards);
        });
  }

  public List<Rewards> getRewards() {
    return rewardsRepository.findAll();
  }

  @Transactional
  public Rewards adjustRewards(String username, Long points, PointsAdjustmentRequest.Operation operation) {
    if (points < 0) {
      throw new IllegalArgumentException("Points cannot be negative");
    }

    return switch (operation) {
      case ADD -> addRewards(username, points);
      case DEDUCT -> deductRewards(username, points);
    };
  }

  private Rewards addRewards(String username, Long points) {
    Rewards rewards = rewardsRepository.findById(username)
        .orElseGet(() -> new Rewards(username, 0L));
    
    rewards.setPoints(rewards.getPoints() + points);
    return rewardsRepository.save(rewards);
  }

  private Rewards deductRewards(String username, Long points) {
    Rewards rewards = rewardsRepository.findById(username)
        .orElseThrow(() -> new InsufficientPointsException("User has no rewards account"));
    
    if (rewards.getPoints() < points) {
      throw new InsufficientPointsException("Insufficient points");
    }
    
    rewards.setPoints(rewards.getPoints() - points);
    return rewardsRepository.save(rewards);
  }
}
