package org.xamos.rewards.security;

import com.auth0.client.mgmt.ClientsClient;
import com.auth0.client.mgmt.ManagementApi;
import com.auth0.client.mgmt.types.ClientAppTypeEnum;
import com.auth0.client.mgmt.types.CreateClientRequestContent;
import com.auth0.client.mgmt.types.CreateClientResponseContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xamos.rewards.exceptions.Auth0ManagementException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Auth0ManagementServiceTests {

    @Mock
    private ManagementApi managementApi;

    @Mock
    private ClientsClient clientsClient;

    @Mock
    private CreateClientResponseContent responseContent;

    @InjectMocks
    private Auth0ManagementService service;

    @BeforeEach
    void setUp() {
        when(managementApi.clients()).thenReturn(clientsClient);
    }

    @Test
    void shouldCreateClient() {
        ArgumentCaptor<CreateClientRequestContent> requestCaptor = 
                ArgumentCaptor.forClass(CreateClientRequestContent.class);
        
        when(clientsClient.create(requestCaptor.capture())).thenReturn(responseContent);
        when(responseContent.getClientId()).thenReturn(Optional.of("test-client-id"));

        String clientId = service.createClient("Test App");

        assertThat(clientId).isEqualTo("test-client-id");
        
        CreateClientRequestContent capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getName()).isEqualTo("Test App");
        assertThat(capturedRequest.getAppType().get()).isEqualTo(ClientAppTypeEnum.NON_INTERACTIVE);
    }

    @Test
    void shouldThrowExceptionWhenAuth0Fails() {
        when(clientsClient.create(any())).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> service.createClient("Fail App"))
                .isInstanceOf(Auth0ManagementException.class)
                .hasMessage("Failed to create Auth0 client")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsMissing() {
        when(clientsClient.create(any())).thenReturn(responseContent);
        when(responseContent.getClientId()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createClient("No ID App"))
                .isInstanceOf(Auth0ManagementException.class)
                .hasMessage("Failed to create Auth0 client")
                .hasCauseInstanceOf(Auth0ManagementException.class);
    }
}
