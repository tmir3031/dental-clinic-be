package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.model.DoctorDetailList;
import ro.dental.clinic.service.DoctorService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/doctors")
public class DoctorApi {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<DoctorDetailList> getDoctorDetailsList() {
        return ResponseEntity.ok(doctorService.getDoctorDetails());
    }

//    @PatchMapping("/{doctorId}/appointments/{appointmentId}")
//    @Secured({"ROLE_DOCTOR"})
//    public ResponseEntity<Void> patchLeaveRequest(@PathVariable String doctorId,
//                                                  @PathVariable Long appointmentId,
//                                                  @RequestBody LeaveRequestReview leaveRequestReview) {
//        doctorService.patchAppointment(doctorId, appointmentId, leaveRequestReview);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    // TODO trebuie sa trimita o notificare pentru userul caruia i-a anulat programarea
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long requestId) {
        doctorService.deleteAppointment(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    //Threads
    // TODO trebuie sa afiseze doar cererile pentru doctorul logat
    @RequestMapping(method = RequestMethod.GET, consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody CompletableFuture<ResponseEntity> getAllAppointments() {
        return doctorService.getAllAppointments().<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleGetAppointmentFailure);
    }

    private static final Function<Throwable, ResponseEntity<? extends List<AppointmentEty>>> handleGetAppointmentFailure = throwable -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
}
