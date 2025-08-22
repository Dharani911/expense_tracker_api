package com.expensetracker.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates and validates JWTs for the application.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expirationMinutes}") long expirationMinutes
    ) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    /** Generates a signed JWT for the given subject with optional extra claims. */
    public String generateToken(String subject, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMinutes * 60_000L);

        return Jwts.builder()
                .subject(subject)
                .claims(extraClaims == null ? Map.of() : extraClaims)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /** Generates a signed JWT for the given subject without extra claims. */
    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }

    /** Returns the subject (username/email) stored in the token. */
    public String getSubject(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /** Returns the configured expiration (in minutes). */
    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    /** True if the token is well-formed, signed with our key, and not expired. */
    public boolean isTokenValid(String token) {
        try {
            Date exp = getClaim(token, Claims::getExpiration);
            return exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /** Extracts a specific claim using the resolver. */
    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }

    /** Parses and verifies the token, returning its claims payload. */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    /** Returns token expiration as epoch seconds. */
    public long getExpirationEpochSeconds(String token) {
        var exp = getClaim(token, io.jsonwebtoken.Claims::getExpiration);
        return exp == null ? 0L : exp.toInstant().getEpochSecond();
    }

}
