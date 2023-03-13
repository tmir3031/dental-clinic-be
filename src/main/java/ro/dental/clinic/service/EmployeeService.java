package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.*;
import ro.dental.clinic.enums.AppointmentStatus;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.enums.EmployeeStatus;
import ro.dental.clinic.exceptions.BusinessException.BusinessExceptionElement;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.mapper.AppointmentMapper;
import ro.dental.clinic.mapper.EmployeeCreationRequestToUserDetailsMapper;
import ro.dental.clinic.mapper.EmployeeMapper;
import ro.dental.clinic.model.AppointmentCreationRequest;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.EmployeeDetailList;
import ro.dental.clinic.model.SpecializationDetailsListItem;
import ro.dental.clinic.utils.TimeManager;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final AppointmentRightsManager appointmentRightsManager;
    private final EmployeeHandler employeeHandler;
    private final EmployeeRepository employeeRepository;
    private final TimeManager timeManager;

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
        final String role = "DOCTOR";
        var employeeDetailStream = employeeRepository.findAll().stream();
        if (role != null) {
            employeeDetailStream = employeeDetailStream.filter(employeeEty ->
                    employeeEty.getRole().toUpperCase().contains(role.trim().toUpperCase()));
        }
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
        var employee =  getEmployee(employeeId);

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

    @Transactional
    public void createAppointment(String employeeId,
                                   AppointmentCreationRequest creationRequest) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
                        .build())));

        var newAppointment = AppointmentMapper.INSTANCE.mapAppointmentCreationRequestToAppointmentEty(
                creationRequest);
        newAppointment.setEmployee(employee);
        appointmentRightsManager.checkCreate(newAppointment);

        var instant = timeManager.instant();
        newAppointment.setCrtTms(instant);
        newAppointment.setCrtUsr(employee.getId());
        newAppointment.setMdfTms(instant);
        newAppointment.setMdfUsr(employee.getId());
        newAppointment.setStatus(AppointmentStatus.PENDING);
        newAppointment.setEmployee(employee);
        employee.addAppointment(newAppointment);
        employeeRepository.save(employee);
    }
}
