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
    void registerApplicationShouldCreateAuth0ClientAndSaveLocally() {
        String appName = "New App";
        String generatedClientId = "auth0-client-123";
        
        when(auth0ManagementService.createClient(appName)).thenReturn(generatedClientId);

        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setId(1L);
            return app;
        });

        Application result = applicationService.registerApplication(new Application(null, appName, null));

        assertThat(result.getClientId()).isEqualTo(generatedClientId);
        assertThat(result.getName()).isEqualTo(appName);
        
        verify(auth0ManagementService).createClient(appName);
        verify(applicationRepository).save(any(Application.class));
    }
}
