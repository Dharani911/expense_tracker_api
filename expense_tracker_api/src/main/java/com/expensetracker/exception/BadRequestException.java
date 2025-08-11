package com.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for invalid or unacceptable client requests.
 * Triggers a 400 Bad Request HTTP response when thrown.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new BadRequestException with the given message.
     *
     * @param message Detailed message explaining the reason for the bad request
     */
    public BadRequestException(String message) {
        super(message);
    }
}
