package ro.dental.clinic.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidManager {

    public String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}
