package ro.dental.clinic.utils;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenClaimsParser {

  private final JwtDecoder jwtDecoder;

  public Map<String, Object> getJwtTokenClaims(String token) {
    return jwtDecoder.decode(token).getClaims();
  }

  public String getUserRole(Map<String, Object> claims) {
    return ((Collection<String>) claims.get("roles")).stream()
        .findFirst().orElseThrow(() -> new IllegalStateException(
            String.format("Expected exists a role for the employee %s",
                claims.get("sub").toString())));
  }
}
