package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import ro.dental.clinic.config.KeycloakProperties;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.exceptions.BusinessException.BusinessExceptionElement;
import ro.dental.clinic.model.KeycloakGrantType;
import ro.dental.clinic.model.KeycloakLoginRequest;
import ro.dental.clinic.model.KeycloakLoginResponse;
import ro.dental.clinic.model.KeycloakLogoutRequest;

import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final KeycloakUserApi keycloakUserApi;
    private final KeycloakProperties keycloakProperties;

    public KeycloakLoginResponse login(String username, String password) {

        var loginRequest = new KeycloakLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        loginRequest.setGrantType(KeycloakGrantType.PASSWORD);
        return sendLoginOrRefreshRequest(loginRequest);
    }

    public KeycloakLoginResponse refresh(String refreshToken) {

        var refreshRequest = new KeycloakLoginRequest();
        refreshRequest.setRefreshToken(refreshToken);
        refreshRequest.setGrantType(KeycloakGrantType.REFRESH_TOKEN);
        return sendLoginOrRefreshRequest(refreshRequest);
    }

    public void logout(String refreshToken) {
        var request = new KeycloakLogoutRequest();
        request.setRefreshToken(refreshToken);
        request.setClientId(keycloakProperties.getUserRealm().getClientId());
        request.setRealm(keycloakProperties.getUserRealm().getName());
        request.setClientSecret(keycloakProperties.getUserRealm().getClientSecret());
        keycloakUserApi.logout(request);
    }

    private KeycloakLoginResponse sendLoginOrRefreshRequest(KeycloakLoginRequest request) {
        try {

            request.setRealm(keycloakProperties.getUserRealm().getName());
            request.setClientId(keycloakProperties.getUserRealm().getClientId());
            request.setClientSecret(keycloakProperties.getUserRealm().getClientSecret());

            log.trace("Trying to authenticate user {}...", request.getUsername());
            var response = keycloakUserApi.login(request);
            log.trace("Successfully authenticated user {}.", request.getUsername());
            return response;

        } catch (HttpClientErrorException | HttpServerErrorException httpException) {
            log.warn("Failed trying to log with user {}", request.getUsername(), httpException);
            throw new BusinessException(httpException,
                    Collections.singletonList(BusinessExceptionElement.builder()
                            .errorCode(httpException.getStatusCode() == HttpStatus.UNAUTHORIZED
                                    ? BusinessErrorCode.KEYCLOAK_LOGIN_INVALID_CREDENTIALS
                                    : BusinessErrorCode.KEYCLOAK_LOGIN_GEN)
                            .build()));

        }
    }
}
