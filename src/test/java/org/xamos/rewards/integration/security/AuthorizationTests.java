package org.xamos.rewards.integration.security;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Tag("integration")
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class AuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/rewards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWhenValidTokenAndRegisteredApp() throws Exception {
        mockJwt("user123", "test-client-id");

        mockMvc.perform(get("/rewards").header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenValidTokenButNotRegisteredApp() throws Exception {
        mockJwt("user123", "unregistered-app");

        mockMvc.perform(get("/rewards").header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWhenValidM2MTokenAndRegisteredApp() throws Exception {
        mockJwt("test-client-id", null);

        mockMvc.perform(get("/rewards").header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenValidM2MTokenButNotRegisteredApp() throws Exception {
        mockJwt("unregistered-app", null);

        mockMvc.perform(get("/rewards").header("Authorization", "Bearer token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessToActuatorWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    private void mockJwt(String sub, String azp) {
        String context = (azp != null) ? "user" : "application";
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(sub)
                .claim("https://api.xamos.org/identity_context", context)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600));
        
        if (azp != null) builder.claim("azp", azp);
        
        when(jwtDecoder.decode(anyString())).thenReturn(builder.build());
    }
}
