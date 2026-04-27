package org.xamos.rewards.exceptions;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

public class InactiveApplicationException extends OAuth2AuthenticationException {
    public InactiveApplicationException(String clientId) {
        super(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, 
                "Application is inactive and cannot perform operations: " + clientId, null));
    }
}
