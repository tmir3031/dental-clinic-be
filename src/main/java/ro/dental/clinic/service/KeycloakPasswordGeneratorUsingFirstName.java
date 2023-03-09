package ro.dental.clinic.service;

import org.springframework.stereotype.Service;
import ro.dental.clinic.model.UserDetails;

@Service
public class KeycloakPasswordGeneratorUsingFirstName implements KeycloakPasswordGenerator {
    @Override
    public String generatePassword(UserDetails userDetails) {
        return userDetails.getFirstName();
    }
}
