package org.xamos.rewards.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.xamos.rewards.application.ApplicationRepository;
import org.xamos.rewards.exceptions.ApplicationNotRegisteredException;
import org.xamos.rewards.exceptions.InactiveApplicationException;
import org.xamos.rewards.models.Application;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsAuthenticationConverterTests {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private RewardsAuthenticationConverter converter;

    @Test
    void shouldAllowMutationScopesWithActiveAuthorizedParty() {
        String clientId = "test-client";
        Jwt jwt = createJwt("user123", clientId, "user", "read:rewards add:rewards");
        Application app = new Application(1L, "App", clientId, true, "owner");

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        AbstractAuthenticationToken result = converter.convert(jwt);
        
        assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .contains("SCOPE_add:rewards");
    }

    @Test
    void shouldRejectTokenWithInactiveAuthorizedParty() {
        String clientId = "test-client";
        Jwt jwt = createJwt("user123", clientId, "user", "read:rewards add:rewards");
        Application app = new Application(1L, "App", clientId, false, "owner");

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(InactiveApplicationException.class);
    }

    @Test
    void shouldStripMutationScopesWithoutAuthorizedParty() {
        Jwt jwt = createJwt("user123", null, "user", "read:rewards add:rewards");

        AbstractAuthenticationToken result = converter.convert(jwt);
        
        assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .contains("SCOPE_read:rewards")
                .doesNotContain("SCOPE_add:rewards");
    }

    @Test
    void shouldStripMutationScopesForApplicationContextEvenIfActive() {
        String clientId = "m2m-client";
        Jwt jwt = createJwt(clientId + "@clients", null, "application", "read:rewards add:rewards");
        Application app = new Application(1L, "M2M App", clientId, true, "owner");

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        AbstractAuthenticationToken result = converter.convert(jwt);
        
        assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority))
                .contains("SCOPE_read:rewards")
                .doesNotContain("SCOPE_add:rewards");
    }

    @Test
    void shouldRejectApplicationContextIfInactive() {
        String clientId = "m2m-client";
        Jwt jwt = createJwt(clientId + "@clients", null, "application", "read:rewards");
        Application app = new Application(1L, "M2M App", clientId, false, "owner");

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(InactiveApplicationException.class);
    }

    private Jwt createJwt(String sub, String azp, String context, String scopes) {
        Jwt.Builder builder = Jwt.withTokenValue("mock")
                .header("alg", "none")
                .subject(sub)
                .claim("https://api.xamos.org/identity_context", context)
                .claim("scope", scopes);
        if (azp != null) builder.claim("azp", azp);
        return builder.build();
    }
}
