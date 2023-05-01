package ro.dental.clinic.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailAudience {
    private String email;
    private String preferredLanguage;

}
