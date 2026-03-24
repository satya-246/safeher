package com.safeher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // This class does two things:
    // 1. Disables the default Spring login page (so our HTML login works)
    // 2. Allows all /api/auth/** and /api/sos/** and frontend pages freely
    // 3. Requires a request param "adminKey" check in AdminController (done there)

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())       // Disable CSRF for REST API
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()        // We handle admin auth manually in AdminController
            );
        return http.build();
    }
}
