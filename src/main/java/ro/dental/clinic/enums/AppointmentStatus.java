package ro.dental.clinic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppointmentStatus {
    PENDING(1),
    APPROVED(2),
    REJECTED(3);

    /**
     * Number representing the order in which the corresponding leave request status is chosen when we
     * need to sort leave request by status.
     */
    private final int priority;
}
