package ro.dental.clinic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.DoctorEty;
import ro.dental.clinic.domain.PatientEty;
import ro.dental.clinic.model.DoctorCreationRequest;
import ro.dental.clinic.model.DoctorDetailListItem;
import ro.dental.clinic.model.PatientCreationRequest;

/**
 * Mapper used for converting EmployeeEty object to EmployeeDto object
 */

//@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = SpecializationMapper.class)
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, uses = SpecializationMapper.class)
public interface DoctorMapper {

    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(target = "specializationDetailsListItem", source = "specializationEtyList")
    @Mapping(target = "username",source = "user.username")
    @Mapping(target = "firstName",source = "user.firstName")
    @Mapping(target = "email",source = "user.email")
    @Mapping(target = "role",source = "user.role")
    @Mapping(target = "status",source = "user.status")
    @Mapping(target = "lastName",source = "user.lastName")
    @Mapping(target = "gender",source = "user.gender")
    DoctorDetailListItem mapDoctorEtyToDoctorDto(DoctorEty doctorEty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specializationEtyList", ignore = true)
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "user.userId", ignore = true)
    @Mapping(target = "user.crtUsr", ignore = true)
    @Mapping(target = "user.mdfUsr",ignore = true)
    @Mapping(target = "user.crtTms", ignore = true)
    @Mapping(target = "user.mdfTms", ignore = true)
    @Mapping(target = "user.email", source = "email")
    @Mapping(target = "user.role", source = "role")
    @Mapping(target = "user.gender", source = "gender")
    @Mapping(target = "appointmentEtyList", ignore = true)
    @Mapping(target = "v", ignore = true)
    DoctorEty mapDoctorCreationRequestToDoctorEty(
            DoctorCreationRequest doctorCreationRequest);



}