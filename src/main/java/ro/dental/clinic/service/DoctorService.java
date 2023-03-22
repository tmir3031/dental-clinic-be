package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.domain.AppointmentRepository;
import ro.dental.clinic.domain.DoctorEty;
import ro.dental.clinic.domain.DoctorRepository;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.AppointmentReview;
import ro.dental.clinic.model.DoctorDetailList;
import ro.dental.clinic.utils.TimeManager;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final AppointmentRightsManager appointmentRightsManager;
    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    private final DoctorHandler doctorHandler;

    private final TimeManager timeManager;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;

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
                .build())));
        ;
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

    @Transactional
    public void patchAppointment(String doctorId, Long appointmentId, AppointmentReview appointmentReview) {
        var doctor = getDoctor(doctorId);

        var appointment = checkAppointmentIsValid(doctor, appointmentId,
                appointmentReview.getV());

        appointment.setMdfTms(timeManager.instant());
        appointment.setMdfUsr(securityAccessTokenProvider.getUserIdFromAuthToken());

        if (isNull(appointmentReview.getTreatment())) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.APPOINTMENT_TREATMENT_IS_NULL)
                    .build()));
        } else {
            appointment.setTreatment(appointmentReview.getTreatment());
        }

        doctorRepository.save(doctor);
    }

    private DoctorEty getDoctor(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.USER_NOT_FOUND)
                        .build())));
    }

    private AppointmentEty checkAppointmentIsValid(DoctorEty doctorEty,
                                                   Long appointmentId, Long v) {

        var appointment = doctorEty.getAppointmentEtyList().stream()
                .filter(appointmentEty -> appointmentEty.getId()
                        .equals(appointmentId)).findFirst()
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND)
                        .build())));

        if (!appointment.getV().equals(v)) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.APPOINTMENT_CONFLICT)
                    .build()));
        }

        return appointment;
    }
}
