package com.dh.msbills.runner;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    @Autowired
    private final Keycloak keycloakAdmin;

    private static final String KEYCLOAK_SERVER_URL = "http://localhost:8080";
    private static final String COMPANY_SERVICE_REALM_NAME = "umReino";
    private static final String MS_GATEWAY_CLIENT_ID = "ms-gateway-client";
    private static final String MS_USERS_CLIENT_ID = "ms-users-client";
    private static final String MS_GATEWAY_REDIRECT_URL = "http://localhost:8090/*";
    private static final String MS_USERS_REDIRECT_URL = "http://localhost:8084/*";
    private static final String PROVIDERS = "PROVIDERS";
    private static final String READERS = "READERS";
    private static final List<UserPass> USERS_LIST = Arrays.asList(
            new UserPass("oadmin", "admin"),
            new UserPass("ouser", "user"));

    @Override
    public void run(String... args) {
        log.info("\n\nInitializing '{}' realm in Keycloak ...\n\n", COMPANY_SERVICE_REALM_NAME);
        try {
            Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms()
                    .findAll()
                    .stream()
                    .filter(r -> r.getRealm().equals(COMPANY_SERVICE_REALM_NAME))
                    .findAny();

            if (representationOptional.isPresent()) {
                log.info("\nRemoving already pre-configured '{}' realm\n", COMPANY_SERVICE_REALM_NAME);
                keycloakAdmin.realm(COMPANY_SERVICE_REALM_NAME).remove();
                log.info("\nRealm '{}' removed!\n", COMPANY_SERVICE_REALM_NAME);
            }

            // Realm
            RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(COMPANY_SERVICE_REALM_NAME);
            realmRepresentation.setEnabled(true);
            realmRepresentation.setRegistrationAllowed(true);

            List<GroupRepresentation> groupRepresentations = new ArrayList<>();

            GroupRepresentation providers = new GroupRepresentation();
            providers.setName(PROVIDERS);
            List<String> rolesProvider = new ArrayList<>();
            rolesProvider.add("PROVIDER");
            providers.setRealmRoles(rolesProvider);

            GroupRepresentation readers = new GroupRepresentation();
            readers.setName(READERS);
            List<String> rolesReader = new ArrayList<>();
            rolesReader.add("READER");
            readers.setRealmRoles(rolesReader);

            groupRepresentations.add(providers);
            groupRepresentations.add(readers);
            realmRepresentation.setGroups(groupRepresentations);

            // Client Gateway
            ClientRepresentation clientGatewayRepresentation = new ClientRepresentation();
            clientGatewayRepresentation.setClientId(MS_GATEWAY_CLIENT_ID);
            clientGatewayRepresentation.setDirectAccessGrantsEnabled(true);
            clientGatewayRepresentation.setPublicClient(false);
            clientGatewayRepresentation.setSecret("segredo");
            clientGatewayRepresentation.setRedirectUris(Collections.singletonList(MS_GATEWAY_REDIRECT_URL));
            clientGatewayRepresentation.setBaseUrl(MS_GATEWAY_REDIRECT_URL);

            // Client Users
            ClientRepresentation clientUsersRepresentation = new ClientRepresentation();
            clientUsersRepresentation.setClientId(MS_USERS_CLIENT_ID);
            clientUsersRepresentation.setDirectAccessGrantsEnabled(true);
            clientUsersRepresentation.setPublicClient(false);
            clientUsersRepresentation.setSecret("segredo");
            clientUsersRepresentation.setRedirectUris(Collections.singletonList(MS_USERS_REDIRECT_URL));
            clientUsersRepresentation.setBaseUrl(MS_USERS_REDIRECT_URL);

            List<ClientRepresentation> clients = new ArrayList<>();
            clients.add(clientGatewayRepresentation);
            clients.add(clientUsersRepresentation);
            realmRepresentation.setClients(clients);

            // Users
            List<UserRepresentation> userRepresentations = USERS_LIST.stream()
                    .map(userPass -> {
                        // User Credentials
                        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                        credentialRepresentation.setValue(userPass.getPassword());

                        // User
                        UserRepresentation userRepresentation = new UserRepresentation();
                        userRepresentation.setUsername(userPass.getUsername());
                        userRepresentation.setEnabled(true);
                        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
                        userRepresentation.setGroups(getClientGroups(userPass));
                        Map<String, List<String>> dni = new HashMap<>();
                        List<String> dniList = new ArrayList<>();
                        dniList.add("dni");
                        dni.put("dni", dniList);
                        userRepresentation.setAttributes(dni);
                        return userRepresentation;
                    })
                    .collect(Collectors.toList());
            realmRepresentation.setUsers(userRepresentations);

            // Create Realm
            keycloakAdmin.realms().create(realmRepresentation);

            // Testing
            UserPass admin = USERS_LIST.get(0);
            log.info("Testing getting token for '{}' ...", admin.getUsername());

            Keycloak keycloakGatewayApp = KeycloakBuilder.builder().serverUrl(KEYCLOAK_SERVER_URL)
                    .realm(COMPANY_SERVICE_REALM_NAME).clientSecret("segredo").username(admin.getUsername()).password(admin.getPassword())
                    .clientId(MS_GATEWAY_CLIENT_ID).build();

            log.info("\n'{}' token: {}\n", admin.getUsername(), keycloakGatewayApp.tokenManager().grantToken().getToken());
            log.info("\n'{}' initialization completed successfully!\n", COMPANY_SERVICE_REALM_NAME);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private List<String> getClientGroups(UserPass userPass) {

        List<String> groups = new ArrayList<>();

        if ("admin01".equals(userPass.getUsername())) {
            groups.add(PROVIDERS);
        } else {
            groups.add(READERS);
        }
        return groups;
    }

    @Value
    private static class UserPass {
        String username;
        String password;
    }

}
