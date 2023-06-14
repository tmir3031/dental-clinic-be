package ro.dental.clinic.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.dental.clinic.model.LogoutRequest;
import ro.dental.clinic.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logout")
public class LogoutApi {
    private final UserService userService;

    @PostMapping
    public void logout(@RequestBody LogoutRequest logoutRequest) {
        userService.logout(logoutRequest.getRefreshToken());
    }
}
