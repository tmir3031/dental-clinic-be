package ro.dental.clinic.model;

import lombok.Data;

@Data
public class PatientDetailListItem {
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
}
