package org.xamos.rewards.application;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.xamos.rewards.models.Application;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public Mono<Application> getApplicationById(Long id) {
    return applicationRepository.findById(id);
  }

  public Flux<Application> getApplications() {
    return applicationRepository.findAll();
  }

  public Mono<Application> getApplicationByClientId(String clientId) {
    return applicationRepository.findByClientId(clientId);
  }

  public Mono<Application> registerApplication(Application application) {
    return applicationRepository.save(application);
  }

  public Mono<Void> deleteApplication(Long id) {
    return applicationRepository.deleteById(id);
  }

  public Mono<Application> updateApplication(Application application) {
    return applicationRepository.save(application);
  }
}
