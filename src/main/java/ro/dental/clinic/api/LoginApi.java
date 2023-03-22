package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.*;
import ro.dental.clinic.service.UserService;
import ro.dental.clinic.utils.JwtTokenClaimsParser;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class LoginApi {

    private final UserService userService;
    private final JwtTokenClaimsParser jwtTokenClaimsParser;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest loginRequest) {

        var keycloakLoginResponse = userService.login(loginRequest.getUsername(),
                loginRequest.getPassword());

        var loginRs = new LoginResponse();
        loginRs.setAccessToken(keycloakLoginResponse.getAccessToken());
        loginRs.setRefreshToken(keycloakLoginResponse.getRefreshToken());
        loginRs.setExpiresIn(keycloakLoginResponse.getExpiresIn());
        loginRs.setRefreshTokenExpiresIn(keycloakLoginResponse.getRefreshExpiresIn());

        loginRs.setUserDetails(getUserDetails(loginRs.getAccessToken()));

        return new ResponseEntity<>(loginRs, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        var keycloakRefreshResponse = userService.refresh(
                refreshRequest.getRefreshToken());
        var refreshResponse = new RefreshResponse();
        refreshResponse.setAccessToken(keycloakRefreshResponse.getAccessToken());
        refreshResponse.setRefreshToken(keycloakRefreshResponse.getRefreshToken());
        refreshResponse.setExpiresIn(keycloakRefreshResponse.getExpiresIn());
        refreshResponse.setRefreshTokenExpiresIn(keycloakRefreshResponse.getRefreshExpiresIn());
        refreshResponse.setUserDetails(getUserDetails(refreshResponse.getAccessToken()));

        return new ResponseEntity<>(refreshResponse, HttpStatus.OK);
    }

    private LoginResponseUserDetails getUserDetails(String accessToken) {
        var userDetails = new LoginResponseUserDetails();
        Map<String, Object> claims = jwtTokenClaimsParser.getJwtTokenClaims(accessToken);
        userDetails.setUserId(claims.get("sub").toString());
        userDetails.setUsername(claims.get("preferred_username").toString());
        userDetails.setRole(jwtTokenClaimsParser.getUserRole(claims));
        return userDetails;
    }

}