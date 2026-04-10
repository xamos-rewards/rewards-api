package org.xamos.rewards.security;

import com.auth0.client.auth.AuthAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Auth0Config {
    @Value("${app.security.auth0.domain}")
    private String domain;

    @Value("${app.security.auth0.management.client-id}")
    private String clientId;

    @Value("${app.security.auth0.management.client-secret}")
    private String clientSecret;

    @Bean
    public AuthAPI authApi() {
        return AuthAPI.newBuilder(domain, clientId, clientSecret).build();
    }
}
