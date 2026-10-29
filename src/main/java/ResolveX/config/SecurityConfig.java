package ResolveX.config;

import ResolveX.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@AllArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                java.util.List.of(
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "https://resolve-x-frontend.vercel.app"
                )
        );

        configuration.setAllowedMethods(
                java.util.List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                java.util.List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"You do not have permission to access this resource\"}"
                            );
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/auth/**").permitAll()

                        // User endpoints
                        .requestMatchers(HttpMethod.POST, "/api/complaints")
                        .hasRole("USER")

                        .requestMatchers(HttpMethod.GET, "/api/complaints/my")
                        .hasRole("USER")

                        .requestMatchers(HttpMethod.GET, "/api/complaints/*")
                        .hasRole("USER")

                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/assign/*")
                        .hasRole("USER")

                        // Worker endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/accept")
                        .hasRole("WORKER")

                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/cancel")
                        .hasRole("WORKER")

                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/resolve")
                        .hasRole("WORKER")

                        .requestMatchers("/api/workers/my-complaints")
                        .hasRole("WORKER")

                        .requestMatchers(HttpMethod.GET, "/api/workers/available")
                        .hasAnyRole("USER", "WORKER")

                        .requestMatchers(HttpMethod.PUT, "/api/workers/availability")
                        .hasRole("WORKER")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

