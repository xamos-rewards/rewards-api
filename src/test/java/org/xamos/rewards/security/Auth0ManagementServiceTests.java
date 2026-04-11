package org.xamos.rewards.security;

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
import org.xamos.rewards.exceptions.Auth0ManagementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Auth0ManagementServiceTests {

    @Mock
    private ManagementAPI managementApi;

    @InjectMocks
    private Auth0ManagementService auth0ManagementService;

    @Test
    void shouldCreateAuth0ClientSuccessfully() throws Exception {
        String appName = "Test Application";
        String generatedClientId = "auth0-id-123";

        ClientsEntity clientsEntity = mock(ClientsEntity.class);
        Request<Client> createRequest = mock(Request.class);
        Response<Client> createResponse = mock(Response.class);
        Client createdClient = mock(Client.class);

        when(managementApi.clients()).thenReturn(clientsEntity);
        when(clientsEntity.create(any(Client.class))).thenReturn(createRequest);
        when(createRequest.execute()).thenReturn(createResponse);
        when(createResponse.getBody()).thenReturn(createdClient);
        when(createdClient.getClientId()).thenReturn(generatedClientId);

        String result = auth0ManagementService.createClient(appName);

        assertThat(result).isEqualTo(generatedClientId);
        verify(clientsEntity).create(argThat(client -> 
            appName.equals(client.getName()) && "non_interactive".equals(client.getAppType())
        ));
    }

    @Test
    void shouldThrowExceptionWhenCreationFails() throws Exception {
        when(managementApi.clients()).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> auth0ManagementService.createClient("Test App"))
                .isInstanceOf(Auth0ManagementException.class)
                .hasMessage("Failed to create Auth0 client");
    }
}
