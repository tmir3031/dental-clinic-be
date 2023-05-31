package ro.dental.clinic.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenClaimsParser {

    private final JwtDecoder jwtDecoder;

    public Map<String, Object> getJwtTokenClaims(String token) {
        return jwtDecoder.decode(token).getClaims();
    }

    public String getUserRole(Map<String, Object> claims) {
        var realmAccess = (Map<String, Object>) claims.get("realm_access");
        var roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream().filter(role -> role.startsWith("ROLE_"))
                .findFirst().orElseThrow(() -> new IllegalStateException(
                        String.format("Expected exists a role for the employee %s",
                                claims.get("sub").toString())));
    }
}
