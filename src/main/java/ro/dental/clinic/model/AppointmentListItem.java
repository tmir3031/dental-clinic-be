package ro.dental.clinic.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentListItem {
    private Long id;
    private LocalDate date;
    private String hour;
    private EmployeeDetailsListItem doctorDetails;
    private EmployeeDetailsListItem patientDetails;
}
