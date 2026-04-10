package org.xamos.rewards.security;

import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.xamos.rewards.application.ApplicationRepository;
import org.xamos.rewards.exceptions.ApplicationNotRegisteredException;
import org.xamos.rewards.models.Application;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RewardsAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ApplicationRepository applicationRepository;
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Set<GrantedAuthority> authorities = new HashSet<>();
        
        Collection<GrantedAuthority> standardScopes = jwtGrantedAuthoritiesConverter.convert(jwt);
        if (standardScopes != null) {
            authorities.addAll(standardScopes);
        }

        authorities.addAll(extractPermissions(jwt));

        String azp = jwt.getClaimAsString("azp");
        String sub = jwt.getSubject();
        String clientId;
        boolean userMediated;

        if (azp != null && !azp.isBlank()) {
            clientId = azp;
            userMediated = !azp.equals(sub);
        } else {
            clientId = sub;
            userMediated = false;
        }

        Application application = applicationRepository.findByClientId(clientId)
                .orElseThrow(() -> new ApplicationNotRegisteredException(clientId));

        return new RewardsAuthenticationToken(jwt, authorities, application, userMediated);
    }

    private List<SimpleGrantedAuthority> extractPermissions(Jwt jwt) {
        List<String> permissions = jwt.getClaim("permissions");
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("SCOPE_" + permission))
                .collect(Collectors.toList());
    }
}
