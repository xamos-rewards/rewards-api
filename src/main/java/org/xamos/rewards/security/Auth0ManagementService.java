package org.xamos.rewards.security;

import com.auth0.client.mgmt.ManagementApi;
import com.auth0.client.mgmt.types.ClientAppTypeEnum;
import com.auth0.client.mgmt.types.CreateClientRequestContent;
import com.auth0.client.mgmt.types.CreateClientResponseContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xamos.rewards.exceptions.Auth0ManagementException;

@Slf4j
@Service
@AllArgsConstructor
public class Auth0ManagementService {

    private final ManagementApi managementApi;

    /**
     * Creates a new Non-Interactive (M2M) client in Auth0.
     *
     * @param name the name of the application
     * @return the generated client_id from Auth0
     * @throws Auth0ManagementException if the creation fails
     */
    public String createClient(String name) {
        try {
            CreateClientRequestContent request = CreateClientRequestContent.builder()
                    .name(name)
                    .appType(ClientAppTypeEnum.NON_INTERACTIVE)
                    .build();

            CreateClientResponseContent createdClient = managementApi.clients().create(request);
            return createdClient.getClientId()
                    .orElseThrow(() -> new Auth0ManagementException("Created client did not return a Client ID"));
        } catch (Exception e) {
            log.error("Failed to create Auth0 client: {}", name, e);
            throw new Auth0ManagementException("Failed to create Auth0 client", e);
        }
    }
}
