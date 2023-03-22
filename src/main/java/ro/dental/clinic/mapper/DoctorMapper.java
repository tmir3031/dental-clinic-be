package ro.dental.clinic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.DoctorEty;
import ro.dental.clinic.model.DoctorDetailListItem;

/**
 * Mapper used for converting EmployeeEty object to EmployeeDto object
 */

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = SpecializationMapper.class)
public interface DoctorMapper {

    // DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(target = "specializationDetailsListItem", source = "specializationEtyList")
    @Mapping(target = "username",source = "user.username")
    @Mapping(target = "firstName",source = "user.firstName")
    @Mapping(target = "email",source = "user.email")
    @Mapping(target = "role",source = "user.role")
    @Mapping(target = "status",source = "user.status")
    @Mapping(target = "lastName",source = "user.lastName")
    DoctorDetailListItem mapDoctorEtyToDoctorDto(DoctorEty doctorEty);


}