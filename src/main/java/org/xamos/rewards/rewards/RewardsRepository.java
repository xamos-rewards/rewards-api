package org.xamos.rewards.rewards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.xamos.rewards.models.Rewards;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, String> {
}
