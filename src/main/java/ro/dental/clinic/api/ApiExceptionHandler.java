package ro.dental.clinic.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.model.ErrorDetails;
import ro.dental.clinic.model.ErrorDetails.ErrorDetailsItem;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(BusinessException exception) {

        Optional<HttpStatus> serverStatusError = exception.getErrors().stream()
                .filter(errorItem -> errorItem.getErrorCode().getStatus().is5xxServerError())
                .map(errorItem -> errorItem.getErrorCode().getStatus()).findAny();

        var status = serverStatusError.orElseGet(() -> exception.getErrors().stream()
                .filter(errorItem -> errorItem.getErrorCode().getStatus().is4xxClientError())
                .map(errorItem -> errorItem.getErrorCode().getStatus())
                .findAny()
                .orElse(HttpStatus.BAD_REQUEST));
        var errorDetails = new ErrorDetails();
        errorDetails.setErrors(exception.getErrors().stream()
                .map(e -> ErrorDetailsItem.builder()
                        .errorCode(e.getErrorCode().name())
                        .build()).collect(Collectors.toList()));

        return ResponseEntity.status(status)
                .body(errorDetails);
    }
}