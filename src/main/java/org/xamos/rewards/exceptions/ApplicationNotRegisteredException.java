package org.xamos.rewards.exceptions;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

public class ApplicationNotRegisteredException extends OAuth2AuthenticationException {

    public ApplicationNotRegisteredException(String clientId) {
        super(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, 
                "Application not registered in Rewards System: " + clientId, null));
    }
}
