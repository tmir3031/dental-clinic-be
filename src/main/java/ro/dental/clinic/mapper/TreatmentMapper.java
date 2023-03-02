package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.TreatmentEty;
import ro.dental.clinic.model.TreatmentDetailsListItem;

/**
 * Mapper used for converting TreatmentEty object to TreatmentDto object
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TreatmentMapper {

    TreatmentMapper INSTANCE = Mappers.getMapper(TreatmentMapper.class);

    @Mapping(target = "v", ignore = true)
    @Mapping(target = "specialization_id", ignore = true)
    TreatmentDetailsListItem mapTreatmentEtyToTreatmentDto(TreatmentEty treatmentEty);

}
