package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.dental.clinic.config.KeycloakProperties;
import ro.dental.clinic.config.KeycloakTechUserAccessTokenProvider;
import ro.dental.clinic.mapper.HttpEntityWithEmptyBodyProvider;
import ro.dental.clinic.model.KeycloakRealmRoleRepresentation;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class KeycloakRealmRoleService {

    private final KeycloakTechUserAccessTokenProvider techUserAccessTokenProvider;
    private final KeycloakProperties keycloakProperties;

    private final HttpEntityWithEmptyBodyProvider httpEntityWithEmptyBodyProvider;

    public KeycloakRealmRoleRepresentation findRoleByName(String roleName) {

        var adminAccessToken = techUserAccessTokenProvider.getBearerToken();
        HttpEntity<String> entity = httpEntityWithEmptyBodyProvider.getEntity(adminAccessToken);

        String realmRolesUrl = keycloakProperties.getApi().getBaseUrl() +
                keycloakProperties.getApi().getRealmRoles().getPath();


        RestTemplate restTemplate = new RestTemplate();

        var realmRoles = restTemplate
                .exchange(realmRolesUrl, HttpMethod.GET, entity, KeycloakRealmRoleRepresentation[].class)
                .getBody();


        return Arrays.stream(realmRoles).filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElse(null);
    }
}

