package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.utils.TimeManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class EmployeeService {

//    private static final String INIT_DAYS_OFF_DESCRIPTION = "Initial number of days off for the current year";
//    public static final String TEAM_LEAD = "TEAM_LEAD";
//    private final TimeManager timeManager;
//    private final EmployeeRepository employeeRepository;
//    private final TeamRepository teamRepository;
//    private final TeamEmployeeRepository teamEmployeeRepository;
//    private final EmployeeHandler employeeHandler;
//    private final KeycloakClientApi keycloakClientApi;
//    private final SecurityAccessTokenProvider securityAccessTokenProvider;
//
//    @Transactional(readOnly = true)
//    public EmployeeDetailList getEmployeeDetails(String name) {
//        var employeeDetailList = new EmployeeDetailList();
//        var employeeDetailStream = employeeRepository.findAll().stream();
//        if (name != null) {
//            employeeDetailStream = employeeDetailStream.filter(employeeEty ->
//                    employeeEty.getFirstName().toUpperCase().contains(name.trim().toUpperCase()) ||
//                            employeeEty.getLastName().toUpperCase().contains(name.trim().toUpperCase()));
//        }
//        var currentYear = timeManager.getCurrentYear();
//        employeeDetailList.setItems(employeeDetailStream.map(employeeEty -> {
//            var employeeDetailListItem = EmployeeMapper.INSTANCE.mapEmployeeEtyToEmployeeDto(
//                    employeeEty);
//            employeeDetailListItem.setTotalVacationDays(employeeHandler.getEmployeeYearTotalDaysOff(
//                    employeeEty.getEmpYearlyDaysOffEtyList(), currentYear));
//            employeeDetailListItem.setTeamDetails(
//                    employeeHandler.handleEmployeeTeamDetails(employeeEty.getEmployeeId()));
//            return employeeDetailListItem;
//        }).collect(Collectors.toList()));
//        return employeeDetailList;
//    }
//
//
//    @Transactional
//    public void createEmployee(
//            EmployeeCreationRequest employeeCreationRequest) {
//
//        var employeeEty = EmployeeMapper.INSTANCE.mapEmployeeCreationRequestToEmployeeEty(
//                employeeCreationRequest);
//
//        var mapper = EmployeeCreationRequestToUserDetailsMapper.INSTANCE;
//        var userToBeAdded = mapper.toUserDetails(employeeCreationRequest);
//        var addedUser = keycloakClientApi.createUser(userToBeAdded);
//        employeeEty.setEmployeeId(addedUser.getId());
//        createInitialSetup(employeeEty, employeeCreationRequest.getTeamId());
//
//        var empHist = new EmpYearlyDaysOffHistEty();
//        empHist.setDescription(INIT_DAYS_OFF_DESCRIPTION);
//        empHist.setNoDays(employeeCreationRequest.getNoDaysOff());
//        empHist.setType(EmpYearlyDaysOffModifType.INCREASE);
//        createNewEmplYearlyDaysOffEty(employeeEty, empHist);
//
//        employeeRepository.save(employeeEty);
//    }
//
//    @Transactional
//    public void updateEmployee(String employeeId,
//                               EmployeeUpdateRequest employeeUpdateRequest) {
//
//        var employeeEty = getEmployee(employeeId);
//
//        if (isNull(employeeUpdateRequest.getV())) {
//            throw new BusinessException(List.of(BusinessExceptionElement.builder()
//                    .errorCode(BusinessErrorCode.INVALID_PAYLOAD)
//                    .build()));
//        }
//
//        if (employeeUpdateRequest.getV() < employeeEty.getV()) {
//            throw new BusinessException(List.of(BusinessExceptionElement.builder()
//                    .errorCode(BusinessErrorCode.EMPLOYEE_CONFLICT)
//                    .build()));
//        }
//
//        setMdfUsrAndTms(employeeEty);
//        updateEmployee(employeeEty, employeeUpdateRequest);
//
//        employeeRepository.save(employeeEty);
//    }
//
//
//    @Transactional
//    public void inactivateEmployee(String employeeId) {
//        var employeeEty = getEmployee(employeeId);
//        setMdfUsrAndTms(employeeEty);
//        employeeEty.setStatus(EmployeeStatus.INACTIVE);
//        employeeRepository.save(employeeEty);
//    }
//
//    @Transactional(readOnly = true)
//    public TeamDetailsListItem getEmployeeTeamDetails(String employeeId) {
//        return employeeHandler.handleEmployeeTeamDetails(employeeId);
//    }
//
//    private void createInitialSetup(EmployeeEty employeeEty,
//                                    Long teamId) {
//        var creationUser = securityAccessTokenProvider.getUserIdFromAuthToken();
//        var instant = timeManager.instant();
//        employeeEty.setCrtUsr(creationUser);
//        employeeEty.setCrtTms(instant);
//        employeeEty.setMdfUsr(creationUser);
//        employeeEty.setMdfTms(instant);
//        employeeEty.setStatus(EmployeeStatus.ACTIVE);
//        var savedEmployee = employeeRepository.save(employeeEty);
//        handleTeamEmployeeOnCreation(savedEmployee, teamId);
//    }
//
//    private void setMdfUsrAndTms(EmployeeEty employeeEty) {
//        employeeEty.setMdfUsr(securityAccessTokenProvider.getUserIdFromAuthToken());
//        employeeEty.setMdfTms(timeManager.instant());
//    }
//
//    private void updateEmployee(EmployeeEty employeeEty,
//                                EmployeeUpdateRequest employeeUpdateRequest) {
//        Optional.ofNullable(employeeUpdateRequest.getFirstName()).ifPresent(employeeEty::setFirstName);
//        Optional.ofNullable(employeeUpdateRequest.getLastName()).ifPresent(employeeEty::setLastName);
//        Optional.ofNullable(employeeUpdateRequest.getEmail()).ifPresent(employeeEty::setEmail);
//
//        handleTeamEmployeeOnUpdate(employeeEty, employeeUpdateRequest.getTeamId());
//        handleTeamEmployeeOnRoleUpdate(employeeEty, employeeUpdateRequest.getRole());
//
//        Optional.ofNullable(employeeUpdateRequest.getRole()).ifPresent(role -> {
//
//            UserDetails credentials = new UserDetails();
//            credentials.setId(employeeEty.getId());
//            credentials.setRole("ROLE_" + employeeEty.getRole());
//            keycloakClientApi.updateRoleOfUserWithCredentials(credentials, "ROLE_" + role);
//            employeeEty.setRole(role);
//        });
//    }
//
//    private void handleTeamEmployeeOnCreation(EmployeeEty employeeEty, Long teamId) {
//        var isTeamLeader = employeeEty.getRole().equalsIgnoreCase(TEAM_LEAD);
//
//        if (nonNull(teamId)) {
//            var teamEmployeeEty = new TeamEmployeeEty();
//            teamEmployeeEty.setEmployee(employeeEty);
//            teamEmployeeEty.setTeam(teamRepository.findById(teamId).orElseThrow(() ->
//                    new BusinessException(List.of(BusinessExceptionElement.builder()
//                            .errorCode(BusinessErrorCode.TEAM_NOT_FOUND)
//                            .build()))));
//
//            teamEmployeeEty.setIsTeamLeader(isTeamLeader);
//            teamEmployeeRepository.save(teamEmployeeEty);
//        } else if (isTeamLeader) {
//            throw new BusinessException(List.of(BusinessExceptionElement.builder()
//                    .errorCode(BusinessErrorCode.TEAM_EMPLOYEE_IS_TEAM_LEADER_NOT_ALLOWED)
//                    .build()));
//        }
//    }
//
//    private void handleTeamEmployeeOnUpdate(EmployeeEty employeeEty, Long teamId) {
//        if (nonNull(teamId)) {
//            var teamEmployeeEty = teamEmployeeRepository.findByEmployee(employeeEty)
//                    .orElse(new TeamEmployeeEty());
//            teamEmployeeEty.setEmployee(employeeEty);
//            teamEmployeeEty.setTeam(teamRepository.findById(teamId).orElseThrow(() ->
//                    new BusinessException(List.of(BusinessExceptionElement.builder()
//                            .errorCode(BusinessErrorCode.TEAM_NOT_FOUND)
//                            .build()))));
//
//            teamEmployeeEty.setIsTeamLeader(employeeEty.getRole().equalsIgnoreCase(TEAM_LEAD));
//            teamEmployeeRepository.save(teamEmployeeEty);
//        }
//    }
//
//    private void handleTeamEmployeeOnRoleUpdate(EmployeeEty employeeEty, String requestRole) {
//        if (nonNull(requestRole)) {
//            var isTeamLeader = employeeEty.getRole().equalsIgnoreCase(TEAM_LEAD);
//            var willBeTeamLeader = requestRole.equalsIgnoreCase(TEAM_LEAD);
//
//            if ((isTeamLeader && !willBeTeamLeader) || (!isTeamLeader && willBeTeamLeader)) {
//                var teamEmployeeEty = teamEmployeeRepository.findByEmployee(employeeEty)
//                        .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
//                                .errorCode(BusinessErrorCode.TEAM_EMPLOYEE_NOT_FOUND)
//                                .build()))
//                        );
//
//                teamEmployeeEty.setIsTeamLeader(willBeTeamLeader);
//                teamEmployeeRepository.save(teamEmployeeEty);
//            }
//        }
//    }
//
//    private EmployeeEty getEmployee(String employeeId) {
//        return employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new BusinessException(List.of(BusinessExceptionElement.builder()
//                        .errorCode(BusinessErrorCode.EMPLOYEE_NOT_FOUND)
//                        .build())));
//    }


}