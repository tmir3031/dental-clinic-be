package ro.dental.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.AppointmentDetailsListItem;
import ro.dental.clinic.model.AppointmentListItem;
import ro.dental.clinic.model.PatientDetailListItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AppointmentMapper {

    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);

    @Mapping(source = "doctor.user", target = "doctorDetails")
    @Mapping(source = "patient.user", target = "patientDetails")
    AppointmentDetailsListItem mapAppointmentEtyToAppointmentDetailsDto(AppointmentEty appointmentEty);

    @Mapping(source = "doctor.user", target = "doctorDetails")
    @Mapping(source = "patient.user", target = "patientDetails")
    AppointmentListItem mapAppointmentEtyToAppointmentDto(AppointmentEty appointmentEty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "crtTms", ignore = true)
    @Mapping(target = "mdfUsr", ignore = true)
    @Mapping(target = "mdfTms", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "v", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "treatment", ignore = true)
    AppointmentEty mapAppointmentCreationRequestToAppointmentEty(AppointmentCreationRequest appointmentCreationRequest);

    @Mapping(source = "patient.id", target = "id")
    @Mapping(source = "patient.user.firstName", target = "firstName")
    @Mapping(source = "patient.user.lastName", target = "lastName")
    @Mapping(source = "patient.phone", target = "phone")
    PatientDetailListItem mapLeaveRequestEtyToPatientDto(AppointmentEty appointmentEty);

}
