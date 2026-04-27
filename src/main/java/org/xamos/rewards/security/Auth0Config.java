package org.xamos.rewards.security;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementApi;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class Auth0Config {
    @Value("${app.security.auth0.domain}")
    private String domain;

    @Value("${app.security.auth0.management.client-id}")
    private String clientId;

    @Value("${app.security.auth0.management.client-secret}")
    private String clientSecret;

    @Bean
    public OkHttpClient auth0OkHttpClient() {
        return new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(Executors.newVirtualThreadPerTaskExecutor()))
                .build();
    }

    @Bean
    public AuthAPI authApi() {
        return AuthAPI.newBuilder(domain, clientId, clientSecret).build();
    }

    @Bean
    public ManagementApi managementApi(OkHttpClient okHttpClient) {
        return ManagementApi.builder()
                .domain(domain)
                .clientCredentials(clientId, clientSecret)
                .httpClient(okHttpClient)
                .build();
    }
}
