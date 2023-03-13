package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.AppointmentDetailsList;
import ro.dental.clinic.service.AppointmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/requests")
public class AppointmentApi {

    private final AppointmentService appointmentService;

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})
    public ResponseEntity<AppointmentDetailsList> getLeaveRequestDetailsList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsDetails(status, search));
    }

}
