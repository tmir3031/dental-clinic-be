package ro.dental.clinic.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ErrorDetails {

    private List<ErrorDetailsItem> errors;

    @Builder
    @Data
    public static class ErrorDetailsItem {

        private String message;
        private String errorCode;
        private Map<String, Object> contextVariables;
    }
}
