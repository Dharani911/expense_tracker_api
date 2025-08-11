package com.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for unauthorized access attempts.
 * Triggers a 401 Unauthorized HTTP response when thrown.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    /**
     * Creates a new UnauthorizedException with the given message.
     *
     * @param message Detailed message explaining the reason for the unauthorized access
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}
