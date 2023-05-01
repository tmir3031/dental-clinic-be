package ro.dental.clinic.email;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;


/**
 * Application service responsible for detecting situations (based on domain
 * events) which require email notifications
 */
@Slf4j
public class EmailNotificationApplicationService implements EmailNotificationAdapter {

    private final EmailMessagingService emailMessagingService;
    private final EmployeeReadModelInformationProvider employeeReadModelProvider;
    private final LeaveRequestDao leaveRequestDao;

    private final Consumer<DomainEvent> doNothing = event -> {
    };

    /**
     * Returns the display name for an employee
     * If name and surname are present, the full name will be returned, otherwise the username will be returned
     */
    private final Function<EmployeeReadModel, String> getEmployeeDisplayName = e -> {
        var fullName = Joiner.on(' ').skipNulls().join(e.getName(), e.getSurname());
        return fullName.isBlank() ? e.getAuthenticationDetails().getUsername() : fullName;
    };


    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EmailNotificationApplicationService(
            EmailMessagingService emailMessagingService,
            EmployeeReadModelInformationProvider employeeReadModelProvider,
            LeaveRequestDao leaveRequestDao) {
        this.emailMessagingService = emailMessagingService;
        this.employeeReadModelProvider = employeeReadModelProvider;
        this.leaveRequestDao = leaveRequestDao;
    }

    @EventListener(DomainEvent.class)
    @Transactional
    @Async("emailSendingExecutorService")
    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    public void onEvent(DomainEvent event) {
        log.info("Received event " + event.toString());
        switch (event) {
            case DaysOffLimitDefined limitDefined -> notifyAboutDaysOffLimitDefined(limitDefined);
            case RemainingDaysOffDeducted daysDeducted -> { }
            case RemainingDaysOffReturned daysReturned -> { }
            case LeaveRequested leaveRequested -> notifyAboutLeaveRequested(leaveRequested);
            case LeaveAccepted leaveAccepted -> notifyAboutLeaveAccepted(leaveAccepted);
            case LeaveCancelled leaveCancelled -> notifyAboutLeaveCancelled(leaveCancelled);
            case LeaveRejected leaveRejected -> notifyAboutLeaveRejected(leaveRejected);
            case LeaveUpdated leaveUpdated -> { }
            case LeaveMarkedAsPending leaveMarkedAsPending -> notifyAboutLeaveMarkedAsPending(leaveMarkedAsPending);
            case DaysOffLimitIncreased limitIncreased -> notifyAboutDaysOffLimitIncreased(limitIncreased);
            case DaysOffLimitDecreased limitDecreased -> notifyAboutDaysOffLimitDecreased(limitDecreased);
        }
    }

    public void notifyAboutEventsDiscarded(EmployeeId employeeId) {
        var employee = getActiveEmployee(employeeId);
        if (employee == null) {
            return;
        }

        emailMessagingService.sendEmailMessage(
                Set.of(buildEmailAudience(employee.getEmail(), employee.getPreferredLanguage())),
                EmailNotificationType.DISCARDED_EVENTS,
                Map.of());
    }

    private void notifyAboutLeaveRequested(LeaveRequested event) {

        var requester = employeeReadModelProvider.getEmployee(event.employeeId());

        var eventYear = event.calendarYear().year();
        final Map<String, Object> emailPayload = Map.of(
                EMPLOYEE_DISPLAY_NAME, getEmployeeDisplayName.apply(requester),
                LEAVE_TYPE, event.type(),
                START_DATE, LocalDate.of(eventYear, event.leaveStart().month(), event.leaveStart().day()),
                END_DATE, LocalDate.of(eventYear, event.leaveEnd().month(), event.leaveEnd().day()),
                NUM_DAYS_OFF, event.deductedDaysOff());

        var audience = getAudienceForEmployeeAction(requester);

        emailMessagingService.sendEmailMessage(
                audience,
                EmailNotificationType.LEAVE_REQUEST_CREATION,
                emailPayload);
    }

    private void notifyAboutLeaveAccepted(LeaveAccepted event) {
        var leaveRequested = getLeaveRequested(event.leaveId());

        var employee = getActiveEmployee(event.employeeId());
        if (employee == null) {
            return;
        }
        var accountable = employeeReadModelProvider.getEmployee(event.acceptor());

        final Map<String, Object> emailPayload = Map.of(
                START_DATE, leaveRequested.getStartDate(),
                END_DATE, leaveRequested.getEndDate(),
                ACCOUNTABLE_NAME, getEmployeeDisplayName.apply(accountable));

        emailMessagingService.sendEmailMessage(
                Set.of(buildEmailAudience(employee.getEmail(), employee.getPreferredLanguage())),
                EmailNotificationType.LEAVE_REQUEST_ACCEPTANCE,
                emailPayload);
    }

    private EmployeeReadModel getActiveEmployee(EmployeeId employeeId) {
        var employee = employeeReadModelProvider.getEmployee(employeeId);
        if (!EmployeeStatus.ACTIVE.name().equals(employee.getStatus())) {
            log.warn("The employee with id {} will not be notified by email because he is not ACTIVE", employeeId.value());
            employee = null;
        }
        return employee;
    }

    private LeaveRequestReadModel getLeaveRequested(LeaveRequestId event) {
        return leaveRequestDao.findById(event.value())
                .orElseThrow(() -> new LeaveScheduleException(
                        LeaveScheduleErrorCode.LEAVE_REQUEST_NOT_FOUND,
                        Map.of("leaveRequestId", event.value())));
    }

    private Set<EmailAudience> getAudienceForEmployeeAction(EmployeeReadModel employee) {
        var audience = employeeReadModelProvider.getHR()
                .stream()
                .map(hr -> buildEmailAudience(hr.getEmail(), hr.getPreferredLanguage()))
                .collect(Collectors.toSet());

        employeeReadModelProvider.getTeamLeaderForTeam(employee.getTeamId())
                .filter(tl -> !employee.getId().equals(tl.getId()))
                .ifPresent(tl -> audience.add(buildEmailAudience(tl.getEmail(), tl.getPreferredLanguage())));
        return audience;
    }

    private EmailAudience buildEmailAudience(String email, String preferredLanguage) {
        return EmailAudience.builder()
                .email(email)
                .preferredLanguage(preferredLanguage)
                .build();
    }
}
