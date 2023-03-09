package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.dental.clinic.domain.EmployeeRepository;
import ro.dental.clinic.mapper.EmployeeMapper;
import ro.dental.clinic.model.EmployeeCreationRequest;
import ro.dental.clinic.model.EmployeeDetailList;
import ro.dental.clinic.model.SpecializationDetailsListItem;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeHandler employeeHandler;
    private final EmployeeRepository employeeRepository;

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

        employeeRepository.save(employeeEty);
    }
}
