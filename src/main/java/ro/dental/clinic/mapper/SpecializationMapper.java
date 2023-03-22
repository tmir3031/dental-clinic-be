package ro.dental.clinic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.SpecializationEty;
import ro.dental.clinic.model.SpecializationDetailsListItem;

/**
 * Mapper used for converting SpecializationEty object to SpecializationDto object
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, componentModel = "spring"
        )
public interface SpecializationMapper {

    SpecializationMapper INSTANCE = Mappers.getMapper(SpecializationMapper.class);

    SpecializationDetailsListItem mapSpecializationEtyToSpecializationDto(SpecializationEty specializationEty);

}
