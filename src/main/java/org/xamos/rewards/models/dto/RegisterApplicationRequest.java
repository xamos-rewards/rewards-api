package org.xamos.rewards.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.xamos.rewards.models.Application;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterApplicationRequest {

  @NotBlank
  private String name;

  public Application toApplication() {
    Application application = new Application();
    application.setName(name);

    // TODO: Remove later when we have Auth0 integration
    application.setClientId(UUID.randomUUID().toString());

    return application;
  }
}
