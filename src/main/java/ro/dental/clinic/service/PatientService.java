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
import ro.dental.clinic.mapper.DoctorMapper;
import ro.dental.clinic.mapper.PatientCreationRequestToUserDetailsMapper;
import ro.dental.clinic.mapper.PatientMapper;
import ro.dental.clinic.model.*;
import ro.dental.clinic.utils.TimeManager;

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
    private final TimeManager timeManager;
    private final AppointmentRepository appointmentRepository;

    private final SpecializationDoctorRepository specializationDoctorRepository;
    private final SpecializationRepository specializationRepository;
    private final KeycloakClientApi keycloakClientApi;
    private final SecurityAccessTokenProvider securityAccessTokenProvider;


    @Transactional
    public void createPatient(
            PatientCreationRequest userCreationRequest) {

        var patientEty = PatientMapper.INSTANCE.mapPatientCreationRequestToPatientEty(
                userCreationRequest);

        var mapper = PatientCreationRequestToUserDetailsMapper.INSTANCE;
        var userToBeAdded = mapper.toUserDetails(userCreationRequest);
        var addedUser = keycloakClientApi.createUser(userToBeAdded);
        patientEty.getUser().setUserId(addedUser.getId());
        createInitialSetup(patientEty);
        patientRepository.save(patientEty);
    }

    private void createInitialSetup(PatientEty patientEty) {
        // var creationUser = securityAccessTokenProvider.getUserIdFromAuthToken();
        var instant = timeManager.instant();
        patientEty.getUser().setCrtTms(instant);
        patientEty.getUser().setMdfUsr(patientEty.getId());
        patientEty.getUser().setMdfTms(instant);
        patientEty.getUser().setStatus(UserStatus.ACTIVE);
    }

    @Transactional
    public void updateEmployee(String patientId,
                               PatientUpdateRequest patientUpdateRequest) {

        var patientEty = patientRepository.findAll().stream().filter(patient -> patient.getId().equals(patientId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                .build())));
        ;

        if (isNull(patientUpdateRequest.getV())) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.INVALID_PAYLOAD)
                    .build()));
        }

        if (patientUpdateRequest.getV() < patientEty.getV()) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.EMPLOYEE_CONFLICT)
                    .build()));
        }

        setMdfUsrAndTms(patientEty);
        updateEmployee(patientEty, patientUpdateRequest);

        patientRepository.save(patientEty);
    }

    private void setMdfUsrAndTms(PatientEty patientEty) {
        patientEty.getUser().setMdfUsr(securityAccessTokenProvider.getUserIdFromAuthToken());
        patientEty.getUser().setMdfTms(timeManager.instant());
    }

    private void updateEmployee(PatientEty patientEty,
                                PatientUpdateRequest patientUpdateRequest) {
        var oldUserEty = patientEty.getUser();
        Optional.ofNullable(patientUpdateRequest.getFirstName()).ifPresent(oldUserEty::setFirstName);
        Optional.ofNullable(patientUpdateRequest.getLastName()).ifPresent(oldUserEty::setLastName);
        Optional.ofNullable(patientUpdateRequest.getEmail()).ifPresent(oldUserEty::setEmail);
        Optional.ofNullable(patientUpdateRequest.getRole()).ifPresent(role -> {
            UserDetails credentials = new UserDetails();
            credentials.setId(patientEty.getId());
            credentials.setRole("ROLE_" + patientEty.getUser().getRole());
            keycloakClientApi.updateRoleOfUserWithCredentials(credentials, "ROLE_" + role);
            patientEty.getUser().setRole(role);
        });
        patientEty.setUser(oldUserEty);
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

    @Transactional
    public void createAppointment(String patientId, AppointmentCreationRequest creationRequest) {
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        var doctor = doctorRepository.findById(creationRequest.getDoctorId())
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        List<AppointmentEty> appointments = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getDoctor().getId().equals(doctor.getId()))
                .filter(a -> a.getDate().equals(creationRequest.getDate()))
                .filter(a -> a.getHour().equals(creationRequest.getHour()))
                .collect(Collectors.toList());

        if (!appointments.isEmpty()) {
            // Dacă există deja programare, verificăm dacă este deja aprobată și aruncăm o excepție dacă este
            for (AppointmentEty appointment : appointments) {
                if (appointment.getStatus() == AppointmentStatus.APPROVED) {
                    throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                            .errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED)
                            .build()));
                }
            }
            // Dacă programarea există dar nu este aprobată, o ștergem și o recreăm
            appointmentRepository.deleteAll(appointments);
        }

        var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(
                creationRequest);
        newAppointment.setDoctor(doctor);
        appointmentRightsManager.checkCreate(newAppointment);

        var instant = timeManager.instant();
        newAppointment.setCrtTms(instant);
        newAppointment.setPatient(patient);
        newAppointment.setMdfTms(instant);
        newAppointment.setMdfUsr(patient.getId());
        newAppointment.setStatus(AppointmentStatus.APPROVED);
        newAppointment.setDescription(creationRequest.getDescription());
        appointmentRepository.save(newAppointment);
     }


    // ------------------------------------------------------------------------------------------------------------------
    // Threads
//
//    @Async
//    public CompletableFuture<List<AppointmentEty>> saveAppointment(String employeeId, AppointmentCreationRequest creationRequest) {
//        var employee = patientRepository.findById(employeeId)
//                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
//                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
//                        .build())));
//        var doctor = doctorRepository.findById(creationRequest.getDoctorId())
//                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
//                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
//                        .build())));
//        //List<AppointmentEty> appointments = appointmentRepository.findByDateAndHourAndEmployee(creationRequest.getDate(), creationRequest.getHour(), doctor);
//        List<AppointmentEty> appointmentEties = appointmentRepository.findAll();
//        List<AppointmentEty> appointments = new ArrayList<>();
//        for (AppointmentEty app: appointmentEties){
//            //app.getEmployee().getEmployeeId().equals(doctor.getEmployeeId()) &&
//            if( app.getDate().equals(creationRequest.getDate()) && app.getHour().equals(creationRequest.getHour())){
//                appointments.add(app);
//            }
//        }

//        List<AppointmentEty> appointments = appointmentRepository.findAll()
//                .stream()
//                .filter(a -> a.getEmployee().getId().equals(doctor.getEmployeeId()))
//                .filter(a -> a.getDate().equals(creationRequest.getDate()))
//                .filter(a -> a.getHour().equals(creationRequest.getHour()))
//                .collect(Collectors.toList());

//        if (!appointments.isEmpty()) {
//            // Dacă există deja programare, verificăm dacă este deja aprobată și aruncăm o excepție dacă este
//            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
//                    .errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED)
//                    .build()));
//
//        } else {
//            var newAppointment = new AppointmentEty();
//            newAppointment.setHour(creationRequest.getHour());
//            newAppointment.setDescription(creationRequest.getDescription());
//            newAppointment.setDate(creationRequest.getDate());
//            newAppointment.setDoctor(doctor);
//            appointmentRightsManager.checkCreate(newAppointment);
//
//            var instant = timeManager.instant();
//            newAppointment.setCrtTms(instant);
//            newAppointment.setMdfTms(instant);
//            newAppointment.setMdfUsr(employee.getId());
//            newAppointment.setStatus(AppointmentStatus.APPROVED);
//            newAppointment.setPatient(employee);
//            appointmentRepository.save(newAppointment);
//            // employee.addAppointment(newAppointment);
//            // employeeRepository.save(employee);
//            // List<AppointmentEty> savedAppointments = appointmentRepository.saveAll(Collections.singletonList(newAppointment));
//            // return CompletableFuture.completedFuture(savedAppointments);
//            return null;
//        }
//    }
}
