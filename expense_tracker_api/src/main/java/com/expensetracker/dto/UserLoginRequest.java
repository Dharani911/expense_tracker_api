package com.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;

/** Login payload accepting username or email in 'identifier'. */
public class UserLoginRequest {
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
