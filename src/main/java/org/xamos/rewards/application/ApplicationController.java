package org.xamos.rewards.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.models.dto.RegisterApplicationRequest;
import org.xamos.rewards.models.dto.UpdateApplicationRequest;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/applications")
@AllArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @GetMapping
  public ResponseEntity<List<Application>> getApplications() {
    return ResponseEntity.ok(applicationService.getApplications());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Application> getApplicationById(@Valid @Positive @PathVariable Long id) {
    return ResponseEntity.ok(applicationService.getApplicationById(id));
  }

  @GetMapping("/client/{clientId}")
  public ResponseEntity<Application> getApplicationByClientId(@Valid @NotBlank @PathVariable String clientId) {
    return ResponseEntity.ok(applicationService.getApplicationByClientId(clientId));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_create:application')")
  public ResponseEntity<Application> registerApplication(@Valid @RequestBody RegisterApplicationRequest request) {
    return ResponseEntity.ok(applicationService.registerApplication(request.toApplication()));
  }

  @PutMapping
  public ResponseEntity<Application> updateApplication(@Valid @RequestBody UpdateApplicationRequest request) {
    return ResponseEntity.ok(applicationService.updateApplication(request.getId(), request.getName()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteApplication(@Valid @Positive @PathVariable Long id) {
    applicationService.deleteApplication(id);
    return ResponseEntity.accepted().build();
  }
}
