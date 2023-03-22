package ro.dental.clinic.model;

import lombok.Data;

@Data
public class KeycloakUserCredentials {

    private String type;
    private String value;
}
