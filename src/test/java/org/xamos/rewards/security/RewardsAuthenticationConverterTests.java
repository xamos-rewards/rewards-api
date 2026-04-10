package org.xamos.rewards.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.xamos.rewards.application.ApplicationRepository;
import org.xamos.rewards.exceptions.ApplicationNotRegisteredException;
import org.xamos.rewards.models.Application;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsAuthenticationConverterTests {

    @Mock
    private ApplicationRepository applicationRepository;

    private RewardsAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        converter = new RewardsAuthenticationConverter(applicationRepository);
    }

    @Test
    void shouldIdentifyUserMediatedRequestWhenAzpClaimPresent() {
        String clientId = "test-client";
        String user = "auth0|user123";
        Jwt jwt = createJwt(user, clientId);
        Application app = new Application(1L, "Test App", clientId);

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result).isInstanceOf(RewardsAuthenticationToken.class);
        RewardsAuthenticationToken token = (RewardsAuthenticationToken) result;
        assertThat(token.getApplication()).isEqualTo(app);
        assertThat(token.isUserMediated()).isTrue();
        assertThat(token.isServiceRequest()).isFalse();
        assertThat(token.getUser()).isPresent().hasValue(user);
        assertThat(token.getName()).isEqualTo(user);
    }

    @Test
    void shouldIdentifyServiceRequestWhenAzpClaimMissing() {
        String clientId = "test-client";
        Jwt jwt = createJwt(clientId, null);
        Application app = new Application(1L, "Test App", clientId);

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result).isInstanceOf(RewardsAuthenticationToken.class);
        RewardsAuthenticationToken token = (RewardsAuthenticationToken) result;
        assertThat(token.getApplication()).isEqualTo(app);
        assertThat(token.isServiceRequest()).isTrue();
        assertThat(token.isUserMediated()).isFalse();
        assertThat(token.getUser()).isEmpty();
        assertThat(token.getName()).isEqualTo(clientId);
    }

    @Test
    void shouldIdentifyServiceRequestWhenAzpEqualsSub() {
        String clientId = "test-client";
        Jwt jwt = createJwt(clientId, clientId);
        Application app = new Application(1L, "Test App", clientId);

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.of(app));

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertThat(result).isInstanceOf(RewardsAuthenticationToken.class);
        RewardsAuthenticationToken token = (RewardsAuthenticationToken) result;
        assertThat(token.isServiceRequest()).isTrue();
        assertThat(token.isUserMediated()).isFalse();
        assertThat(token.getUser()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenAppNotRegistered() {
        String clientId = "unknown-client";
        Jwt jwt = createJwt("user123", clientId);

        when(applicationRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> converter.convert(jwt))
                .isInstanceOf(ApplicationNotRegisteredException.class)
                .hasMessageContaining(clientId);
    }

    private Jwt createJwt(String sub, String azp) {
        Jwt.Builder builder = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .subject(sub);
        
        if (azp != null) builder.claim("azp", azp);
        
        return builder.build();
    }
}
