package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.dental.clinic.domain.TreatmentRepository;
import ro.dental.clinic.mapper.TreatmentMapper;
import ro.dental.clinic.model.TreatmentDetailsList;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentDetailsList getTreatmentDetails(String type) {
        var treatmentDetailsList = new TreatmentDetailsList();
        treatmentDetailsList.setItems(treatmentRepository.findAll().stream().filter(treatmentEty -> treatmentEty.getType().equals(type)).map(TreatmentMapper.INSTANCE::mapTreatmentEtyToTreatmentDto)
                .collect(Collectors.toList()));
        return treatmentDetailsList;
    }
}
