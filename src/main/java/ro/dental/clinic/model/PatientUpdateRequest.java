package ro.dental.clinic.model;

import lombok.Data;

@Data
public class PatientUpdateRequest {

    private String firstName;
    private String lastName;
    private String allergies;
    private String diseases;
    private String phone;
    private String gender;
    private Long v;

}
