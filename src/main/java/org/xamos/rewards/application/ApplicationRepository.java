package org.xamos.rewards.application;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xamos.rewards.models.Application;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long>  {
  Optional<Application> findByClientId(String clientId);
}
