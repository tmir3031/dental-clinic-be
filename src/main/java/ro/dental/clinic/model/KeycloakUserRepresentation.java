package ro.dental.clinic.model;

import lombok.Data;

import java.util.List;

@Data
public class KeycloakUserRepresentation {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private List<KeycloakUserCredentials> credentials;
}
