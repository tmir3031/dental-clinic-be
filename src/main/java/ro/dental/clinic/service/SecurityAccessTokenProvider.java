package ro.dental.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import ro.dental.clinic.enums.BusinessErrorCode;
import ro.dental.clinic.exceptions.BusinessException;
import ro.dental.clinic.exceptions.BusinessException.BusinessExceptionElement;
import ro.dental.clinic.utils.JwtTokenClaimsParser;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityAccessTokenProvider {

    private final JwtTokenClaimsParser jwtTokenClaimsParser;

    public String getUserIdFromAuthToken() {
        var jwtAuthenticationToken = getJwtAuthenticationToken();
        if (jwtAuthenticationToken.getName() == null) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.KEYCLOAK_TOKEN_DETAILS_MISSING_USER_ID)
                    .build()));
        }
        return jwtAuthenticationToken.getName();
    }

    private JwtAuthenticationToken getJwtAuthenticationToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return (JwtAuthenticationToken) authentication;
        } else {
            throw new IllegalStateException("No JWT Authentication Token found in Security Context!");
        }
    }

    public String getUserRoleFromAuthToken() {
        var jwtAuthenticationToken = getJwtAuthenticationToken();
        if (jwtAuthenticationToken.getName() == null) {
            throw new BusinessException(List.of(BusinessExceptionElement.builder()
                    .errorCode(BusinessErrorCode.KEYCLOAK_TOKEN_DETAILS_MISSING_USER_ID)
                    .build()));
        }
        return jwtTokenClaimsParser.getUserRole(jwtAuthenticationToken.getTokenAttributes());
    }

}
