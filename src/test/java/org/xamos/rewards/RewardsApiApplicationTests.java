package org.xamos.rewards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles({"unit-test"})
class RewardsApiApplicationTests {

  @Autowired
  private DatabaseClient databaseClient;

  @Test
  void contextLoads() {
  }

  @Test
  void testDatabaseConnection() {
    this.databaseClient.sql("SELECT * FROM applications")
        .map(row -> row.get("name", String.class))
        .all()
        .as(StepVerifier::create)
        .expectNextCount(1)
        .verifyComplete();
  }
}
