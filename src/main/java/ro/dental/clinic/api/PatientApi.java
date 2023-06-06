package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.dental.clinic.email.SenderEmailService;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.PatientCreationRequest;
import ro.dental.clinic.model.PatientUpdateRequest;
import ro.dental.clinic.model.TreatmentAppointmentDetailsList;
import ro.dental.clinic.service.PatientService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/patients")
public class PatientApi {
    private final PatientService patientService;
    private final SenderEmailService senderEmailService;

    @PostMapping
    public ResponseEntity<Void> createPatient(
            @Valid @RequestBody PatientCreationRequest patientCreationRequest) {
        patientService.createPatient(patientCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long appointmentId) {
        patientService.deleteAppointment(appointmentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientCreationRequest> getPatientById(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getPatientById(patientId));
    }

    @GetMapping("/treatments/{patientId}")
    public ResponseEntity<TreatmentAppointmentDetailsList> getTreatmentsForAPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.getTreatemntsForAPatient(patientId));
    }

    @PostMapping("/{patientId}/appointments")
    public ResponseEntity<Void> createAppointment(
            @PathVariable String patientId,
            @Valid @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        patientService.createAppointment(patientId, appointmentCreationRequest);
        var patient = patientService.getPatientById(patientId);
        String email = "timonea_raluca@yahoo.com"; // patient.getUser().getEmail();
        String subject = "Programare efectuata cu succes";
        String body = "Îți transmitem această notificare pentru a confirma programarea efectuata la clinica noastra in data de " + appointmentCreationRequest.getDate() + "ora " + appointmentCreationRequest.getHour() + "\n" + "Multimim pentru increderea acordata!\n" + "Cu respect,\n" + "Echipa DentalConnect";
        senderEmailService.sendEmail(email, body, subject);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{patientId}")
    public ResponseEntity<Void> updatePatient(@PathVariable String patientId,
                                              @Valid @RequestBody PatientUpdateRequest patientUpdateRequest) {
        patientService.updatePatient(patientId, patientUpdateRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
