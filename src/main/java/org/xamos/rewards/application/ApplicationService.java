package org.xamos.rewards.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xamos.rewards.models.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public Mono<Application> getApplicationById(Long id) {
    return applicationRepository.findById(id).log();
  }

  public Flux<Application> getApplications() {
    return applicationRepository.findAll().log();
  }

  public Mono<Application> getApplicationByClientId(String clientId) {
    return applicationRepository.findByClientId(clientId).log();
  }

  public Mono<Application> registerApplication(Application application) {
    return applicationRepository.save(application).log();
  }

  public Mono<Void> deleteApplication(Long id) {
    return applicationRepository.deleteById(id).log();
  }

  public Mono<Application> updateApplication(Application application) {
    return applicationRepository.save(application).log();
  }
}
