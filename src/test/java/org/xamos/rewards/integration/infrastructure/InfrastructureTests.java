package org.xamos.rewards.integration.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("integration")
@ActiveProfiles({"integration"})
@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfrastructureTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void verifyVirtualThreadsInUse() {
        String threadInfo = restTemplate.getForObject("/internal/thread-info", String.class);

        log.info("Request handled by: {}", threadInfo);

        assertThat(threadInfo).containsIgnoringCase("VirtualThread");
    }
}
