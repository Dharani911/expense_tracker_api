package com.expensetracker.service;

import com.expensetracker.dto.UpdateUserRequest;
import com.expensetracker.dto.UserResponse;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.mapper.UserMapper;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User profile operations for the currently authenticated user.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Returns the logged‑in user's profile. */
    public UserResponse getMyProfile() {
        User user = getCurrentUserOrThrow();
        return UserMapper.toResponse(user);
    }

    /** Updates the logged‑in user's editable fields (name, email). */
    @Transactional
    public UserResponse updateMyProfile(final UpdateUserRequest req) {
        User user = getCurrentUserOrThrow();

        // Enforce unique email if changed
        String newEmail = req.getEmail();
        if (!user.getEmail().equalsIgnoreCase(newEmail)
                && userRepository.existsByEmail(newEmail)) {
            throw new BadRequestException("Email already in use.");
        }

        user.setName(req.getName());
        user.setEmail(newEmail);
        userRepository.save(user);
        return UserMapper.toResponse(user);
    }

    /** Soft‑deactivates the logged‑in user's account. */
    @Transactional
    public void deactivateMyAccount() {
        User user = getCurrentUserOrThrow();
        user.setActive(false);
        userRepository.save(user);
    }

    // ---------- helpers ----------

    /** Loads the current user by username from the security context. */
    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found.");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
