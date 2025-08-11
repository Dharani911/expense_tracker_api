package com.expensetracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    /** Returns 401 with a small JSON body when an unauthenticated request hits a protected route. */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, Object> body = Map.of(
                "status", 401,
                "error", "Unauthorized",
                "message", authException.getMessage() != null
                        ? authException.getMessage()
                        : "Invalid or missing authorization token",
                "path", request.getRequestURI()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}

