package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.dental.clinic.domain.DoctorRepository;
import ro.dental.clinic.domain.SpecializationDoctorRepository;
import ro.dental.clinic.domain.SpecializationRepository;
import ro.dental.clinic.mapper.DoctorMapper;
import ro.dental.clinic.model.DoctorDetailListItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DoctorHandler {
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorMapper doctorMapper;

    public List<DoctorDetailListItem> handleDoctorSpecializationDetails() {
        return doctorRepository.findAll().stream().map(doctorMapper::mapDoctorEtyToDoctorDto).collect(Collectors.toList());
    }

    public List<DoctorDetailListItem> handleDoctorSpecializationDetails(Long specializationId) {
        var specialization = specializationRepository.findById(specializationId);
        return specialization.map(specializationEty -> specializationEty.getDoctorEtyList().stream().map(doctorMapper::mapDoctorEtyToDoctorDto).collect(Collectors.toList())).orElse(null);
    }

}