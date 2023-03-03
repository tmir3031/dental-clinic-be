package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.dental.clinic.domain.SpecilizationRepository;
import ro.dental.clinic.mapper.SpecializationMapper;
import ro.dental.clinic.model.SpecializationDetailsList;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecilizationRepository specilizationRepository;

    public SpecializationDetailsList getSpecializationDetails() {
        var specializationDetailsList = new SpecializationDetailsList();
        specializationDetailsList.setItems(specilizationRepository.findAll().stream().map(SpecializationMapper.INSTANCE::mapSpecializationEtyToSpecializationDto)
                .collect(Collectors.toList()));
        return specializationDetailsList;
    }
}
