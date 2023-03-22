package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import ro.dental.clinic.config.KeycloakApiProperties;
import ro.dental.clinic.model.KeycloakGrantType;
import ro.dental.clinic.model.KeycloakLoginRequest;
import ro.dental.clinic.model.KeycloakLoginResponse;
import ro.dental.clinic.model.KeycloakLogoutRequest;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
class KeycloakUserApi {

    private final KeycloakApiProperties keycloakApiProperties;
    private final RestTemplate restTemplate;

    public KeycloakLoginResponse login(KeycloakLoginRequest keycloakLoginRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.put("grant_type",
                Collections.singletonList(keycloakLoginRequest.getGrantType().getType()));
        loginRequest.put("client_id", Collections.singletonList(keycloakLoginRequest.getClientId()));
        loginRequest.put("client_secret", ObjectUtils.isEmpty(keycloakLoginRequest.getClientSecret()) ?
                Collections.singletonList("")
                : Collections.singletonList(keycloakLoginRequest.getClientId()));

        if (keycloakLoginRequest.getGrantType() == KeycloakGrantType.REFRESH_TOKEN) {
            loginRequest.put("refresh_token",
                    Collections.singletonList(keycloakLoginRequest.getRefreshToken()));
        } else {
            loginRequest.put("username", Collections.singletonList(keycloakLoginRequest.getUsername()));
            loginRequest.put("password", Collections.singletonList(keycloakLoginRequest.getPassword()));
        }

        HttpEntity<MultiValueMap<String, String>> reqEntity = new HttpEntity<>(loginRequest,
                httpHeaders);
        var url =
                keycloakApiProperties.getBaseUrl() + keycloakApiProperties.getLogin().getPath()
                        .replace("{realm}", keycloakLoginRequest.getRealm());

        return restTemplate.postForEntity(url, reqEntity, KeycloakLoginResponse.class).getBody();
    }

    public void logout(KeycloakLogoutRequest logoutRequest) {

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.set("client_id", logoutRequest.getClientId());
        requestBody.set("client_secret", logoutRequest.getClientSecret());
        requestBody.set("refresh_token", logoutRequest.getRefreshToken());

        var requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var entity = new HttpEntity<>(requestBody, requestHeaders);
        var logoutUrl = keycloakApiProperties.getBaseUrl() + keycloakApiProperties.getLogout().getPath()
                .replace("{realm}", logoutRequest.getRealm());

        restTemplate.postForEntity(logoutUrl, entity, String.class);
    }
}
