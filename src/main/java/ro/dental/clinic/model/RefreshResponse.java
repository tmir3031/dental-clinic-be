package ro.dental.clinic.model;

import lombok.Data;

@Data
public class RefreshResponse {

    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private Integer refreshTokenExpiresIn;
    private LoginResponseUserDetails userDetails;
}
