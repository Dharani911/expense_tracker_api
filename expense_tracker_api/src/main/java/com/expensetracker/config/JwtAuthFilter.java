package com.expensetracker.config;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT filter that never blocks: if a valid token is present, it authenticates using
 * the username stored as JWT subject. Otherwise, it continues unauthenticated.
 * Skips /api/auth/**, /actuator/**, /error and all OPTIONS preflight requests.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight
        return MATCHER.match("/api/auth/**", path)
                || MATCHER.match("/actuator/**", path)
                || MATCHER.match("/error", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String header = req.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (jwtService.isTokenValid(token)) {
                    String username = jwtService.getSubject(token); // subject = username
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Optional<User> u = userRepository.findByUsername(username);
                        if (u.isPresent() && Boolean.TRUE.equals(u.get().getActive())) {
                            var auth = new UsernamePasswordAuthenticationToken(
                                    username, null, Collections.emptyList()
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        } else {
                            SecurityContextHolder.clearContext();
                        }
                    }
                } else {
                    SecurityContextHolder.clearContext();
                }
            }
            chain.doFilter(req, res);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            chain.doFilter(req, res);
        }
    }
}
