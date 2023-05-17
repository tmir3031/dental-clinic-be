package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.email.SenderEmailService;
import ro.dental.clinic.model.*;
import ro.dental.clinic.service.DoctorService;
import ro.dental.clinic.service.PatientService;
import ro.dental.clinic.service.PhotoService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/doctors")
public class DoctorApi {

    private final DoctorService doctorService;

    private final SenderEmailService senderEmailService;
    private final PhotoService photoService;

    private final PatientService patientService;

    @PostMapping("/save-image/{userId}")
    public ResponseEntity<Void> create(@PathVariable String userId,
            @RequestParam("image") MultipartFile image
    ) {
        photoService.createEty(image, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/view-image/{userId}")
    public ResponseEntity<ImageModel> getImage(@PathVariable String userId) {
        return ResponseEntity.ok(photoService.getImage(userId));
    }

    @GetMapping("/view-all-image/{userId}")
    public ResponseEntity<List<ImageModel>> getImages(@PathVariable String userId) {
        return ResponseEntity.ok(photoService.getImages(userId));
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createDoctor(
            @Valid @RequestBody DoctorCreationRequest userCreationRequest) {
        doctorService.createDoctor(userCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDetailListItem> getDoctorById(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping("/my-patients/{doctorId}")
    public ResponseEntity<PatientDetailList> getAllMyPatient(@PathVariable String doctorId) {
        return ResponseEntity.ok(patientService.getAllPatientDTOsForADoctor(doctorId));
    }

    @GetMapping
    public ResponseEntity<DoctorDetailList> getDoctorDetailsList(
            @RequestParam(value = "specializationId", required = false) Long specializationId
    ) {
        return ResponseEntity.ok(doctorService.getDoctorDetails(specializationId));
    }

    @PatchMapping("/{doctorId}/appointments/{appointmentId}")
    @Secured({"ROLE_DOCTOR"})
    public ResponseEntity<Void> patchLeaveRequest(@PathVariable String doctorId,
                                                  @PathVariable Long appointmentId,
                                                  @RequestBody AppointmentReview appointmentReview) {
        doctorService.patchAppointment(doctorId, appointmentId, appointmentReview);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // TODO trebuie sa trimita o notificare pentru userul caruia i-a anulat programarea
    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId) {
        doctorService.deleteAppointment(appointmentId);
        senderEmailService.sendEmail("timonea_raluca@yahoo.com", "Aici e un text pentru stergerea unei cereri", "aici e subiectul emailului");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    //Threads
    @GetMapping("/all_appp")
    public @ResponseBody ResponseEntity<CompletableFuture<List<AppointmentEty>>> getAllAppointments() {
//        return doctorService.getAllAppointments().<ResponseEntity>thenApply(ResponseEntity::ok)
//                .exceptionally(handleGetAppointmentFailure);
        return ResponseEntity.ok(doctorService.getAllAppointments());
    }

    private static final Function<Throwable, ResponseEntity<? extends List<AppointmentEty>>> handleGetAppointmentFailure = throwable -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
}
