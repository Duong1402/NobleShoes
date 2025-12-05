package com.example.datn.config;

import com.example.datn.expection.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("customerAuthenticationProvider") AuthenticationProvider customerProvider,
            @Qualifier("employeeAuthenticationProvider") AuthenticationProvider employeeProvider
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())

                // ✅ Nếu chưa login -> 401, thiếu quyền -> 403
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(401, "UNAUTHORIZED"))
                        .accessDeniedHandler((req, res, e) -> res.sendError(403, "FORBIDDEN"))
                )

                // ✅ 2 provider
                .authenticationProvider(customerProvider)
                .authenticationProvider(employeeProvider)

                .authorizeHttpRequests(auth -> auth
                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // static + swagger + error
                        .requestMatchers(
                                "/images/**", "/uploads/**", "/favicon.ico",
                                "/swagger-ui/**", "/v3/api-docs/**", "/error"
                        ).permitAll()

                        // public
                        .requestMatchers("/api/public/**", "/public/**").permitAll()

                        // ✅ auth: CHỈ permit login/register (KHÔNG permitAll cả /api/auth/**)
                        .requestMatchers(
                                "/api/auth/login/**",
                                "/api/auth/register"
                                // nếu có refresh thì thêm:
                                // ,"/api/auth/refresh"
                        ).permitAll()

                        // ✅ /me bắt buộc có token
                        .requestMatchers("/api/auth/me").authenticated()

                        // ✅ admin APIs
                        .requestMatchers("/admin/**", "/api/admin/**")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                        .anyRequest().authenticated()
                )

                // ✅ jwt filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ CUSTOMER provider
    @Bean("customerAuthenticationProvider")
    public AuthenticationProvider customerAuthenticationProvider(
            @Qualifier("khachHangDetailsService") UserDetailsService uds,
            PasswordEncoder pe
    ) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(pe);
        return p;
    }

    // ✅ EMPLOYEE provider
    @Bean("employeeAuthenticationProvider")
    public AuthenticationProvider employeeAuthenticationProvider(
            @Qualifier("nhanVienDetailsService") UserDetailsService uds,
            PasswordEncoder pe
    ) {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(pe);
        return p;
    }
}
