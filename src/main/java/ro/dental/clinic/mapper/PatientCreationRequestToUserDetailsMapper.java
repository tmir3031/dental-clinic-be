package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.model.PatientCreationRequest;
import ro.dental.clinic.model.UserDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PatientCreationRequestToUserDetailsMapper {

    PatientCreationRequestToUserDetailsMapper INSTANCE =
            Mappers.getMapper(PatientCreationRequestToUserDetailsMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "convertRole")
    UserDetails toUserDetails(PatientCreationRequest request);

    @Named(value = "convertRole")
    static String convertRole(String roleFromCreationRequest) {
        return "ROLE_" + roleFromCreationRequest;
    }

}

