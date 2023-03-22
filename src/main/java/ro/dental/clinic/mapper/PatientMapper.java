package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.PatientEty;
import ro.dental.clinic.domain.UserEty;
import ro.dental.clinic.model.PatientCreationRequest;

/**
 * Mapper used for converting UserEty object to UserDto object
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PatientMapper {

    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.userId", ignore = true)
    @Mapping(target = "user.crtUsr", ignore = true)
    @Mapping(target = "user.mdfUsr",ignore = true)
    @Mapping(target = "user.crtTms", ignore = true)
    @Mapping(target = "user.mdfTms", ignore = true)
    @Mapping(target = "user.email", source = "email")
    // @Mapping(target = "description", source = "description")
    @Mapping(target = "user.role", source = "role")

    @Mapping(target = "appointmentEtyList", ignore = true)
    @Mapping(target = "v", ignore = true)
    PatientEty mapPatientCreationRequestToPatientEty(
            PatientCreationRequest patientCreationRequest);

}
