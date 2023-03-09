package ro.dental.clinic.model;

import lombok.Data;

@Data
public class KeycloakLogoutRequest {

    private String refreshToken;
    private String clientId;
    private String clientSecret;
    private String realm;
}
