package org.xamos.rewards.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.xamos.rewards.models.Application;

import java.util.Collection;
import java.util.Optional;

/**
 * A specialized Authentication token for the Rewards System.
 * It represents the dual identity of a user and service:
 * 1. The User (getUser()), representing the human user initiating the request, if any.
 * 2. The Validated Application (getApplication()), representing the service/application it comes from.
 * 
 * The name is the primary actor, which can be either the user or the service.
 */
public class RewardsAuthenticationToken extends JwtAuthenticationToken {

    @Getter
    private final Application application;
    private final boolean userMediated;

    public RewardsAuthenticationToken(Jwt jwt, 
                                    Collection<? extends GrantedAuthority> authorities, 
                                    Application application, 
                                    boolean userMediated) {
        super(jwt, authorities, jwt.getSubject());
        this.application = application;
        this.userMediated = userMediated;
    }

    /**
     * Returns the User ID if this request was mediated by a human user.
     * Returns Optional.empty() for M2M requests.
     */
    public Optional<String> getUser() {
        return userMediated ? Optional.of(getName()) : Optional.empty();
    }

    /**
     * Returns true if the request is a M2M call with no human user involved.
     */
    public boolean isServiceRequest() {
        return !userMediated;
    }

    /**
     * Returns true if a human user initiated this request through an application.
     */
    public boolean isUserMediated() {
        return userMediated;
    }
}
