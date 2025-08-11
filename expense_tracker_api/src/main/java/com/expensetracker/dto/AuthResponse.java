package com.expensetracker.dto;

/** Returned after successful authentication. */
public class AuthResponse {
    private String token;
    private long expiresAtSeconds;   // epoch seconds
    private String tokenType;        // e.g., "Bearer"

    public AuthResponse() {}

    public AuthResponse(String token, long expiresAtSeconds, String tokenType) {
        this.token = token;
        this.expiresAtSeconds = expiresAtSeconds;
        this.tokenType = tokenType;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getExpiresAtSeconds() { return expiresAtSeconds; }
    public void setExpiresAtSeconds(long expiresAtSeconds) { this.expiresAtSeconds = expiresAtSeconds; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
