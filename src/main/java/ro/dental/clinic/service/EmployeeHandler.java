package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.dental.clinic.domain.QSpecializationUserEty;
import ro.dental.clinic.domain.SpecializationUserRepository;
import ro.dental.clinic.mapper.SpecializationMapper;
import ro.dental.clinic.model.SpecializationDetailsListItem;

@Component
@RequiredArgsConstructor
public class EmployeeHandler {
    private final SpecializationUserRepository specializationUserRepository;

    public SpecializationDetailsListItem handleEmployeeSpecializationDetails(String employeeId) {
        var rs = specializationUserRepository.findAll(
                QSpecializationUserEty.specializationUserEty.employee.employeeId.eq(employeeId));
        return rs.iterator().hasNext() ?
                SpecializationMapper.INSTANCE.mapSpecializationEtyToSpecializationDto(specializationUserRepository.findAll(
                                QSpecializationUserEty.specializationUserEty.employee.employeeId.eq(employeeId))
                        .iterator().next().getSpecializationEty()) : null;
    }

}