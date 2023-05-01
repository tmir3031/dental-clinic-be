package ro.dental.clinic.email;

import org.springframework.scheduling.annotation.Async;

/**
 * Adapter interface responsible for email notification
 */
public interface EmailNotificationAdapter {

    @Async("emailSendingExecutorService")
    void notifyAboutEventsDiscarded(EmployeeId employeeId);
}