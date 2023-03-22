package ro.dental.clinic.model;

import lombok.Data;

@Data
public class UserCreationRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
}
