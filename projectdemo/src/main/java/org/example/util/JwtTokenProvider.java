package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.example.security.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("SuperSecretKeyForJwtGenerationChangeThis".getBytes());

    // Token validity durations (in milliseconds)
    private final long AUTH_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 hour
    private final long NON_AUTH_TOKEN_VALIDITY = 1000 * 60 * 5; // 5 minutes

    // === TOKEN CREATION ===

    /** Generates a JWT for an authenticated user */
    public String generateAuthToken(Authentication authentication) {
        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities()
                .stream().map(Object::toString).collect(Collectors.toList());

        Date now = new Date();
        Date expiry = new Date(now.getTime() + AUTH_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("type", "AUTH")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Generates a "non-auth" token (for login requests, not tied to a user) */
    public String generateNonAuthToken(String clientId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + NON_AUTH_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(clientId)
                .claim("type", "NON_AUTH")
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // === TOKEN VALIDATION ===

    /** Extracts the token from the Authorization header */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /** Validates signature and expiration */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Checks if the token type is AUTH */
    public boolean isAuthToken(String token) {
        Claims claims = getClaims(token);
        return "AUTH".equals(claims.get("type"));
    }

    /** Checks if the token type is NON_AUTH */
    public boolean isNonAuthToken(String token) {
        Claims claims = getClaims(token);
        return "NON_AUTH".equals(claims.get("type"));
    }

    // === AUTHENTICATION OBJECT CREATION ===

    /** Builds Spring Security Authentication from JWT */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");

        List<GrantedAuthority> authorities = roles != null
                ? roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

    /** Parses claims safely */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

