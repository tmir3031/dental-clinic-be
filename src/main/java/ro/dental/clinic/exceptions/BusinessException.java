package ro.dental.clinic.exceptions;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import ro.dental.clinic.enums.BusinessErrorCode;

import java.util.List;
import java.util.Map;

public class BusinessException extends RuntimeException {

    @Getter
    private final List<BusinessExceptionElement> errors;

    public BusinessException(Throwable cause, List<BusinessExceptionElement> errors) {
        super(cause);
        this.errors = errors;
    }

    public BusinessException(List<BusinessExceptionElement> errors) {
        this(null, errors);
    }

    @Getter
    @Setter
    @Builder
    public static class BusinessExceptionElement {

        @JsonUnwrapped
        private final BusinessErrorCode errorCode;

        @Singular
        private final Map<String, Object> contextVariables;
    }
}