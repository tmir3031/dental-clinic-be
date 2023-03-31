package ro.dental.clinic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
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
    TreatmentDetailsListItem mapTreatmentEtyToTreatmentDto(TreatmentEty treatmentEty);

}
