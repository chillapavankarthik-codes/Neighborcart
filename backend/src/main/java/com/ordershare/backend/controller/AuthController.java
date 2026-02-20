package com.ordershare.backend.controller;

import com.ordershare.backend.auth.AuthService;
import com.ordershare.backend.dto.AuthResponse;
import com.ordershare.backend.dto.AuthUserResponse;
import com.ordershare.backend.dto.RequestOtpRequest;
import com.ordershare.backend.dto.RequestOtpResponse;
import com.ordershare.backend.dto.VerifyOtpRequest;
import com.ordershare.backend.security.AuthenticatedUser;
import com.ordershare.backend.security.CurrentUserProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserProvider currentUserProvider;

    public AuthController(AuthService authService, CurrentUserProvider currentUserProvider) {
        this.authService = authService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/request-otp")
    public RequestOtpResponse requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        return authService.requestOtp(request.getPhoneNumber());
    }

    @PostMapping("/verify-otp")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOtp(request);
    }

    @GetMapping("/me")
    public AuthUserResponse me() {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
        return authService.getCurrentUser(user);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        AuthenticatedUser user = currentUserProvider.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header is required");
        }

        String rawToken = authorizationHeader.substring(7).trim();
        authService.logout(user, rawToken);
        SecurityContextHolder.clearContext();
    }
}
