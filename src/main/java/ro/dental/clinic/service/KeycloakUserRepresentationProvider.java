package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.dental.clinic.config.KeycloakProperties;
import ro.dental.clinic.mapper.UserDtoToKeycloakUserRepresentationMapper;
import ro.dental.clinic.model.KeycloakUserCredentials;
import ro.dental.clinic.model.KeycloakUserRepresentation;
import ro.dental.clinic.model.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserRepresentationProvider {

    private final KeycloakPasswordGenerator passwordGenerator;
    private final KeycloakProperties keycloakProperties;

    public KeycloakUserRepresentation createKeycloakUserRepresentationFromDto(UserDetails userDetails) {
        String password = userDetails.getPassword();
        KeycloakUserCredentials keycloakCredentials = new KeycloakUserCredentials();
        keycloakCredentials.setType(keycloakProperties.getUserRealm().getGrantType());
        keycloakCredentials.setValue(password);

        List<KeycloakUserCredentials> listOfCredentialsOfUser = new ArrayList<>();
        listOfCredentialsOfUser.add(keycloakCredentials);

        var userDtoToKeycloakUserMapper = UserDtoToKeycloakUserRepresentationMapper.INSTANCE;
        ;
        var keycloakUser = userDtoToKeycloakUserMapper.toKeycloakUserRepresentation(userDetails);

        keycloakUser.setCredentials(listOfCredentialsOfUser);
        keycloakUser.setEnabled(true);

        return keycloakUser;
    }
}
