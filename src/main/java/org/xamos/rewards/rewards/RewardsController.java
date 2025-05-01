package org.xamos.rewards.rewards;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xamos.rewards.models.Rewards;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

  @PostMapping({"/{username}/add/{points}"})
  public Mono<ResponseEntity<Rewards>> addRewards(@PathVariable String username, @PathVariable Long points) {
    return rewardsService.addRewards(username, points)
        .map(rewards -> ResponseEntity.ok(rewards));
  }

  @PostMapping({"/{username}/deduct/{points}"})
  public Mono<ResponseEntity<Rewards>> deductRewards(@PathVariable String username, @PathVariable Long points) {
    return rewardsService.deductRewards(username, points)
        .map(rewards -> ResponseEntity.ok(rewards));
  }
}
