package ro.dental.clinic.config;

import lombok.Data;

@Data
public class KeycloakApiProperties {

    private String baseUrl;
    private KeycloakEndpoint users;
    private KeycloakEndpoint createAdminAccessToken;
    private KeycloakEndpoint userRealmRoles;
    private KeycloakEndpoint realmRoles;
    private KeycloakEndpoint createUserAccessToken;
    private KeycloakEndpoint login;
    private KeycloakEndpoint logout;

    @Data
    public static class KeycloakEndpoint {

        private String path;
    }
}