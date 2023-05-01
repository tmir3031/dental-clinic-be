package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import ro.dental.clinic.email.EmailMessagingAdapter;
import ro.dental.clinic.email.EmailRecipient;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class AppointmentHandler {

    private static final String NOTIFICATION_TYPE = "APPOINTMENT_CREATION";
    private final EmailMessagingAdapter emailMessagingAdapter;
    public void sendNotificationApprovedAppointment(String firstName, String lastName, String hour, LocalDate date, String email, String preferredLanguage) {
        var audience = Set.of(EmailRecipient.builder().email(email).preferredLanguage(preferredLanguage).build());
        emailMessagingAdapter.sendEmailMessage(audience, NOTIFICATION_TYPE, Map.of(firstName,lastName,hour,date));
    }
}
