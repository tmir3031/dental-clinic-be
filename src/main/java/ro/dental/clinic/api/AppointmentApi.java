package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.AppointmentDetailsList;
import ro.dental.clinic.service.AppointmentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointments")
public class AppointmentApi {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<AppointmentDetailsList> getAppointmentsDetailsList(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsDetails(date, search));
    }

    @GetMapping("/free-time-slot/")
    public ResponseEntity<List<String>> getFreeTimeSlotForADay(
            @RequestParam(value = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "doctorId", required = false) String doctorId,
            @RequestParam(value = "specializationId", required = false) Long specializationId) {
        return ResponseEntity.ok(
                appointmentService.getFreeTimeSlotForADay(date, doctorId, specializationId));
    }

}
