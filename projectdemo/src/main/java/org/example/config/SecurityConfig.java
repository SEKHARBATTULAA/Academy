package org.example.config;

import org.example.security.AuthorizationFilter;
import org.example.security.PasswordAuthenticationFilter;
import org.example.security.UsernamePasswordAuthProvider;
import org.example.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final UsernamePasswordAuthProvider usernamePasswordAuthProvider;

    public SecurityConfig(UsernamePasswordAuthProvider usernamePasswordAuthProvider) {
        this.usernamePasswordAuthProvider = usernamePasswordAuthProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(usernamePasswordAuthProvider));
    }

    @Bean
    public JwtTokenProvider getJwtTokenProvider(){
        return new JwtTokenProvider();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManager manager = authenticationManager();

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/n/login").permitAll()
                        .anyRequest().authenticated()
                ).addFilterBefore(new AuthorizationFilter(getJwtTokenProvider()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new PasswordAuthenticationFilter(authenticationManager(), getJwtTokenProvider()),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
