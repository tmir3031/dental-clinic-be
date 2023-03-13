package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.UserDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmployeeCreationRequestToUserDetailsMapper {

    EmployeeCreationRequestToUserDetailsMapper INSTANCE =
            Mappers.getMapper(EmployeeCreationRequestToUserDetailsMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "convertRole")
    UserDetails toUserDetails(EmployeeCreationRequest request);

    @Named(value = "convertRole")
    static String convertRole(String roleFromCreationRequest) {
        return "ROLE_" + roleFromCreationRequest;
    }

}

