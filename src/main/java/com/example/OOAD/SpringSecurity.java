package com.example.OOAD;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Chain of Responsibility Pattern (Behavioral) — SpringSecurity
 *
 * Intent: Avoid coupling the sender of a request to its receiver by giving
 * more than one object a chance to handle the request. Chain the receiving
 * objects and pass the request along the chain until an object handles it.
 *
 * In this project: Every incoming HTTP request passes through a chain of
 * security filters in order:
 *   1. CorsFilter         — checks and applies CORS policy
 *   2. CSRF filter        — disabled here (stateless REST API)
 *   3. Authorization rule — permits all requests (JWT/session handled client-side)
 *
 * Each filter either handles the request (e.g. rejects a bad CORS origin)
 * or passes it to the next handler in the chain.
 *
 * Also demonstrates: BCryptPasswordEncoder is a Strategy for password hashing
 * that can be swapped for a different encoding scheme without changing callers.
 */
@Configuration
public class SpringSecurity {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors() // enable CORS
            .and()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .formLogin().disable();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
