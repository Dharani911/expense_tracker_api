package com.expensetracker.service;

import com.expensetracker.config.JwtService;
import com.expensetracker.dto.AuthResponse;
import com.expensetracker.dto.UserLoginRequest;
import com.expensetracker.dto.UserRegisterRequest;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/** Handles registration, login (username/email), and logout (stateless). */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Registers a new active user after uniqueness checks. */
    @Transactional
    public AuthResponse register(UserRegisterRequest req) {
        final String username = trim(req.getUsername());
        final String email = trim(req.getEmail()).toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new BadRequestException("Username already in use.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already in use.");
        }

        User user = new User();
        user.setUsername(username);
        user.setName(trim(req.getName()));
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setActive(true);

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    /** Authenticates by username OR email and returns a JWT. */
    public AuthResponse login(UserLoginRequest req) {
        final String identifier = trim(req.getIdentifier());
        Optional<User> userOpt = identifier.contains("@")
                ? userRepository.findByEmail(identifier.toLowerCase())
                : userRepository.findByUsername(identifier);

        User user = userOpt.orElseThrow(() -> new BadRequestException("Invalid credentials."));
        if (!Boolean.TRUE.equals(user.getActive()) ||
                !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials.");
        }

        return buildAuthResponse(user);
    }

    /** For stateless JWT, logout is client-side; we just acknowledge. */
    public void logout(String ignored) {
        // No server state to clear yet (token blacklist could be added later).

    }

    /** Generates token for the user and maps to AuthResponse (epoch seconds). */
    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getUsername(),
                Map.of("uid", user.getId())
        );
        long expSeconds = jwtService.getExpirationEpochSeconds(token);
        return new AuthResponse(token, expSeconds, "Bearer");
    }

    private static String trim(String v) { return v == null ? null : v.trim(); }
}
