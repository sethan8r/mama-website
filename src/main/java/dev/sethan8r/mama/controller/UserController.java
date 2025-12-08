package dev.sethan8r.mama.controller;

import dev.sethan8r.mama.dto.ChangePasswordRequest;
import dev.sethan8r.mama.dto.ChangeUsernameRequest;
import dev.sethan8r.mama.dto.UserResponseDto;
import dev.sethan8r.mama.security.SecurityService;
import dev.sethan8r.mama.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    @GetMapping
    public UserResponseDto getUserById() {
        var user = securityService.getCurrentUser();

        return userService.getUserById(user.getId());
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid ChangePasswordRequest dto) {
        var user = securityService.getCurrentUser();

        userService.updatePassword(user.getId(), dto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/username")
    public ResponseEntity<Void> updateUsername(@RequestBody @Valid ChangeUsernameRequest dto) {
        var user = securityService.getCurrentUser();

        userService.updateUsername(user.getId(), dto);

        return ResponseEntity.noContent().build();
    }
}
