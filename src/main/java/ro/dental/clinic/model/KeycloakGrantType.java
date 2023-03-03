package ro.dental.clinic.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum KeycloakGrantType {

    REFRESH_TOKEN("refresh_token"),
    PASSWORD("password");

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

}
