package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
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

import java.security.SecureRandom;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentRightsManager appointmentRightsManager;
    private final DoctorHandler doctorHandler;
    private final KeycloakClientApi keycloakClientApi;
    private final DoctorRepository doctorRepository;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;
    private final SpecializationDoctorRepository specializationDoctorRepository;
    private final SpecializationRepository specializationRepository;
    private final TimeManager timeManager;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Transactional
    public void createDoctor(DoctorCreationRequest doctorCreationRequest) {

        doctorCreationRequest.setPassword(doctorCreationRequest.getPassword());
        var doctorEty = DoctorMapper.INSTANCE.mapDoctorCreationRequestToDoctorEty(doctorCreationRequest);
        var mapper = DoctorCreationRequestToUserDetailsMapper.INSTANCE;
        var userToBeAdded = mapper.toUserDetails(doctorCreationRequest);
        var addedUser = keycloakClientApi.createUser(userToBeAdded);
        doctorEty.setSpecializationEtyList(null);
        doctorEty.getUser().setUserId(addedUser.getId());
        createInitialSetup(doctorEty);
        doctorRepository.save(doctorEty);
        doctorCreationRequest.getSpecializationIds().forEach(specId -> {
            specializationRepository.findById(specId).ifPresent(specializationEty -> {
                SpecializationDoctorEty specializationDoctorEty = new SpecializationDoctorEty();
                specializationDoctorEty.setSpecialization(specializationEty);
                specializationDoctorEty.setDoctor(doctorEty);
                specializationDoctorEty.setV(0L);
                specializationDoctorRepository.save(specializationDoctorEty);
            });
        });
    }

    @Transactional
    public void deleteAppointment(Long requestId) {
        var appointment = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getId().equals(requestId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND)
                .build())));

        appointmentRightsManager.checkDelete(appointment);
        appointmentRepository.deleteById(requestId);
    }

    public String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        while (password.length() < length) {
            int index = random.nextInt(CHARACTERS.length());
            char character = CHARACTERS.charAt(index);
            password.append(character);
        }

        return password.toString();
    }

    public DoctorEty getDoctor(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.USER_NOT_FOUND)
                        .build())));
    }

    @Transactional
    public DoctorDetailListItem getDoctorById(String doctorId) {
        var doctorDetailList = new DoctorDetailList();
        doctorDetailList.setItems(doctorHandler.handleDoctorSpecializationDetails());
        return doctorDetailList.getItems().stream().filter(d -> d.getId().equals(doctorId)).findFirst()
                .orElse(null);
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

    private void createInitialSetup(DoctorEty doctorEty) {
        var instant = timeManager.instant();
        doctorEty.getUser().setCrtTms(instant);
        doctorEty.getUser().setMdfUsr(doctorEty.getId());
        doctorEty.getUser().setMdfTms(instant);
        doctorEty.getUser().setStatus(UserStatus.ACTIVE);
    }

}
