package org.xamos.rewards.application;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.xamos.rewards.models.Application;
import reactor.core.publisher.Mono;

@Repository
public interface ApplicationRepository extends R2dbcRepository<Application, Long>  {
  Mono<Application> findByClientId(String clientId);
}
