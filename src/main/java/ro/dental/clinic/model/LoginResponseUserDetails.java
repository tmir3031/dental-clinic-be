package ro.dental.clinic.model;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseUserDetails {

    private String role;
    private String username;
    private String employeeId;
    private TeamDetails teamDetails;

    @Data
    @Builder
    public static class TeamDetails {

        private String teamName;
        private Long teamId;
    }
}
