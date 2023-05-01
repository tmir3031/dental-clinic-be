package ro.dental.clinic.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRecipient {
    private String email;
    private String preferredLanguage;
}
