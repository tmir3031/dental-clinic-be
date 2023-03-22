package ro.dental.clinic.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentCreationRequest {
    private String hour;
    private LocalDate date;
    private String doctorId;
}
