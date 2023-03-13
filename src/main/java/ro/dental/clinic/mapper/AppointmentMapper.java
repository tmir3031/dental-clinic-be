package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.AppointmentDetailsListItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AppointmentMapper {

    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "employee", target = "employeeDetails")
    AppointmentDetailsListItem mapLeaveRequestEtyToLeaveRequestDto(AppointmentEty appointmentEty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "crtUsr", ignore = true)
    @Mapping(target = "crtTms", ignore = true)
    @Mapping(target = "mdfUsr", ignore = true)
    @Mapping(target = "mdfTms", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "rejectReason", ignore = true)
    @Mapping(target = "v", ignore = true)
    @Mapping(target = "employee", ignore = true)
    AppointmentEty mapAppointmentCreationRequestToAppointmentEty(AppointmentCreationRequest appointmentCreationRequest);

}
