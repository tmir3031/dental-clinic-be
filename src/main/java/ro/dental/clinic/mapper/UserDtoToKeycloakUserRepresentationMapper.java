package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.model.KeycloakUserRepresentation;
import ro.dental.clinic.model.UserDetails;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserDtoToKeycloakUserRepresentationMapper {

    UserDtoToKeycloakUserRepresentationMapper INSTANCE =
            Mappers.getMapper(UserDtoToKeycloakUserRepresentationMapper.class);

    @Mapping(target = "credentials", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    KeycloakUserRepresentation toKeycloakUserRepresentation(UserDetails user);

}