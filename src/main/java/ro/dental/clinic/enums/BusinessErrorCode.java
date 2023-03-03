package ro.dental.clinic.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum BusinessErrorCode {
    EMPLOYEE_NOT_FOUND("EDOT0001400", "Invalid employeeId"),
    EMPLOYEE_CONFLICT("EDOT0002409", "Conflict of employee's versions"),
    EMPLOYEE_YEARLY_DAYS_OFF_DECREASE_NOT_PERMITTED("EDOT0004400", "This employee doesn't have the number of vacation days set for this year. They cannot be decreased"),
    KEYCLOAK_LOGIN_INVALID_CREDENTIALS("EDOT0004400", "Invalid credentials!"),
    KEYCLOAK_LOGIN_GEN("EDOT0004503", "Failed logging in!"),
    LEGALLY_DAYS_OFF_INVALID_REQUEST("EDOT0016400", "Invalid period of time"),
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