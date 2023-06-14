package ro.dental.clinic.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum BusinessErrorCode {
    USER_NOT_FOUND("EDOT0001400", "Invalid userId"),
    KEYCLOAK_LOGIN_INVALID_CREDENTIALS("EDOT0004400", "Invalid credentials!"),
    KEYCLOAK_LOGIN_GEN("EDOT0004503", "Failed logging in!"),
    APPOINTMENT_NOT_FOUND("EDOT0005400", "Invalid appointment"),
    APPOINTMENT_DELETE_NOT_PERMITTED_APPROVED_AND_DAYS_IN_PAST("EDOT0009400", "This appointment cannot be deleted because it has approved days off that are in the past"),
    APPOINTMENT_DELETE_NOT_PERMITTED_REJECTED_APPOINTMENT("EDOT0010405", "The appointment cannot be deleted because is rejected."),
    PAST_PERIOD("EDOT0014400", "Cannot create an appointment in the past"),
    APPOINTMENT_TREATMENT_IS_NULL("EDOT018400", "Treatment for an appointment cannot be null"),
    INVALID_PAYLOAD("EDOT0021400", "Version of updated users cannot be null"),
    USER_ROLE_NOT_FOUND("EDOT0023400", "Invalid role"),
    USER_USERNAME_CONFLICT("EDOT0024400", "User username is already used"),
    KEYCLOAK_TOKEN_DETAILS_MISSING_USER_ID("EDOT0025404", "User id not found in token details!"),
    APPOINTMENT_ALREADY_APPROVED("EDOT0026400", "The time slot for the chosen doctor is not available"),
    IMAGE_NOT_SAVED("EDOTI0026400", "Radiography not save"),
    IMAGE_NOT_EXIST("EDOTI0026400", "Radiography not exist"),
    A_FUTURE_APPOINTMENT_CANNOT_BE_CHANGED("EDOT002700", "A future appointment cannot be changed");

    private final String errorCode;
    private final String devMsg;
    private final HttpStatus status;

    BusinessErrorCode(String errorCode, String devMsg) {
        this.errorCode = errorCode;
        this.devMsg = devMsg;
        this.status = HttpStatus.resolve(Integer.parseInt(errorCode.substring(errorCode.length() - 3)));
    }
}