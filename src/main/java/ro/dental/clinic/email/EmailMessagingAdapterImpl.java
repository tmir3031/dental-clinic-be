package ro.dental.clinic.email;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter responsible for email sending
 */
@RequiredArgsConstructor
public class EmailMessagingAdapterImpl implements EmailMessagingAdapter {

    private final EmailMessagingService emailMessagingService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEmailMessage(final Set<EmailRecipient> audience,
                                 final String notificationType,
                                 final Map<String, Object> inputPayload) {
        emailMessagingService.sendEmailMessage(
                mapEmailRecipientToEmailAudience(audience),
                EmailNotificationType.valueOf(notificationType),
                inputPayload);
    }

    private Set<EmailAudience> mapEmailRecipientToEmailAudience(Set<EmailRecipient> audience) {
        return Optional.ofNullable(audience)
                .map(audienceSet -> audienceSet.stream().map(aud -> EmailAudience.builder()
                                .email(aud.getEmail())
                                .preferredLanguage(aud.getPreferredLanguage())
                                .build())
                        .collect(Collectors.toSet()))
                .orElse(null);
    }
}