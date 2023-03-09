package ro.dental.clinic.model;

import lombok.Data;

@Data
public class LogoutRequest {

    private String refreshToken;
}
