package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.EmployeeDetailList;
import ro.dental.clinic.service.EmployeeService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeApi {
    private final EmployeeService employeeService;
    @GetMapping
    public ResponseEntity<EmployeeDetailList> getEmployeeDetailsListByRole(
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(employeeService.getEmployeeDetails());
    }

    @PostMapping
    // @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> createEmployee(
            @Valid @RequestBody EmployeeCreationRequest employeeCreationRequest) {
        employeeService.createEmployee(employeeCreationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
