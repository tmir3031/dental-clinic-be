package ro.dental.clinic.model;

import lombok.Data;

@Data
public class UserDetails {

    private String id;
    private String username;
    private String password;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
}
