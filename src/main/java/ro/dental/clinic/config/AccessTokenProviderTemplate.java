package ro.dental.clinic.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

public abstract class AccessTokenProviderTemplate implements BearerTokenProvider {

    public static final String DEFAULT_TOKEN_URI = "/realms/{realm}/protocol/openid-connect/token";

    @Getter
    final RestTemplate restTemplate;

    AccessTokenDtls accessTokenDetails;

    public AccessTokenProviderTemplate() {
        restTemplate = new RestTemplate();
    }

    public AccessTokenProviderTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        Optional.of(baseUrl()).ifPresent(baseUrl ->
                restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl)));
    }

    @Override
    @Nullable
    public synchronized String getBearerToken() {
        var clock = clock();
        var nowish = clock.instant().plus(tokenValidityTolerance());
        if (shouldRequestTokenWithUserCredentials(nowish)) {
            accessTokenDetails = getTokenByUserCredentials();
        } else if (shouldRefreshToken(nowish)) {
            accessTokenDetails = refreshToken();
        }
        return accessTokenDetails.accessToken;
    }

    protected abstract Clock clock();

    protected abstract String baseUrl();

    protected abstract String grantType();

    protected abstract String clientId();

    protected abstract String username();

    protected abstract String password();

    protected Duration tokenValidityTolerance() {
        return Duration.ofSeconds(10);
    }

    protected String tokenUri() {
        return DEFAULT_TOKEN_URI;
    }

    protected String accessTokenResponseProperty() {
        return "access_token";
    }

    protected String expiresInResponseProperty() {
        return "expires_in";
    }

    protected String refreshTokenResponseProperty() {
        return "refresh_token";
    }

    protected String refreshExpiresInResponseProperty() {
        return "refresh_expires_in";
    }

    protected abstract String realm();


    protected boolean shouldRequestTokenWithUserCredentials(Instant currentTime) {
        if (accessTokenDetails == null) {
            return true;
        }
        if (accessTokenDetails.refreshToken != null &&
                !currentTime.isBefore(accessTokenDetails.refreshExpiresAt)) {
            return true;
        }
        return accessTokenDetails.refreshToken == null &&
                !currentTime.isBefore(accessTokenDetails.expiresAt);
    }

    protected boolean shouldRefreshToken(Instant currentTime) {
        return accessTokenDetails.refreshToken != null &&
                !currentTime.isBefore(accessTokenDetails.expiresAt);
    }

    @Nonnull
    private AccessTokenDtls getTokenByUserCredentials() {
        final Map<String, String> params = Maps.newLinkedHashMap();
        Optional.ofNullable(grantType()).ifPresent(grantType -> params.put("grant_type", grantType));
        Optional.ofNullable(clientId()).ifPresent(clientId -> params.put("client_id", clientId));
        Optional.ofNullable(username()).ifPresent(username -> params.put("username", username));
        Optional.ofNullable(password()).ifPresent(password -> params.put("password", password));
        return requestTokenWithParams(params);
    }

    @Nonnull
    private AccessTokenDtls refreshToken() {
        final Map<String, String> params = Maps.newLinkedHashMap();
        params.put("grant_type", "refresh_token");
        Optional.ofNullable(clientId()).ifPresent(clientId -> params.put("client_id", clientId));
        params.put("refresh_token", accessTokenDetails.refreshToken);
        return requestTokenWithParams(params);
    }

    private AccessTokenDtls requestTokenWithParams(final Map<String, String> params) {
        var pathVars = Maps.<String, String>newTreeMap();
        Optional.ofNullable(realm()).ifPresent(realm -> pathVars.put("realm", realm));
        var rse = restTemplate.exchange(
                tokenUri(),
                HttpMethod.POST,
                createHttpEntityForTokenRequest(params),
                JsonNode.class,
                pathVars);
        return parseAccessTokenDetails(Optional.of(rse).map(HttpEntity::getBody)
                .orElseThrow(() -> {
                    throw new IllegalStateException("Not found the access token data");
                }));
    }

    protected AccessTokenDtls parseAccessTokenDetails(JsonNode src) {
        var clock = clock();
        var nowish = clock.instant().plus(tokenValidityTolerance());

        var accessTokenDtls = new AccessTokenDtls();
        accessTokenDtls.accessToken = src.get(accessTokenResponseProperty()).asText();
        Optional.ofNullable(src.get(expiresInResponseProperty()))
                .ifPresent(expiresInNd -> accessTokenDtls.expiresIn = expiresInNd.asInt());
        Optional.ofNullable(src.get(refreshTokenResponseProperty()))
                .ifPresent(refreshTokenNd -> accessTokenDtls.refreshToken = refreshTokenNd.asText());
        Optional.ofNullable(src.get(refreshExpiresInResponseProperty()))
                .ifPresent(rtExpiresInNd -> accessTokenDtls.refreshExpiresIn = rtExpiresInNd.asInt());

        accessTokenDtls.expiresAt = nowish.plus(accessTokenDtls.expiresIn, ChronoUnit.SECONDS);
        accessTokenDtls.refreshExpiresAt = nowish.plus(accessTokenDtls.refreshExpiresIn, ChronoUnit.SECONDS);
        return accessTokenDtls;
    }

    private HttpEntity<?> createHttpEntityForTokenRequest(Map<String, String> params) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var mvm = new MultiValueMapAdapter<String, String>(Maps.newTreeMap());
        params.forEach(mvm::add);
        return new HttpEntity<>(mvm, headers);
    }

    static class AccessTokenDtls {
        String accessToken;
        String refreshToken;
        int expiresIn;
        int refreshExpiresIn;
        Instant expiresAt;
        Instant refreshExpiresAt;
    }

}
