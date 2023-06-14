package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.enums.UserStatus;

import java.util.List;

@Data
public class DoctorDetailListItem {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String gender;
    private UserStatus status;
    private Long v;
    private String description;
    private List<SpecializationDetailsListItem> specializationDetailsListItem;
}
