package org.xamos.rewards.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xamos.rewards.exceptions.ApplicationClientIdNotFoundException;
import org.xamos.rewards.exceptions.ApplicationIdNotFoundException;
import org.xamos.rewards.models.Application;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;

  public Application getApplicationById(Long id) {
    return applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationIdNotFoundException(id));
  }

  public List<Application> getApplications() {
    return applicationRepository.findAll();
  }

  public Application getApplicationByClientId(String clientId) {
    return applicationRepository.findByClientId(clientId)
            .orElseThrow(() -> new ApplicationClientIdNotFoundException(clientId));
  }

  public Application registerApplication(Application application) {
    return applicationRepository.save(application);
  }

  public void deleteApplication(Long id) {
    applicationRepository.deleteById(id);
  }

  @Transactional
  public Application updateApplication(Long id, String name) {
    Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationIdNotFoundException(id));
    app.setName(name);
    return applicationRepository.save(app);
  }
}
