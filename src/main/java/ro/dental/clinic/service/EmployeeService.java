package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.*;
import ro.dental.clinic.enums.AppointmentStatus;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.enums.EmployeeStatus;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.exceptions.BusinessException.BusinessExceptionElement;
import ro.dental.clinic.mapper.AppointmentMapper;
import ro.dental.clinic.mapper.EmployeeCreationRequestToUserDetailsMapper;
import ro.dental.clinic.mapper.EmployeeMapper;
import ro.dental.clinic.model.*;
import ro.dental.clinic.utils.TimeManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final AppointmentRightsManager appointmentRightsManager;
    private final EmployeeHandler employeeHandler;
    private final EmployeeRepository employeeRepository;
    private final TimeManager timeManager;

    private final AppointmentRepository appointmentRepository;

    private final SpecializationUserRepository specializationUserRepository;
    private final SpecializationRepository specializationRepository;
    private final KeycloakClientApi keycloakClientApi;

    private final SecurityAccessTokenProvider securityAccessTokenProvider;

    @Transactional(readOnly = true)
    public SpecializationDetailsListItem getEmployeeSpecializationDetails(String employeeId) {
        return employeeHandler.handleEmployeeSpecializationDetails(employeeId);
    }

    @Transactional
    public EmployeeDetailList getEmployeeDetails() {
        var employeeDetailList = new EmployeeDetailList();
        var employeeDetailStream = employeeRepository.findAll().stream();
//        final String role = "DOCTOR";
//        employeeDetailStream = employeeDetailStream.filter(employeeEty ->
//                employeeEty.getRole().toUpperCase().contains(role.trim().toUpperCase()));
//
        employeeDetailList.setItems(employeeDetailStream.map(employeeEty -> {
            var employeeDetailListItem = EmployeeMapper.INSTANCE.mapEmployeeEtyToEmployeeDto(
                    employeeEty);
            employeeDetailListItem.setSpecializationDetailsListItem(
                    employeeHandler.handleEmployeeSpecializationDetails(employeeEty.getEmployeeId()));
            return employeeDetailListItem;
        }).collect(Collectors.toList()));
        System.out.println(employeeDetailList);
        return employeeDetailList;
    }

    @Transactional
    public void createEmployee(
            EmployeeCreationRequest employeeCreationRequest) {

        var employeeEty = EmployeeMapper.INSTANCE.mapEmployeeCreationRequestToEmployeeEty(
                employeeCreationRequest);

        var mapper = EmployeeCreationRequestToUserDetailsMapper.INSTANCE;
        var userToBeAdded = mapper.toUserDetails(employeeCreationRequest);
        var addedUser = keycloakClientApi.createUser(userToBeAdded);
        employeeEty.setEmployeeId(addedUser.getId());
        createInitialSetup(employeeEty, employeeCreationRequest.getSpecializationId());
        employeeRepository.save(employeeEty);
    }

    private void createInitialSetup(EmployeeEty employeeEty,
                                    Long specializationId) {
        var creationUser = securityAccessTokenProvider.getUserIdFromAuthToken();
        var instant = timeManager.instant();
        employeeEty.setCrtUsr(creationUser);
        employeeEty.setCrtTms(instant);
        employeeEty.setMdfUsr(creationUser);
        employeeEty.setMdfTms(instant);
        employeeEty.setStatus(EmployeeStatus.ACTIVE);
        employeeEty.setDescription("");
        var savedEmployee = employeeRepository.save(employeeEty);
        handleSpecializationEmployeeOnCreation(savedEmployee, specializationId);
    }

    private void handleSpecializationEmployeeOnCreation(EmployeeEty employeeEty, Long specializationId) {
        if (nonNull(specializationId)) {
            var teamEmployeeEty = new SpecializationUserEty();
            teamEmployeeEty.setEmployee(employeeEty);
            teamEmployeeEty.setSpecializationEty(specializationRepository.findById(specializationId).orElseThrow(() ->
                    new BusinessException(List.of(BusinessExceptionElement.builder()
                            .errorCode(BusinessErrorCode.TEAM_NOT_FOUND)
                            .build()))));

            specializationUserRepository.save(teamEmployeeEty);
        }
    }

    @Transactional
    public void deleteAppointment(String employeeId, Long requestId) {
        var employee = getEmployee(employeeId);

        var appointment = getAppointment(employee, requestId);
        appointmentRightsManager.checkDelete(appointment);
        employee.removeAppointment(appointment);
        employeeRepository.save(employee);
    }

    private EmployeeEty getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
    }

    private AppointmentEty getAppointment(EmployeeEty employee, Long requestId) {
        return employee.getAppointmentEtyList().stream()
                .filter(appointmentEty -> appointmentEty.getId()
                        .equals(requestId)).findFirst()
                .orElseThrow(() -> new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.APPOINTMENT_NOT_FOUND)
                        .build())));
    }

    //    @Transactional
//    public void createAppointment(String employeeId,
//                                   AppointmentCreationRequest creationRequest) {
//        var employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
//                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
//                        .build())));
//
//        var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(
//                creationRequest);
//        newAppointment.setEmployee(employee);
//        appointmentRightsManager.checkCreate(newAppointment);
//
//        var instant = timeManager.instant();
//        newAppointment.setCrtTms(instant);
//        newAppointment.setCrtUsr(employee.getId());
//        newAppointment.setMdfTms(instant);
//        newAppointment.setMdfUsr(employee.getId());
//        newAppointment.setStatus(AppointmentStatus.PENDING);
//        newAppointment.setEmployee(employee);
//        employee.addAppointment(newAppointment);
//        employeeRepository.save(employee);
//    }
    @Transactional
    public void createAppointment(String employeeId, AppointmentCreationRequest creationRequest) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        var doctor = employeeRepository.findById(creationRequest.getDoctorId())
                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        List<AppointmentEty> appointments = appointmentRepository.findByDateAndHourAndEmployee(creationRequest.getDate(), creationRequest.getHour(), doctor);

        if (!appointments.isEmpty()) {
            // Dacă există deja programare, verificăm dacă este deja aprobată și aruncăm o excepție dacă este
            for (AppointmentEty appointment : appointments) {
                if (appointment.getStatus() == AppointmentStatus.APPROVED) {
                    throw new BusinessException(List.of(BusinessExceptionElement.builder()
                            .errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED)
                            .build()));
                }
            }
            // Dacă programarea există dar nu este aprobată, o ștergem și o recreăm
            appointmentRepository.deleteAll(appointments);
        }

        var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(
                creationRequest);
        newAppointment.setEmployee(doctor);
        appointmentRightsManager.checkCreate(newAppointment);

        var instant = timeManager.instant();
        newAppointment.setCrtTms(instant);
        newAppointment.setCrtUsr(employee.getId());
        newAppointment.setMdfTms(instant);
        newAppointment.setMdfUsr(employee.getId());
        newAppointment.setStatus(AppointmentStatus.APPROVED);
        newAppointment.setRejectReason("ceva");
        employee.addAppointment(newAppointment);
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(String employeeId,
                               EmployeeUpdateRequest employeeUpdateRequest) {

        var employeeEty = getEmployee(employeeId);

        if (isNull(employeeUpdateRequest.getV())) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.INVALID_PAYLOAD)
                    .build()));
        }

        if (employeeUpdateRequest.getV() < employeeEty.getV()) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.EMPLOYEE_CONFLICT)
                    .build()));
        }

        setMdfUsrAndTms(employeeEty);
        updateEmployee(employeeEty, employeeUpdateRequest);

        employeeRepository.save(employeeEty);
    }

    private void setMdfUsrAndTms(EmployeeEty employeeEty) {
        employeeEty.setMdfUsr(securityAccessTokenProvider.getUserIdFromAuthToken());
        employeeEty.setMdfTms(timeManager.instant());
    }

    private void updateEmployee(EmployeeEty employeeEty,
                                EmployeeUpdateRequest employeeUpdateRequest) {
        Optional.ofNullable(employeeUpdateRequest.getFirstName()).ifPresent(employeeEty::setFirstName);
        Optional.ofNullable(employeeUpdateRequest.getLastName()).ifPresent(employeeEty::setLastName);
        Optional.ofNullable(employeeUpdateRequest.getEmail()).ifPresent(employeeEty::setEmail);

//        handleSpecializationEmployeeOnUpdate(employeeEty, employeeUpdateRequest.getSpecializationId());
//        handleSpecializationEmployeeOnRoleUpdate(employeeEty, employeeUpdateRequest.getRole());

        Optional.ofNullable(employeeUpdateRequest.getRole()).ifPresent(role -> {

            UserDetails credentials = new UserDetails();
            credentials.setId(employeeEty.getId());
            credentials.setRole("ROLE_" + employeeEty.getRole());
            keycloakClientApi.updateRoleOfUserWithCredentials(credentials, "ROLE_" + role);
            employeeEty.setRole(role);
        });
    }

    // ------------------------------------------------------------------------------------------------------------------
    // Threads
    @Async
    public CompletableFuture<List<AppointmentEty>> getAllAppointments() {
        final List<AppointmentEty> cars = appointmentRepository.findAll();
        return CompletableFuture.completedFuture(cars);
    }

    @Async
    public CompletableFuture<List<AppointmentEty>> saveAppointment(String employeeId, AppointmentCreationRequest creationRequest) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        var doctor = employeeRepository.findById(creationRequest.getDoctorId())
                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));
        //List<AppointmentEty> appointments = appointmentRepository.findByDateAndHourAndEmployee(creationRequest.getDate(), creationRequest.getHour(), doctor);
        List<AppointmentEty> appointmentEties = appointmentRepository.findAll();
        List<AppointmentEty> appointments = new ArrayList<>();
        for (AppointmentEty app: appointmentEties){
            //app.getEmployee().getEmployeeId().equals(doctor.getEmployeeId()) &&
            if( app.getDate().equals(creationRequest.getDate()) && app.getHour().equals(creationRequest.getHour())){
                appointments.add(app);
            }
        }
        System.out.println(appointmentEties);
        System.out.println(creationRequest);

//        List<AppointmentEty> appointmentsD = appointmentRepository.findAll()
//                .stream()
//                .filter(a -> a.getEmployee().getId().equals(doctor.getEmployeeId()))
//                .collect(Collectors.toList());
//
//        List<AppointmentEty> appointmentsDate = appointmentRepository.findAll()
//                .stream()
//                .filter(a -> a.getDate().isEqual(creationRequest.getDate()))
//                .collect(Collectors.toList());
//
//        List<AppointmentEty> appointments = appointmentRepository.findAll()
//                .stream()
//                .filter(a -> a.getEmployee().getId().equals(doctor.getEmployeeId()))
//                .filter(a -> a.getDate().equals(creationRequest.getDate()))
//                .filter(a -> a.getHour().equals(creationRequest.getHour()))
//                .collect(Collectors.toList());

        if (!appointments.isEmpty()) {
            // Dacă există deja programare, verificăm dacă este deja aprobată și aruncăm o excepție dacă este
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.APPOINTMENT_ALREADY_APPROVED)
                    .build()));

        } else {
            var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(
                    creationRequest);
            newAppointment.setEmployee(doctor);
            appointmentRightsManager.checkCreate(newAppointment);

            var instant = timeManager.instant();
            newAppointment.setCrtTms(instant);
            newAppointment.setCrtUsr(employee.getId());
            newAppointment.setMdfTms(instant);
            newAppointment.setMdfUsr(employee.getId());
            newAppointment.setStatus(AppointmentStatus.APPROVED);
            newAppointment.setRejectReason("ceva");
            // employee.addAppointment(newAppointment);
            // employeeRepository.save(employee);
            List<AppointmentEty> savedAppointments = appointmentRepository.saveAll(Collections.singletonList(newAppointment));
            return CompletableFuture.completedFuture(savedAppointments);
        }
    }
}
