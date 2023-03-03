package ro.dental.clinic.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ro.axon.dot.keycloak")
public class KeycloakProperties {

    private KeycloakApiProperties api;
    private KeycloakRealm userRealm;

    @Data
    public static class KeycloakRealm {

        private String name;
        private String clientId;
        private String clientSecret;
        private String grantType;
        private KeycloakUser techUser;
        private int tokenToleranceInSeconds;
    }

    @Data
    public static class KeycloakUser {

        private String username;
        private String password;
    }
}
