package org.xamos.rewards.security;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.xamos.rewards.application.ApplicationRepository;
import org.xamos.rewards.models.Application;
import org.xamos.rewards.exceptions.ApplicationNotRegisteredException;

import java.util.Collection;

/**
 * Converts a JWT into a RewardsAuthenticationToken by extracting identity claims
 * and verifying the application against the local database.
 */
@Component
@AllArgsConstructor
public class RewardsAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ApplicationRepository applicationRepository;
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extract standard authorities (scopes) from the JWT
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        
        String azp = jwt.getClaimAsString("azp");
        String sub = jwt.getSubject();
        String clientId;
        boolean userMediated;

        if (azp != null && !azp.isBlank()) {
            clientId = azp;
            // If azp is present but equals sub, the application is acting as itself (M2M)
            userMediated = !azp.equals(sub);
        } else {
            // Standard M2M flow where azp is missing
            clientId = sub;
            userMediated = false;
        }

        // Verify that the application is registered in our local system
        Application application = applicationRepository.findByClientId(clientId)
                .orElseThrow(() -> new ApplicationNotRegisteredException(clientId));

        return new RewardsAuthenticationToken(jwt, authorities, application, userMediated);
    }
}
