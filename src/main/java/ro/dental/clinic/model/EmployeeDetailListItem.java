package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.enums.EmployeeStatus;

import java.time.Instant;

@Data
public class EmployeeDetailListItem {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String crtUsr;
    private Instant crtTms;
    private String mdfUsr;
    private Instant mdfTms;
    private String role;
    private EmployeeStatus status;

    private Long v;
    private String description;
    private SpecializationDetailsListItem specializationDetailsListItem;
    private String username;
}
