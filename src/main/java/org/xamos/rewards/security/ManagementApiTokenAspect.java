package org.xamos.rewards.security;

import com.auth0.client.mgmt.ManagementAPI;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect that automatically ensures the Auth0 Management API token is valid
 * before any calls are made to the ManagementAPI bean.
 */
@Aspect
@Component
@AllArgsConstructor
public class ManagementApiTokenAspect {

    private final ManagementTokenProvider tokenProvider;

    @Before("execution(* com.auth0.client.mgmt.ManagementAPI.*(..))")
    public void ensureTokenValid(JoinPoint joinPoint) {
        ManagementAPI api = (ManagementAPI) joinPoint.getTarget();
        tokenProvider.ensureValidToken(api);
    }
}
