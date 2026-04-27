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
import org.xamos.rewards.exceptions.InactiveApplicationException;
import org.xamos.rewards.models.Application;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RewardsAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final ApplicationRepository applicationRepository;
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>(jwtGrantedAuthoritiesConverter.convert(jwt));
        authorities.addAll(extractPermissions(jwt));

        String context = jwt.getClaimAsString("https://api.xamos.org/identity_context");
        String azp = jwt.getClaimAsString("azp");
        String sub = jwt.getSubject();

        Application application = null;
        boolean userContext = "user".equals(context);

        if (!userContext) {
            String clientId = sub.contains("@clients") ? sub.substring(0, sub.indexOf("@clients")) : sub;
            application = applicationRepository.findByClientId(clientId)
                    .orElseThrow(() -> new ApplicationNotRegisteredException(clientId));

            stripMutationScopes(authorities);
        } else if (azp != null) {
            application = applicationRepository.findByClientId(azp)
                    .orElseThrow(() -> new ApplicationNotRegisteredException(azp));
        } else {
            stripMutationScopes(authorities);
        }

        if (application != null && !application.isActive()) {
            throw new InactiveApplicationException(application.getClientId());
        }

        return new RewardsAuthenticationToken(jwt, authorities, application, userContext);
    }

    private void stripMutationScopes(Set<GrantedAuthority> authorities) {
        authorities.removeIf(auth -> auth.getAuthority().startsWith("SCOPE_add:rewards") || 
                                     auth.getAuthority().startsWith("SCOPE_deduct:rewards"));
    }

    private Collection<GrantedAuthority> extractPermissions(Jwt jwt) {
        List<String> permissions = jwt.getClaim("permissions");
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("SCOPE_" + permission))
                .collect(Collectors.toList());
    }
}
