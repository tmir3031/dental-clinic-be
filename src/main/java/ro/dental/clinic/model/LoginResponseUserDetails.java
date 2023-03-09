package ro.dental.clinic.model;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseUserDetails {

    private String role;
    private String username;
    private String employeeId;
    private SpecializationDetails specializationDetails;

    @Data
    @Builder
    public static class SpecializationDetails {

        private String specializationName;
        private Long specializationId;
    }
}