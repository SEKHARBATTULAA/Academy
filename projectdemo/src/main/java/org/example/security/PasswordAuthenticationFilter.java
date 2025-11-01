package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Map;

public class PasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                        JwtTokenProvider jwtTokenProvider) {
        // Match login endpoint (POST /n/login)
        super(new AntPathRequestMatcher("/n/login", "POST"));
        setAuthenticationManager(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Step 1: Attempt authentication using username/password from request.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException {

        // Parse JSON body: { "username": "...", "password": "..." }
        Map<String, String> creds = objectMapper.readValue(request.getInputStream(), Map.class);
        String username = creds.get("username");
        String password = creds.get("password");

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

        // Delegates authentication to AuthenticationManager (which will call providers)
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * Step 2: If successful, generate a JWT and send it in the response.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        // Generate JWT token for authenticated user
        String jwt = jwtTokenProvider.generateAuthToken(authResult);

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
        response.getWriter().flush();
    }

    /**
     * Step 3: If authentication fails, send error response.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid username or password\"}");
        response.getWriter().flush();
    }
}
