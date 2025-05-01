package org.xamos.rewards.application;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xamos.rewards.models.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        .map(application -> ResponseEntity.ok(application));
  }

  @GetMapping("/client/{clientId}")
  public Mono<ResponseEntity<Application>> getApplicationByClientId(@PathVariable String clientId) {
    return applicationService.getApplicationByClientId(clientId)
        .map(application -> ResponseEntity.ok(application));
  }

  @PostMapping
  public Mono<ResponseEntity<Application>> registerApplication(@RequestBody Application application) {
    return applicationService.registerApplication(application)
        .map(app -> ResponseEntity.ok(app));
  }

  @PutMapping
  public Mono<ResponseEntity<Application>> updateApplication(@RequestBody Application application) {
    return applicationService.updateApplication(application)
            .map(app -> ResponseEntity.ok(app));
  }

  @DeleteMapping("/{id}")
  public Mono<ResponseEntity<Void>> deleteApplication(@PathVariable Long id) {
    return applicationService.deleteApplication(id)
        .then(Mono.just(ResponseEntity.ok().build()));
  }
}
