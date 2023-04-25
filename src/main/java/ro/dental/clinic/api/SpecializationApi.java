package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.SpecializationDetailsList;
import ro.dental.clinic.service.SpecializationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SpecializationApi {
    private final SpecializationService specializationService;

    @GetMapping("specializations")
    public ResponseEntity<SpecializationDetailsList> getSpecializationDetails(@RequestParam(value = "doctorId", required = false) String doctorId) {
        return ResponseEntity.ok(specializationService.getSpecializationDetails(doctorId));
    }
}
