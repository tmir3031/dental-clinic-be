package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.domain.SpecializationEty;

import java.util.List;

@Data
public class DoctorCreationRequest {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String description;
    private String gender;
    private List<SpecializationEty> specializationEtyList;

}

