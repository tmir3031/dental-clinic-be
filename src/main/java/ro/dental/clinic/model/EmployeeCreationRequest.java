package ro.dental.clinic.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeCreationRequest {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String description;
    private Long specializationId;
}
