package ro.dental.clinic.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.UserAccountList;
import ro.dental.clinic.service.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AdminApi {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<UserAccountList> getAllUsers() {
        return ResponseEntity.ok(
                adminService.getAllUsers());
    }

}