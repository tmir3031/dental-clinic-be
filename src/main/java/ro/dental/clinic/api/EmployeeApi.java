package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.EmployeeDetailList;
import ro.dental.clinic.model.EmployeeUpdateRequest;
import ro.dental.clinic.service.EmployeeService;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeApi {
    private final EmployeeService employeeService;
    @GetMapping
    public ResponseEntity<EmployeeDetailList> getEmployeeDetailsListByRole() {
        return ResponseEntity.ok(employeeService.getEmployeeDetails());
    }

    @PostMapping
    public ResponseEntity<Void> createEmployee(
            @Valid @RequestBody EmployeeCreationRequest employeeCreationRequest) {
        employeeService.createEmployee(employeeCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{employeeId}/requests/{requestId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String employeeId,
                                                   @PathVariable Long requestId) {
        employeeService.deleteAppointment(employeeId, requestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{employeeId}/requests")
    public ResponseEntity<Void> postAppointment(
            @PathVariable String employeeId,
            @Valid @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        log.trace("Create an appointment  for the employee with id {} based on the request: {}", employeeId, appointmentCreationRequest);
        employeeService.createAppointment(employeeId, appointmentCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/{employeeId}")
    public ResponseEntity<Void> updateEmployee(@PathVariable String employeeId,
                                               @Valid @RequestBody EmployeeUpdateRequest employeeUpdateRequest) {
        employeeService.updateEmployee(employeeId, employeeUpdateRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------------------------------------------------------------------------------------------------
    //Threads

    @PostMapping("/{employeeId}/requests2")
    public ResponseEntity<Void> postAppointment2(
            @PathVariable String employeeId,
            @Valid @RequestBody AppointmentCreationRequest appointmentCreationRequest) {
        employeeService.saveAppointment(employeeId, appointmentCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @RequestMapping (method = RequestMethod.GET, consumes={MediaType.APPLICATION_JSON_VALUE},
            produces={MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody CompletableFuture<ResponseEntity> getAllAppointments() {
        return employeeService.getAllAppointments().<ResponseEntity>thenApply(ResponseEntity::ok)
                .exceptionally(handleGetAppointmentFailure);
    }

    private static Function<Throwable, ResponseEntity<? extends List<AppointmentEty>>> handleGetAppointmentFailure = throwable -> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };

}
