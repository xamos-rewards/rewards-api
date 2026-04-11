package org.xamos.rewards.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    /**
     * List of Ant-style paths that should be permitted without authentication.
     */
    private List<String> permittedPaths = new ArrayList<>();

    private CorsProperties cors = new CorsProperties();

    @Getter
    @Setter
    public static class CorsProperties {
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
        private List<String> exposedHeaders = new ArrayList<>();
        private boolean allowCredentials;
    }
}
