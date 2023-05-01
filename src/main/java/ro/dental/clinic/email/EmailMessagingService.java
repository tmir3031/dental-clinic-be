package ro.dental.clinic.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for email sending
 */
@RequiredArgsConstructor
public class EmailMessagingService {

    private final EmailMessagingProperties emailMessagingProperties;
    private final EmailTemplatesService emailTemplatesService;
    private final JavaMailSender javaMailSender;

    /**
     * Method used for email sending
     *
     * @param audience         the email and calendar of the recipient
     * @param notificationType the notification type identifying the email template
     * @param inputPayload     the variable context of the email template identified by {@code notificationType}
     * @throws IllegalArgumentException if {@code audience} is null
     * @throws EmailMessagingException  if email sending fails
     */
    public void sendEmailMessage(final Set<EmailAudience> audience,
                                 final EmailNotificationType notificationType,
                                 final Map<String, Object> inputPayload) {
        Preconditions.checkArgument(audience != null && !audience.isEmpty(), "audience could not be null or empty");
        var payload = new HashMap<>(inputPayload);
        audience.forEach(emailAudience -> {
            payload.put("lang", emailAudience.getPreferredLanguage());
            sendEmail(emailAudience.getEmail(), notificationType, payload);
        });
    }

    private void sendEmail(String email, EmailNotificationType notificationType, Map<String, Object> inputPayload) {
        var emailSections =
                emailTemplatesService.processTemplate(notificationType.getNotificationIdentifier(), inputPayload);

        var mailMessage = new SimpleMailMessage();
        mailMessage.setText(emailSections.get("body"));
        mailMessage.setSubject(emailSections.get("subject"));
        mailMessage.setTo(email);
        mailMessage.setFrom(emailMessagingProperties.getMailFrom());

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException emailException) {
            throw new EmailMessagingException(String.format("Failed to send email to %s.", email), emailException);
        }
    }
}
