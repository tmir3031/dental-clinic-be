package ro.dental.clinic.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum BusinessErrorCode {
    EMPLOYEE_NOT_FOUND("EDOT0001400", "Invalid employeeId"),
    EMPLOYEE_CONFLICT("EDOT0002409", "Conflict of employee's versions"),
    TEAM_NOT_FOUND("EDOT0003400", "Invalid teamId"),
    EMPLOYEE_YEARLY_DAYS_OFF_DECREASE_NOT_PERMITTED("EDOT0004400", "This employee doesn't have the number of vacation days set for this year. They cannot be decreased"),
    KEYCLOAK_LOGIN_INVALID_CREDENTIALS("EDOT0004400", "Invalid credentials!"),
    KEYCLOAK_LOGIN_GEN("EDOT0004503", "Failed logging in!"),
    APPOINTMENT_NOT_FOUND("EDOT0005400", "Invalid leave request"),
    LEAVE_REQUEST_IS_REJECTED("EDOT0006405", "This leave request is already rejected"),
    LEAVE_REQUEST_IS_IN_THE_PAST("EDOT0007405", "This leave request is in the past"),
    LEAVE_REQUEST_CONFLICT("EDOT0008409", "Conflict of employee's versions"),
    APPOINTMENT_DELETE_NOT_PERMITTED_APPROVED_AND_DAYS_IN_PAST("EDOT0009400", "This leave request cannot be deleted because it has approved days off that are in the past"),
    APPOINTMENT_DELETE_NOT_PERMITTED_REJECTED_APPOINTMENT("EDOT0010405", "The leave request cannot be deleted because is rejected."),
    INVALID_PERIOD("EDOT0011400", "Invalid period"),
    START_DATE_END_DATE_DIFFERENT_YEARS("EDOT0012400", "Start date and end date of a leave request must be in the same year"),
    START_DATE_AFTER_END_DATE("EDOT0013400", "Start date must be before end date"),
    PAST_PERIOD("EDOT0014400", "Cannot create a leave request in the past"),
    LEAVE_REQUEST_LONGER_THAN_NO_OF_DAYS_OFF("EDOT0015400", "Leave request cannot be longer than number of days off"),
    LEGALLY_DAYS_OFF_INVALID_REQUEST("EDOT0016400", "Invalid period of time"),
    LEAVE_REQUEST_METHOD_NOT_ALLOWED("EDOT017405","Invalid status"),
    LEAVE_REQUEST_REJECTION_REASON_IS_NULL("EDOT018400", "Rejection reason cannot be null"),
    TEAM_EMPLOYEE_IS_TEAM_LEADER_NOT_ALLOWED("EDOT0019405", "Role of team leader not allowed without team"),
    TEAM_EMPLOYEE_NOT_FOUND("EDOT0020400", "Relation between team and employee not found"),
    INVALID_PAYLOAD("EDOT0021400", "Version of updated employee cannot be null"),
    DATES_NOT_FOUND("EDOT0022400", "The dates cannot be null"),
    EMPLOYEE_ROLE_NOT_FOUND("EDOT0023400", "Invalid role"),
    EMPLOYEE_USERNAME_CONFLICT("EDOT0024400", "Employee username is already used"),
    KEYCLOAK_TOKEN_DETAILS_MISSING_USER_ID("EDOT0025404", "User id not found in token details!");

    private final String errorCode;
    private final String devMsg;
    private final HttpStatus status;

    BusinessErrorCode(String errorCode, String devMsg) {
        this.errorCode = errorCode;
        this.devMsg = devMsg;
        this.status = HttpStatus.resolve(Integer.parseInt(errorCode.substring(errorCode.length() - 3)));
    }
}