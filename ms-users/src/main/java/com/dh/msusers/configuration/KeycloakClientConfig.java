package com.dh.msusers.configuration;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dh.keycloak")
public class KeycloakClientConfig {

    @Value("${dh.keycloak.server-url}")
    private String serverurl;
    @Value("${dh.keycloak.realm}")
    private String realm;
    @Value("${dh.keycloak.client-id}")
    private String clientid;
    @Value("${dh.keycloak.client-secret}")
    private String clientsecret;

    @Bean
    public Keycloak getInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverurl)
                .realm(realm)
                .clientId(clientid)
                .clientSecret(clientsecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

}
