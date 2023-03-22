package ro.dental.clinic.config;

import lombok.RequiredArgsConstructor;
import ro.dental.clinic.model.KeycloakGrantType;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Clock;
import java.time.Duration;

@ThreadSafe
@RequiredArgsConstructor
public class KeycloakTechUserAccessTokenProvider extends AccessTokenProviderTemplate {

    private final Clock clock;
    private final KeycloakProperties keycloakProperties;

    @Override
    protected Clock clock() {
        return clock;
    }

    @Override
    protected String baseUrl() {
        return keycloakProperties.getApi().getBaseUrl();
    }


    @Override
    protected String grantType() {
        return KeycloakGrantType.PASSWORD.getType();
    }

    @Override
    protected String clientId() {
        return "admin-cli";
    }


    @Override
    protected String username() {
        return keycloakProperties.getUserRealm().getTechUser().getUsername();
    }


    @Override
    protected String password() {
        return keycloakProperties.getUserRealm().getTechUser().getPassword();
    }

    @Override
    protected Duration tokenValidityTolerance() {
        return Duration.ofSeconds(keycloakProperties.getUserRealm().getTokenToleranceInSeconds());
    }

    @Override
    protected String realm() {
        return keycloakProperties.getUserRealm().getName();
    }

    @Override
    protected String tokenUri() {
        return keycloakProperties.getApi().getLogin().getPath()
                .replace("{realm}", "master");
    }
}

