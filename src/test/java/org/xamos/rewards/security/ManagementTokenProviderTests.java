package org.xamos.rewards.security;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagementTokenProviderTests {

    @Mock
    private AuthAPI authApi;

    @Mock
    private ManagementAPI managementApi;

    @InjectMocks
    private ManagementTokenProvider provider;

    @Test
    void shouldFetchAndSetNewToken() throws Exception {
        TokenRequest request = mock(TokenRequest.class);
        Response<TokenHolder> response = mock(Response.class);
        TokenHolder holder = mock(TokenHolder.class);
        
        when(authApi.requestToken(any())).thenReturn(request);
        when(request.execute()).thenReturn(response);
        when(response.getBody()).thenReturn(holder);
        when(holder.getAccessToken()).thenReturn("new-token");
        when(holder.getExpiresIn()).thenReturn(3600L);

        provider.ensureValidToken(managementApi);

        verify(managementApi).setApiToken("new-token");
    }

    @Test
    void shouldReuseCachedToken() throws Exception {
        TokenRequest request = mock(TokenRequest.class);
        Response<TokenHolder> response = mock(Response.class);
        TokenHolder holder = mock(TokenHolder.class);
        
        when(authApi.requestToken(any())).thenReturn(request);
        when(request.execute()).thenReturn(response);
        when(response.getBody()).thenReturn(holder);
        when(holder.getAccessToken()).thenReturn("new-token");
        when(holder.getExpiresIn()).thenReturn(3600L);

        provider.ensureValidToken(managementApi);
        provider.ensureValidToken(managementApi);

        // Should only fetch once
        verify(authApi, times(1)).requestToken(any());
        verify(managementApi, times(1)).setApiToken("new-token");
    }

    @Test
    void shouldRefreshTokenWhenExpired() throws Exception {
        TokenRequest request = mock(TokenRequest.class);
        Response<TokenHolder> response = mock(Response.class);
        TokenHolder holder = mock(TokenHolder.class);
        
        when(authApi.requestToken(any())).thenReturn(request);
        when(request.execute()).thenReturn(response);
        when(response.getBody()).thenReturn(holder);
        when(holder.getAccessToken()).thenReturn("new-token", "newer-token");
        
        // Return 0 so it expires instantly based on the minusSeconds(300) buffer
        when(holder.getExpiresIn()).thenReturn(0L);

        provider.ensureValidToken(managementApi);
        
        // The second call should instantly see it as expired and refresh
        provider.ensureValidToken(managementApi);

        // Should fetch twice
        verify(authApi, times(2)).requestToken(any());
    }
}
