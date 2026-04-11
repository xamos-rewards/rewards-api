package org.xamos.rewards.integration.security;

import com.auth0.client.mgmt.ManagementAPI;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xamos.rewards.security.ManagementTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("integration")
class ManagementApiTokenAspectIntegrationTests {

    @Autowired
    private ManagementAPI managementApi;

    @MockitoBean
    private ManagementTokenProvider tokenProvider;

    @Test
    void shouldTriggerAspectOnCommonManagementApiMethods() {
        managementApi.clients();
        verify(tokenProvider, times(1)).ensureValidToken(any(ManagementAPI.class));

        managementApi.users();
        verify(tokenProvider, times(2)).ensureValidToken(any(ManagementAPI.class));

        managementApi.roles();
        verify(tokenProvider, times(3)).ensureValidToken(any(ManagementAPI.class));
    }
}
