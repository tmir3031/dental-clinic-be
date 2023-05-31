package ro.dental.clinic.service;

import ro.dental.clinic.model.UserDetails;

public interface KeycloakPasswordGenerator {

    String generatePassword(UserDetails userDetails);
}
