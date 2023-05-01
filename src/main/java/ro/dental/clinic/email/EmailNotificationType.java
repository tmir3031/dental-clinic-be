package ro.dental.clinic.email;

/**
 * Possible types of email notifications
 */
public enum EmailNotificationType {
    APPOINTMENT_CREATION("appointement-creation-notif");
    private final String notificationIdentifier;

    EmailNotificationType(String notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }

    String getNotificationIdentifier() {
        return this.notificationIdentifier;
    }
}
