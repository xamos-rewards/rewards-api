package org.xamos.rewards.rewards;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import org.xamos.rewards.models.Rewards;

@Repository
public interface RewardsRepository extends R2dbcRepository<Rewards, String> {
}
