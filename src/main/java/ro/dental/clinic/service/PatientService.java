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

import java.util.ArrayList;
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
        // var creationUser = securityAccessTokenProvider.getUserIdFromAuthToken();
        var instant = timeManager.instant();
        patientEty.getUser().setCrtTms(instant);
        patientEty.getUser().setMdfUsr(patientEty.getId());
        patientEty.getUser().setMdfTms(instant);
        patientEty.getUser().setStatus(UserStatus.ACTIVE);
    }

    @Transactional
    public void updatePatient(String patientId, PatientUpdateRequest patientUpdateRequest) {

        var patientEty = patientRepository.findAll().stream().filter(patient -> patient.getId().equals(patientId)).findFirst().orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.USER_NOT_FOUND).build())));
        ;

        if (isNull(patientUpdateRequest.getV())) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.INVALID_PAYLOAD).build()));
        }

//        if (patientUpdateRequest.getV() < patientEty.getV()) {
//            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
//                    .errorCode(BusinessErrorCode.EMPLOYEE_CONFLICT)
//                    .build()));
//        }

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
            // Dacă există deja programare, verificăm dacă este deja aprobată și aruncăm o excepție dacă este
            for (AppointmentEty appointment : appointments) {
                if (appointment.getStatus() == AppointmentStatus.APPROVED) {
                    throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder().errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED).build()));
                }
            }
            // Dacă programarea există dar nu este aprobată, o ștergem și o recreăm
            appointmentRepository.deleteAll(appointments);
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

    public List<PatientEty> getAllPatientForADoctor(String doctorId) {
        List<AppointmentEty> appointments = appointmentRepository.findAll().stream().filter(a -> a.getDoctor().getId().equals(doctorId)).collect(Collectors.toList());
        List<PatientEty> patients = new ArrayList<>();
        appointments.forEach(appointmentEty -> patients.add(appointmentEty.getPatient()));
        return patients;

//        var appointments = appointmentRepository.findAll().stream().filter(a -> a.getDoctor().getId().equals(doctorId)).collect(Collectors.toList());
//        List <PatientEty> patientEtyList = new ArrayList<>();
//        appointments.forEach(appointmentEty -> patientEtyList.add(appointmentEty.getPatient()));
//        return patientEtyList;
//        return appointments
//                .stream()
//                .map(a -> {
//                    System.out.println(a + a.getPatient().getId()  + " aiiciiii   ");
//                    PatientEty patient = patientRepository.findById(a.getPatient().getId()).get();
//                    return patient;
//                })
//                .distinct()
//                .collect(Collectors.toList());
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
