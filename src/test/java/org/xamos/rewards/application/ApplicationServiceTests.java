package org.xamos.rewards.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.security.Auth0ManagementService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTests {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private Auth0ManagementService auth0ManagementService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void registerApplicationShouldCreateAuth0Client() {
        String appName = "New App";
        String ownerId = "auth0|owner";
        String generatedClientId = "auth0-client-123";
        
        when(auth0ManagementService.createClient(appName)).thenReturn(generatedClientId);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        Application app = new Application();
        app.setName(appName);
        Application result = applicationService.registerApplication(app, ownerId);

        assertThat(result.getClientId()).isEqualTo(generatedClientId);
        assertThat(result.getOwnerId()).isEqualTo(ownerId);
        assertThat(result.isActive()).isFalse();

        verify(auth0ManagementService).createClient(appName);
        verify(applicationRepository).save(any(Application.class));
    }
}
