package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.dental.clinic.domain.DoctorRepository;
import ro.dental.clinic.domain.SpecializationRepository;
import ro.dental.clinic.mapper.SpecializationMapper;
import ro.dental.clinic.model.SpecializationDetailsList;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;

    public SpecializationDetailsList getSpecializationDetails(String doctorId) {
        var specializationDetailsList = new SpecializationDetailsList();
        if (doctorId == null) {
            specializationDetailsList.setItems(specializationRepository.findAll().stream().map(SpecializationMapper.INSTANCE::mapSpecializationEtyToSpecializationDto)
                    .collect(Collectors.toList()));
        } else {
            var doctor = doctorRepository.findById(doctorId);
            specializationDetailsList.setItems(doctor.get().getSpecializationEtyList().stream().map(SpecializationMapper.INSTANCE::mapSpecializationEtyToSpecializationDto)
                    .collect(Collectors.toList()));
        }
        return specializationDetailsList;
    }
}
