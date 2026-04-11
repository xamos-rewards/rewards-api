package org.xamos.rewards.security;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.json.mgmt.client.Client;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xamos.rewards.exceptions.Auth0ManagementException;

@Slf4j
@Service
@AllArgsConstructor
public class Auth0ManagementService {

    private final ManagementAPI managementApi;

    /**
     * Creates a new Non-Interactive (M2M) client in Auth0.
     *
     * @param name the name of the application
     * @return the generated client_id from Auth0
     * @throws Auth0ManagementException if the creation fails
     */
    public String createClient(String name) {
        try {
            Client auth0Client = new Client(name);
            auth0Client.setAppType("non_interactive");
            Client createdClient = managementApi.clients().create(auth0Client).execute().getBody();
            return createdClient.getClientId();
        } catch (Exception e) {
            log.error("Failed to create Auth0 client: {}", name, e);
            throw new Auth0ManagementException("Failed to create Auth0 client", e);
        }
    }
}
