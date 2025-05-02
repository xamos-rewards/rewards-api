package org.xamos.rewards.rewards;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xamos.rewards.models.Rewards;
import org.xamos.rewards.models.dto.PointsAdjustmentRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/rewards")
@AllArgsConstructor
public class RewardsController {

  private final RewardsService rewardsService;

  @GetMapping
  public ResponseEntity<Flux<Rewards>> getRewards() {
    return ResponseEntity.ok(rewardsService.getRewards());
  }

  @GetMapping("/{username}")
  public Mono<ResponseEntity<Rewards>> getRewards(@PathVariable String username) {
    return rewardsService.getRewards(username)
            .map(rewards -> ResponseEntity.ok(rewards));
  }

  @PutMapping
  public Mono<ResponseEntity<Rewards>> adjustRewards(@Valid @RequestBody PointsAdjustmentRequest request) {
    return rewardsService.adjustRewards(request.getUsername(), request.getPoints(), request.getOperation())
            .map(rewards -> ResponseEntity.ok(rewards));
  }
}
