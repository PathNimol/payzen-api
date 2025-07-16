package org.aub.payrollapi.config;

import lombok.AllArgsConstructor;
import org.aub.payrollapi.exception.CustomAccessDeniedHandler;
import org.aub.payrollapi.jwt.JwtAuthEntryPoint;
import org.aub.payrollapi.jwt.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntrypoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // ✅ Public endpoints (register, login, etc.)
                        .requestMatchers("/api/v1/auths/**").permitAll()

                        // ✅ Swagger and health
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/public/**",
                                "/actuator/health"
                        ).permitAll()

                        // ✅ Role-based secure endpoints
                        .requestMatchers("/api/v1/employees/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/v1/attendance/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/v1/employee-documents/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/v1/salary/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/payslips/**").hasRole("ADMIN")

                        // ✅ Any other request needs authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntrypoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }
}