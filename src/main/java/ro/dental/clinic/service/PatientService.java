package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.*;
import ro.dental.clinic.enums.AppointmentStatus;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.enums.UserStatus;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.mapper.AppointmentMapper;
import ro.dental.clinic.mapper.PatientCreationRequestToUserDetailsMapper;
import ro.dental.clinic.mapper.PatientMapper;
import ro.dental.clinic.model.*;
import ro.dental.clinic.utils.TimeManager;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final AppointmentRightsManager appointmentRightsManager;

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientHandler patientHandler;
    private final TimeManager timeManager;
    private final AppointmentRepository appointmentRepository;
    private final KeycloakClientApi keycloakClientApi;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;


    @Transactional
    public void createPatient(PatientCreationRequest userCreationRequest) {

        var patientEty = PatientMapper.INSTANCE.mapPatientCreationRequestToPatientEty(userCreationRequest);

        var mapper = PatientCreationRequestToUserDetailsMapper.INSTANCE;
        var userToBeAdded = mapper.toUserDetails(userCreationRequest);
        var addedUser = keycloakClientApi.createUser(userToBeAdded);
        patientEty.getUser().setUserId(addedUser.getId());
        createInitialSetup(patientEty);
        patientRepository.save(patientEty);
    }

    private void createInitialSetup(PatientEty patientEty) {
        var instant = timeManager.instant();
        patientEty.getUser().setCrtTms(instant);
        patientEty.getUser().setMdfUsr(patientEty.getId());
        patientEty.getUser().setMdfTms(instant);
        patientEty.getUser().setStatus(UserStatus.ACTIVE);
    }

    @Transactional
    public void updatePatient(String patientId, PatientUpdateRequest patientUpdateRequest) {

        var patientEty = patientRepository.findAll().stream().filter(patient -> patient.getId().equals(patientId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.USER_NOT_FOUND).build())));

        if (isNull(patientUpdateRequest.getV())) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.INVALID_PAYLOAD).build()));
        }

        setMdfUsrAndTms(patientEty);
        updatePatient(patientEty, patientUpdateRequest);

        patientRepository.save(patientEty);
    }

    private void setMdfUsrAndTms(PatientEty patientEty) {
        patientEty.getUser().setMdfUsr(securityAccessTokenProvider.getUserIdFromAuthToken());
        patientEty.getUser().setMdfTms(timeManager.instant());
    }

    private void updatePatient(PatientEty patientEty, PatientUpdateRequest patientUpdateRequest) {
        var oldUserEty = patientEty.getUser();
        Optional.ofNullable(patientUpdateRequest.getFirstName()).ifPresent(oldUserEty::setFirstName);
        Optional.ofNullable(patientUpdateRequest.getLastName()).ifPresent(oldUserEty::setLastName);
        Optional.ofNullable(patientUpdateRequest.getGender()).ifPresent(oldUserEty::setGender);
        patientEty.setAllergies(patientUpdateRequest.getAllergies());
        patientEty.setPhone(patientUpdateRequest.getPhone());
        patientEty.setChronicDiseases(patientUpdateRequest.getDiseases());
        patientEty.setUser(oldUserEty);
    }


    @Transactional
    public void deleteAppointment(Long requestId) {
        var appointment = appointmentRepository.findAll().stream().filter(appointmentEty -> appointmentEty.getId().equals(requestId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND).build())));
        ;
        appointmentRightsManager.checkDelete(appointment);
        appointmentRepository.deleteById(requestId);
    }

    @Transactional
    public void createAppointment(String patientId, AppointmentCreationRequest creationRequest) {
        var patient = patientRepository.findById(patientId).orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.USER_NOT_FOUND).build())));
        var doctor = doctorRepository.findById(creationRequest.getDoctorId()).orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.USER_NOT_FOUND).build())));
        List<AppointmentEty> appointments = appointmentRepository.findAll().stream().filter(a -> a.getDoctor().getId().equals(doctor.getId())).filter(a -> a.getDate().equals(creationRequest.getDate())).filter(a -> a.getHour().equals(creationRequest.getHour())).collect(Collectors.toList());

        if (!appointments.isEmpty()) {
            for (AppointmentEty appointment : appointments) {
                if (appointment.getStatus() == AppointmentStatus.APPROVED) {
                    throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED).build()));
                }
            }
        }

        var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(creationRequest);
        newAppointment.setDoctor(doctor);
        appointmentRightsManager.checkCreate(newAppointment);

        var instant = timeManager.instant();
        newAppointment.setCrtTms(instant);
        newAppointment.setPatient(patient);
        newAppointment.setMdfTms(instant);
        newAppointment.setMdfUsr(patient.getId());
        newAppointment.setStatus(AppointmentStatus.APPROVED);
        newAppointment.setTreatment("");
        appointmentRepository.save(newAppointment);
    }

    @Transactional
    public PatientCreationRequest getPatientById(String patientId) {
        return (patientHandler.handlePatientDetails(patientId));
    }

    @Transactional
    public PatientDetailList getAllPatientDTOsForADoctor(String doctorId) {
        var patients = new PatientDetailList();
        List<PatientDetailListItem> patientDTOs = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .map(appointment -> AppointmentMapper.INSTANCE.mapLeaveRequestEtyToPatientDto(appointment))
                .distinct()
                .collect(Collectors.toList());
        patients.setItems(patientDTOs);
        return patients;
    }

    @Transactional
    public TreatmentAppointmentDetailsList getTreatemntsForAPatient(String patientId) {
        Optional<PatientEty> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isEmpty()) {
            throw new RuntimeException("Pacientul cu id-ul " + patientId + " nu exista.");
        }
        var patient = patientOpt.get();

        var treatmentAppointmentDetailsList = new TreatmentAppointmentDetailsList();
        List<TreatmentAppointmentDetailsListItem> appointmentsList = appointmentRepository.findAllByPatient(patient)
                .stream()
                .filter(appointmentEty -> appointmentEty.getDate().isBefore(LocalDate.now()))
                .map(app -> {

                    TreatmentAppointmentDetailsListItem treatmentAppointmentDetailsListItem = new TreatmentAppointmentDetailsListItem();
                    treatmentAppointmentDetailsListItem.setTreatment(app.getTreatment());
                    treatmentAppointmentDetailsListItem.setDate(app.getDate());
                    var doctorDetails = new EmployeeDetailsListItem();
                    doctorDetails.setFirstName(app.getDoctor().getUser().getFirstName());
                    doctorDetails.setLastName(app.getDoctor().getUser().getLastName());
                    doctorDetails.setUserId(app.getDoctor().getUser().getUserId());
                    treatmentAppointmentDetailsListItem.setDoctorDetails(doctorDetails);
                    return treatmentAppointmentDetailsListItem;

                })
                .sorted(Comparator.comparing(TreatmentAppointmentDetailsListItem::getDate).reversed())
                .collect(Collectors.toList());

        treatmentAppointmentDetailsList.setItems(appointmentsList);
        return treatmentAppointmentDetailsList;
    }

}
