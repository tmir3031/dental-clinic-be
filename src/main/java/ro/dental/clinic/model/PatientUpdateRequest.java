package ro.dental.clinic.model;

import lombok.Data;

@Data
public class PatientUpdateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Long v;
}
