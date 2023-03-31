package ro.dental.clinic.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientCreationRequest {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String gender;
    private String chronicDiseases;
    private String allergies;
    private LocalDate dateOfBirth;
    private String phone;

}

