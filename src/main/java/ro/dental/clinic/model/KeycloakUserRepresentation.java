package ro.dental.clinic.model;

import lombok.Data;

import java.util.List;

@Data
public class KeycloakUserRepresentation {

    private String username;
    private Boolean enabled;
    private List<KeycloakUserCredentials> credentials;
}
