package org.xamos.rewards.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xamos.rewards.models.Application;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterApplicationRequest {

  @NotBlank
  private String name;

  public Application toApplication() {
    Application application = new Application();
    application.setName(name);
    application.setActive(false);
    return application;
  }
}
