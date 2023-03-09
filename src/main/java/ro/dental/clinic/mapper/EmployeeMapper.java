package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.EmployeeEty;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.EmployeeDetailListItem;

/**
 * Mapper used for converting EmployeeEty object to EmployeeDto object
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "specializationDetailsListItem", ignore = true)
    EmployeeDetailListItem mapEmployeeEtyToEmployeeDto(EmployeeEty employeeEty);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "mdfUsr", ignore = true)
    @Mapping(target = "mdfTms", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "crtUsr", ignore = true)
    @Mapping(target = "crtTms", ignore = true)
    @Mapping(target = "appointmentEtyList", ignore = true)
    @Mapping(target = "v", ignore = true)
    EmployeeEty mapEmployeeCreationRequestToEmployeeEty(
            EmployeeCreationRequest employeeCreationRequest);
}