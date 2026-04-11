package org.xamos.rewards.security;

import com.auth0.client.mgmt.ManagementAPI;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManagementApiTokenAspectTests {

    @Mock
    private ManagementTokenProvider tokenProvider;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ManagementAPI managementApi;

    @InjectMocks
    private ManagementApiTokenAspect aspect;

    @Test
    void shouldEnsureValidTokenBeforeExecution() {
        when(joinPoint.getTarget()).thenReturn(managementApi);
        aspect.ensureTokenValid(joinPoint);
        verify(tokenProvider).ensureValidToken(managementApi);
    }
}
