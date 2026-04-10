package org.xamos.rewards.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.xamos.rewards.application.ApplicationController;
import org.xamos.rewards.application.ApplicationService;
import org.xamos.rewards.rewards.RewardsController;
import org.xamos.rewards.rewards.RewardsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private RewardsService rewardsService;

    @MockitoBean
    private ApplicationService applicationService;

    @Test
    void whenAccessingProtectedEndpointWithoutToken_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/rewards"))
                .andExpect(status().isUnauthorized());
    }
}
