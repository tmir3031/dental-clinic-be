package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ro.dental.clinic.domain.AppointmentEty;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.exceptions.BusinessException.BusinessExceptionElement;
import ro.dental.clinic.utils.TimeManager;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static ro.dental.clinic.enums.AppointmentStatus.APPROVED;
import static ro.dental.clinic.enums.AppointmentStatus.REJECTED;

@Component
@RequiredArgsConstructor
public class AppointmentRightsManager {

    private final TimeManager timeManager;

    public AppointmentRightsManager() {
        timeManager = new TimeManager(Clock.systemDefaultZone());
    }

    public void checkDelete(AppointmentEty appointmentEty) {
        if (appointmentEty.getStatus() == REJECTED) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.APPOINTMENT_DELETE_NOT_PERMITTED_REJECTED_APPOINTMENT)
                    .build()));
        }

        if ((appointmentEty.getStatus() == APPROVED)
                && appointmentEty.getDate().getMonth().getValue()
                < timeManager.localDate().getMonth().getValue()) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.APPOINTMENT_DELETE_NOT_PERMITTED_APPROVED_AND_DAYS_IN_PAST)
                    .build()));
        }
    }

    public void checkCreate(AppointmentEty appointmentEty) {
        var date = appointmentEty.getDate();

        var currentDate = timeManager.localDate();
        if (date.getYear() < currentDate.getYear()
                || (date.getYear() == currentDate.getYear() && date.getMonthValue() < currentDate.getMonthValue())) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder().errorCode(
                    BusinessErrorCode.PAST_PERIOD).build()));
        }

    }

}