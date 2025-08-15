package com.expensetracker.dto;

import java.time.Instant;

/**
 * Represents user details returned to the client (safe fields only).
 */
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;


    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


}
