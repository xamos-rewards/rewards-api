package org.xamos.rewards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"unit"})
class RewardsApiApplicationTests {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void contextLoads() {
  }

  @Test
  void testDatabaseConnection() {
    Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM applications", Integer.class);
    assertThat(count).isEqualTo(1);
  }
}
