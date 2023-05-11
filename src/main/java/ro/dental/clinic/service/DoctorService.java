package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.*;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.enums.UserStatus;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.mapper.DoctorCreationRequestToUserDetailsMapper;
import ro.dental.clinic.mapper.DoctorMapper;
import ro.dental.clinic.model.AppointmentReview;
import ro.dental.clinic.model.DoctorCreationRequest;
import ro.dental.clinic.model.DoctorDetailList;
import ro.dental.clinic.model.DoctorDetailListItem;
import ro.dental.clinic.utils.TimeManager;

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
    private final KeycloakClientApi keycloakClientApi;
    private final TimeManager timeManager;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;

    private final SpecializationRepository specializationRepository;

   // @Transactional
    public void createDoctor(DoctorCreationRequest doctorCreationRequest) {

        var doctorEty = DoctorMapper.INSTANCE.mapDoctorCreationRequestToDoctorEty(doctorCreationRequest);
        var mapper = DoctorCreationRequestToUserDetailsMapper.INSTANCE;
        var userToBeAdded = mapper.toUserDetails(doctorCreationRequest);
        var addedUser = keycloakClientApi.createUser(userToBeAdded);
        doctorCreationRequest.getSpecializationIds().forEach(specId -> {
            specializationRepository.findById(specId).ifPresent(specializationEty -> {
                doctorEty.addSpecializationEty(specializationEty);
            });
        });
        doctorEty.getUser().setUserId(addedUser.getId());
        createInitialSetup(doctorEty);
        doctorRepository.save(doctorEty);
    }

    @Transactional
    public DoctorDetailList getDoctorDetails(Long specializationId) {
        var doctorDetailList = new DoctorDetailList();
        if (specializationId == null) {
            doctorDetailList.setItems(doctorHandler.handleDoctorSpecializationDetails());
        } else {
            doctorDetailList.setItems(doctorHandler.handleDoctorSpecializationDetails(specializationId));
        }
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

    private void createInitialSetup(DoctorEty doctorEty) {
        var instant = timeManager.instant();
        doctorEty.getUser().setCrtTms(instant);
        doctorEty.getUser().setMdfUsr(doctorEty.getId());
        doctorEty.getUser().setMdfTms(instant);
        doctorEty.getUser().setStatus(UserStatus.ACTIVE);
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

        var appointment = checkAppointmentIsValid(doctor, appointmentId);

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

    public DoctorEty getDoctor(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.USER_NOT_FOUND)
                        .build())));
    }

    private AppointmentEty checkAppointmentIsValid(DoctorEty doctorEty,
                                                   Long appointmentId) {

        return doctorEty.getAppointmentEtyList().stream()
                .filter(appointmentEty -> appointmentEty.getId()
                        .equals(appointmentId)).findFirst()
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND)
                        .build())));
    }

    @Transactional
    public DoctorDetailListItem getDoctorById(String doctorId) {
        var doctorDetailList = new DoctorDetailList();
        doctorDetailList.setItems(doctorHandler.handleDoctorSpecializationDetails());
        DoctorDetailListItem doctorDetails = doctorDetailList.getItems().stream().filter(d -> d.getId().equals(doctorId)).findFirst()
                .orElse(null);
        ;
        return doctorDetails;
    }


}
