package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.*;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.DoctorDetailList;
import ro.dental.clinic.utils.TimeManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final AppointmentRightsManager appointmentRightsManager;
    private final AppointmentRepository appointmentRepository;

    private final DoctorHandler doctorHandler;

    @Transactional
    public DoctorDetailList getDoctorDetails() {
        var doctorDetailList = new DoctorDetailList();
        doctorDetailList.setItems(doctorHandler.handleDoctorSpecializationDetails());
        return doctorDetailList;
    }

    @Transactional
    public void deleteAppointment(Long requestId) {
        var appointment = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getId().equals(requestId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND)
                .build())));;
        appointmentRightsManager.checkDelete(appointment);
        appointmentRepository.deleteById(requestId);
    }
    // ------------------------------------------------------------------------------------------------------------------
    // Threads
    @Async
    public CompletableFuture<List<AppointmentEty>> getAllAppointments() {
        final List<AppointmentEty> appointmentEties = appointmentRepository.findAll();
        return CompletableFuture.completedFuture(appointmentEties);
    }

}
