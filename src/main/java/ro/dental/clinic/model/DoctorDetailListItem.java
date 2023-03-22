package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.enums.UserStatus;

import java.time.Instant;
import java.util.List;

@Data
public class DoctorDetailListItem {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
//    private String crtUsr;
//    private Instant crtTms;
//    private String mdfUsr;
//    private Instant mdfTms;
    private String role;
    private UserStatus status;
    private Long v;
    private String description;
    private List<SpecializationDetailsListItem> specializationDetailsListItem;
}
