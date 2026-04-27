package org.xamos.rewards.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.xamos.rewards.application.ApplicationController;
import org.xamos.rewards.application.ApplicationRepository;
import org.xamos.rewards.application.ApplicationService;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.rewards.RewardsController;
import org.xamos.rewards.rewards.RewardsService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("unit")
@Import({SecurityConfig.class, SecurityProperties.class})
@WebMvcTest(controllers = {RewardsController.class, ApplicationController.class})
public class AuthorizationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private RewardsAuthenticationConverter rewardsAuthenticationConverter;

    @MockitoBean
    private RewardsService rewardsService;

    @MockitoBean
    private ApplicationRepository applicationRepository;

    @MockitoBean
    private ApplicationService applicationService;

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/rewards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWhenAuthenticatedByConverter() throws Exception {
        Application app = new Application(1L, "Test App", "client-id", true, "owner");
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("client-id")
                .build();
        
        RewardsAuthenticationToken authToken = new RewardsAuthenticationToken(jwt, Collections.emptyList(), app, true);

        when(rewardsAuthenticationConverter.convert(any())).thenReturn(authToken);

        mockMvc.perform(get("/rewards").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowRewardsAdjustmentForAuthenticatedUser() throws Exception {
        Application app = new Application(1L, "Test App", "client-id", true, "owner");
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("auth0|user123")
                .build();

        RewardsAuthenticationToken authToken = new RewardsAuthenticationToken(jwt, Collections.emptyList(), app, true);
        when(rewardsAuthenticationConverter.convert(any())).thenReturn(authToken);

        mockMvc.perform(put("/rewards")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"points\": 10, \"operation\": \"ADD\"}"))
                .andExpect(status().isOk());
    }
}
