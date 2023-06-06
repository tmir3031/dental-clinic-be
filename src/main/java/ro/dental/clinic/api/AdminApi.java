package ro.dental.clinic.api;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.AppointmentList;
import ro.dental.clinic.model.UserAccountList;
import ro.dental.clinic.service.AdminService;
import ro.dental.clinic.service.AppointmentService;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AdminApi {

    private final AdminService adminService;

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<UserAccountList> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/all-app")
    public ResponseEntity<AppointmentList> getAllAppointments(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                              @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                              @RequestParam(value = "search", required = false) String search) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(startDate,endDate, search));
    }
}