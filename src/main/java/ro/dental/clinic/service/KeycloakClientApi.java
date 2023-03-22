package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ro.dental.clinic.config.KeycloakProperties;
import ro.dental.clinic.config.KeycloakTechUserAccessTokenProvider;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.mapper.ModelToHttpEntityWithJsonBody;
import ro.dental.clinic.model.KeycloakRealmRoleRepresentation;
import ro.dental.clinic.model.KeycloakUserRepresentation;
import ro.dental.clinic.model.UserDetails;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakClientApi {

    private final KeycloakProperties keycloakProperties;
    private final KeycloakRealmRoleService keycloakRealmRoleService;
    private final KeycloakTechUserAccessTokenProvider techUserAccessTokenProvider;

    private final RestTemplate restTemplate;
    private final ModelToHttpEntityWithJsonBody modelToHttpEntityWithJsonBodyMapper;

    private final KeycloakUserRepresentationProvider keycloakUserRepresentationProvider;

    public UserDetails createUser(UserDetails user) {

        KeycloakUserRepresentation keycloakUser =
                keycloakUserRepresentationProvider.createKeycloakUserRepresentationFromDto(user);

        var adminAccessToken = techUserAccessTokenProvider.getBearerToken();
        var entity = modelToHttpEntityWithJsonBodyMapper.getEntity(adminAccessToken, keycloakUser);

        String createUserUrl = keycloakProperties.getApi().getBaseUrl() +
                keycloakProperties.getApi().getUsers().getPath();

        URI locationOfCreatedCredential;

        try {
            locationOfCreatedCredential = restTemplate.postForLocation(createUserUrl, entity);
        } catch (HttpClientErrorException.Conflict exception) {

            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.EMPLOYEE_USERNAME_CONFLICT)
                    .build()));
        }

        String path = locationOfCreatedCredential.getPath();
        String userId = path.substring(path.lastIndexOf('/') + 1);

        UserDetails createdCredential = new UserDetails();
        createdCredential.setUsername(keycloakUser.getUsername());
        createdCredential.setId(userId);

        updateRoleOfUserWithCredentials(createdCredential, user.getRole());
        return createdCredential;
    }


    public UserDetails updateRoleOfUserWithCredentials(UserDetails user, String newRole) {

        KeycloakRealmRoleRepresentation oldRole = keycloakRealmRoleService.findRoleByName(
                user.getRole());



        String realmRolesOfUserUrl = keycloakProperties.getApi().getBaseUrl() +
                keycloakProperties.getApi().getUsers().getPath() + "/" +
                user.getId() +
                keycloakProperties.getApi().getUserRealmRoles().getPath();

        if (oldRole != null) {
            // delete old role role
            var adminAccessToken = techUserAccessTokenProvider.getBearerToken();
            var deleteEntity = modelToHttpEntityWithJsonBodyMapper.getEntity(adminAccessToken,
                    new KeycloakRealmRoleRepresentation[]{oldRole});
            restTemplate.exchange(realmRolesOfUserUrl, HttpMethod.DELETE, deleteEntity, String.class);
        }

        // add new role
        var newRoleRepresentation = keycloakRealmRoleService.findRoleByName(newRole);
        if (newRoleRepresentation == null) {
            throw new BusinessException(List.of(BusinessException.BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.EMPLOYEE_ROLE_NOT_FOUND)
                    .build()));
        }

        var adminAccessToken = techUserAccessTokenProvider.getBearerToken();
        var updateEntity = modelToHttpEntityWithJsonBodyMapper.getEntity(adminAccessToken,
                new KeycloakRealmRoleRepresentation[]{newRoleRepresentation});
        restTemplate.postForObject(realmRolesOfUserUrl, updateEntity, String.class);

        user.setRole(newRole);
        return user;
    }
}
