package org.xamos.rewards.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xamos.rewards.exceptions.ApplicationClientIdNotFoundException;
import org.xamos.rewards.exceptions.ApplicationIdNotFoundException;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.security.Auth0ManagementService;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ApplicationService {

  private final ApplicationRepository applicationRepository;
  private final Auth0ManagementService auth0ManagementService;

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

  public Application registerApplication(Application application, String ownerId) {
    String clientId = auth0ManagementService.createClient(application.getName());
    application.setClientId(clientId);
    application.setOwnerId(ownerId);
    application.setActive(false);
    return applicationRepository.save(application);
  }

  @Transactional
  public Application updateApplication(Long id, String name, Boolean isActive) {
    Application app = applicationRepository.findById(id)
            .orElseThrow(() -> new ApplicationIdNotFoundException(id));
    app.setName(name);
    app.setActive(isActive);
    return applicationRepository.save(app);
  }

  public void deleteApplication(Long id) {
    if (!applicationRepository.existsById(id)) {
      throw new ApplicationIdNotFoundException(id);
    }
    applicationRepository.deleteById(id);
  }
}
