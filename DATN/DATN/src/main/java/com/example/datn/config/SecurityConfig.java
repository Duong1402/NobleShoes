package com.example.datn.config;

// Thêm các import này

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

import java.util.Arrays;

@Configuration
@EnableWebSecurity // <-- BẮT BUỘC
@RequiredArgsConstructor // <-- Thêm để inject (tiêm) dependency
public class SecurityConfig {

    // 1. Inject các dependencies cần thiết
    private final JwtAuthenticationFilter jwtAuthFilter; // Filter JWT của bạn

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

                // Xử lý Exception: 401 (chưa login) và 403 (thiếu quyền)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(401, "UNAUTHORIZED"))
                        .accessDeniedHandler((req, res, e) -> res.sendError(403, "FORBIDDEN"))
                )

                // Thêm 2 Authentication Providers
                .authenticationProvider(customerProvider)
                .authenticationProvider(employeeProvider)

                // Cấu hình phân quyền (Authorization)
                .authorizeHttpRequests(auth -> auth
                        // 1. Preflight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Static, Swagger, Error, Public APIs
                        .requestMatchers(
                                // Files
                                "/images/**", "/uploads/**", "/favicon.ico",
                                "/admin/upload/**", // Từ config thứ 2
                                // Docs
                                "/swagger-ui/**", "/v3/api-docs/**",
                                // Error
                                "/error",
                                // Public endpoints
                                "/api/public/**", "/public/**",
                                "/admin/vnpay/*","/api/*"
                        ).permitAll()

                        // 3. Authentication (Login/Register)
                        .requestMatchers(
                                "/api/auth/login/**",
                                "/api/auth/login/customer",
                                "/api/auth/login/employee",
                                "/api/auth/register"
                        ).permitAll()

                        // 4. /me (Yêu cầu Token hợp lệ)
                        .requestMatchers("/api/auth/me").authenticated()

                        // 5. Admin-only APIs
                        // Thêm các APIs chi tiết từ config thứ 2 vào đây
                        .requestMatchers(
                                "/admin/nhan-vien/**",
                                "/admin/chuc-vu/**",
                                "/admin/thong-ke/**"
                        ).hasRole("ADMIN")

                        // 6. Shared Admin/Employee APIs
                        .requestMatchers("/admin/**", "/api/admin/**")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                        // 7. Customer APIs
                        .requestMatchers("/cart/**", "/profile/**", "/api/cart/**", "/api/profile/**")
                        .hasRole("CUSTOMER")

                        // 8. Tất cả các request còn lại đều phải xác thực (có token)
                        .anyRequest().authenticated()
                )

                //  jwt filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 3. THÊM Bean cho CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:4173"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control", "Accept", "Origin", "X-Requested-With", "X-Auth-Token"));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // 4. Các bean bạn đã có (Giữ nguyên)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider customerAuthenticationProvider(
            @Qualifier("khachHangDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationProvider employeeAuthenticationProvider(
            @Qualifier("nhanVienDetailsService") UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}