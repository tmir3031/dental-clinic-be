package ro.dental.clinic.model;

import lombok.Data;

@Data
public class KeycloakLoginRequest {

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String realm;
    private String refreshToken;
    private KeycloakGrantType grantType;

}
