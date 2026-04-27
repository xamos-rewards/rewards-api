package org.xamos.rewards.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationRequest {
  @Positive private Long id;
  @NotBlank private String name;
  @NotNull private Boolean isActive;
}
