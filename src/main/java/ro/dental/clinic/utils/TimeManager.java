package ro.dental.clinic.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TimeManager {

    private final Clock clock;

    public Instant instant() {
        return clock.instant();
    }

    public LocalDate localDate() {
        return LocalDate.ofInstant(clock.instant(), ZoneId.systemDefault());
    }

    public Date date() {
        return Date.from(clock.instant());
    }

}
