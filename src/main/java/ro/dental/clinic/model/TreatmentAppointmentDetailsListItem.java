package ro.dental.clinic.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TreatmentAppointmentDetailsListItem {
    private LocalDate date;
    private String treatment;
    private EmployeeDetailsListItem doctorDetails;
}
