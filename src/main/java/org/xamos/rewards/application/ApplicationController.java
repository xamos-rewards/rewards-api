package org.xamos.rewards.application;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.models.dto.RegisterApplicationRequest;
import org.xamos.rewards.models.dto.UpdateApplicationRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/applications")
@AllArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;

  @GetMapping
  public ResponseEntity<Flux<Application>> getApplications() {
    return ResponseEntity.ok(applicationService.getApplications());
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<Application>> getApplicationById(@PathVariable Long id) {
    return applicationService.getApplicationById(id)
        .map(application -> ResponseEntity.ok(application))
        .switchIfEmpty(Mono.defer(() -> Mono.just(ResponseEntity.notFound().build())));
  }

  @GetMapping("/client/{clientId}")
  public Mono<ResponseEntity<Application>> getApplicationByClientId(@PathVariable String clientId) {
    return applicationService.getApplicationByClientId(clientId)
        .map(application -> ResponseEntity.ok(application))
        .switchIfEmpty(Mono.defer(() -> Mono.just(ResponseEntity.notFound().build())));
  }

  @PostMapping
  public Mono<ResponseEntity<Application>> registerApplication(@Valid @RequestBody RegisterApplicationRequest request) {
    return applicationService.registerApplication(request.toApplication())
        .map(app -> ResponseEntity.ok(app));
  }

  @PutMapping
  public Mono<ResponseEntity<Application>> updateApplication(@Valid @RequestBody UpdateApplicationRequest request) {
    return applicationService.updateApplication(request.getId(), request.getName())
            .map(app -> ResponseEntity.ok(app));
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteApplication(@PathVariable Long id) {
    return applicationService.deleteApplication(id)
        .then(Mono.just(ResponseEntity.accepted().build()));
  }
}
