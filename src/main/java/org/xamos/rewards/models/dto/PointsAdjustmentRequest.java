package org.xamos.rewards.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointsAdjustmentRequest {

  public enum Operation {
    ADD,
    DEDUCT
  }

  @NotBlank
  private String username;

  @Positive
  private Long points;

  @NotNull
  private Operation operation;
}
