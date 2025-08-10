package com.expensetracker.dto;

import jakarta.validation.constraints.*;

public class UserRegisterRequest {

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 500) // per your update
    private String email;

    // ≥8 chars, at least 1 upper, 1 lower, 1 digit, 1 special, no spaces
    @NotBlank
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$",
            message = "Min 8 chars, include upper, lower, digit, special; no spaces."
    )
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
