package ro.dental.clinic.model;

import lombok.Data;
import ro.dental.clinic.enums.AppointmentStatus;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class AppointmentDetailsListItem {
    private Long id;
    private String crtUsr;
    private Instant crtTms;
    private String mdfUsr;
    private Instant mdfTms;
    private LocalDate date;
    private String hour;
    private AppointmentStatus status;
    private String description;
    private String rejectReason;
    private Long v;
    private EmployeeDetailsListItem employeeDetails;

}
