package org.xamos.rewards;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.xamos.rewards.infrastructure.TestSecurityConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
@ActiveProfiles({"integration"})
@AutoConfigureTestRestTemplate
@Import(TestSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RewardsApiIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void verifyVirtualThreadsInUse() {
        String threadInfo = restTemplate.getForObject("/internal/thread-info", String.class);

        log.info("Request handled by: {}", threadInfo);

        assertThat(threadInfo).containsIgnoringCase("VirtualThread");
    }
}
