package ro.dental.clinic.email;

import java.util.Map;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;

/**
 * Adapter interface responsible for email sending
 */
public interface EmailMessagingAdapter {

    /**
     * Method used for email sending
     *
     * @param audience         the email and calendar of the recipient
     * @param notificationType the notification type identifying the email template
     * @param inputPayload     the variable context of the email template identified by {@code notificationType}
     * @throws IllegalArgumentException if {@code audience} is null
     */
    @Async("emailSendingExecutorService")
    void sendEmailMessage(Set<EmailRecipient> audience,
                          String notificationType,
                          Map<String, Object> inputPayload);
}