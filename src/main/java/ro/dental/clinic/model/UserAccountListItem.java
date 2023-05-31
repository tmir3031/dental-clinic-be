package ro.dental.clinic.model;

import lombok.Data;

@Data
public class UserAccountListItem {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String role;
}
