package org.xamos.rewards.application;

import com.auth0.client.mgmt.ClientsEntity;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.client.Client;
import com.auth0.net.Request;
import com.auth0.net.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xamos.rewards.models.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTests {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ManagementAPI managementApi;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void registerApplicationShouldCreateAuth0ClientAndSaveLocally() throws Exception {
        String appName = "New App";
        String generatedClientId = "auth0-client-123";
        
        ClientsEntity clientsEntity = mock(ClientsEntity.class);
        Request<Client> createRequest = mock(Request.class);
        Response<Client> createResponse = mock(Response.class);
        
        Client createdClient = mock(Client.class);
        when(createdClient.getClientId()).thenReturn(generatedClientId);
        
        when(managementApi.clients()).thenReturn(clientsEntity);
        when(clientsEntity.create(any(Client.class))).thenReturn(createRequest);
        when(createRequest.execute()).thenReturn(createResponse);
        when(createResponse.getBody()).thenReturn(createdClient);

        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setId(1L);
            return app;
        });

        Application result = applicationService.registerApplication(new Application(null, appName, null));

        assertThat(result.getClientId()).isEqualTo(generatedClientId);
        assertThat(result.getName()).isEqualTo(appName);
        
        verify(clientsEntity).create(argThat(client -> appName.equals(client.getName())));
        verify(applicationRepository).save(argThat(app -> generatedClientId.equals(app.getClientId())));
    }
}
