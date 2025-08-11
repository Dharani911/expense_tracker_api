package com.expensetracker.controller;

import com.expensetracker.dto.AuthResponse;
import com.expensetracker.dto.UserLoginRequest;
import com.expensetracker.dto.UserRegisterRequest;
import com.expensetracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Auth endpoints: register, login, logout. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Registers a new user and returns a JWT for immediate use. */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest body) {
        return ResponseEntity.ok(authService.register(body));
    }

    /** Logs in with username or email + password and returns a JWT. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest body) {
        return ResponseEntity.ok(authService.login(body));
    }

    /** Stateless logout acknowledgment (token should be dropped on client). */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(null);
        return ResponseEntity.noContent().build();
    }
}
