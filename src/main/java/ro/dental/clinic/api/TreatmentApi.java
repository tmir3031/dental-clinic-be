package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.TreatmentDetailsList;
import ro.dental.clinic.service.TreatmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TreatmentApi {
    private final TreatmentService treatmentService;

    @GetMapping("treatments")
    public ResponseEntity<TreatmentDetailsList> getTreatmentsDetails() {
        return ResponseEntity.ok(treatmentService.getTreatmentsDetails());
    }

}
