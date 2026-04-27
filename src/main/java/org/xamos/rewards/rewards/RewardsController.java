package org.xamos.rewards.rewards;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xamos.rewards.models.Rewards;
import org.xamos.rewards.models.dto.PointsAdjustmentRequest;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/rewards")
@AllArgsConstructor
public class RewardsController {

  private final RewardsService rewardsService;

  @GetMapping
  public ResponseEntity<List<Rewards>> getRewards() {
    return ResponseEntity.ok(rewardsService.getRewards());
  }

  @GetMapping("/{username}")
  public ResponseEntity<Rewards> getRewards(@Valid @NotBlank @PathVariable String username) {
    return ResponseEntity.ok(rewardsService.getRewards(username));
  }

  @PutMapping
  public ResponseEntity<Rewards> adjustRewards(Authentication auth, @Valid @RequestBody PointsAdjustmentRequest request) {
    String username = auth.getName();
    return ResponseEntity.ok(rewardsService.adjustRewards(username, request.getPoints(), request.getOperation()));
  }
}
