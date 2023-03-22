package ro.dental.clinic.model;

import lombok.Data;

@Data
public class LoginResponseUserDetails {

    private String role;
    private String username;
    private String userId;

}