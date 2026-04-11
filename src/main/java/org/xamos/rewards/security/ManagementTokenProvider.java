package org.xamos.rewards.security;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.auth.TokenHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class ManagementTokenProvider {

    private final AuthAPI authApi;
    private final String audience;
    
    private final AtomicReference<CachedToken> tokenCache = new AtomicReference<>();
    private final ReentrantLock lock = new ReentrantLock();

    public ManagementTokenProvider(
            AuthAPI authApi, 
            @Value("${app.security.auth0.management.audience}") String audience) {
        this.authApi = authApi;
        this.audience = audience;
    }

    public void ensureValidToken(ManagementAPI api) {
        CachedToken cached = tokenCache.get();
        if (cached != null && !cached.isExpired()) {
            return;
        }

        lock.lock();
        try {
            cached = tokenCache.get();
            if (cached != null && !cached.isExpired()) {
                return;
            }

            log.info("Fetching new Auth0 Management token...");
            TokenHolder holder = authApi.requestToken(audience).execute().getBody();
            
            // Refresh 5 minutes before actual expiration
            Instant expiresAt = Instant.now()
                    .plusSeconds(holder.getExpiresIn())
                    .minusSeconds(300);
            
            CachedToken newToken = new CachedToken(holder.getAccessToken(), expiresAt);
            tokenCache.set(newToken);
            api.setApiToken(newToken.token());
        } catch (Exception e) {
            log.error("Failed to fetch Auth0 Management token", e);
            throw new RuntimeException("Auth0 Token Fetch Failed", e);
        } finally {
            lock.unlock();
        }
    }

    private record CachedToken(String token, Instant expiresAt) {
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
