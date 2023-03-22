package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.DoctorDetailList;
import ro.dental.clinic.model.PatientUpdateRequest;
import ro.dental.clinic.model.PatientCreationRequest;
import ro.dental.clinic.service.PatientService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/patients")
public class PatientApi {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Void> createPatient(
            @Valid @RequestBody PatientCreationRequest userCreationRequest) {
        patientService.createPatient(userCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long requestId) {
        patientService.deleteAppointment(requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{patientId}/requests")
    public ResponseEntity<Void> postAppointment(
            @PathVariable String patientId,
            @Valid @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        patientService.createAppointment(patientId, appointmentCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{patientId}")
    public ResponseEntity<Void> updateEmployee(@PathVariable String patientId,
                                               @Valid @RequestBody PatientUpdateRequest patientUpdateRequest) {
        patientService.updateEmployee(patientId, patientUpdateRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    //Threads

//    @PostMapping("/{employeeId}/requests2")
//    public ResponseEntity<Void> postAppointment2(
//            @PathVariable String employeeId,
//            @Valid @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
//        patientService.saveAppointment(employeeId, appointmentCreationRequest);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }

}
