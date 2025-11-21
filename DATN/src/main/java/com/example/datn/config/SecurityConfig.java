package com.example.datn.config;

// Thêm các import này
import com.example.datn.expection.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <-- THÊM CORS
                .authorizeHttpRequests(auth -> auth
                        // Cho phép các API đăng nhập
                        .requestMatchers(
                                "/api/auth/login/customer",
                                "/api/auth/login/employee"
                                // Thêm các API public khác (vd: /register, /san-pham/public/...)
                        ).permitAll()
                        .requestMatchers("/admin/**") // Bất kỳ URL nào bắt đầu bằng /admin
                        .hasAnyRole("ADMIN", "EMPLOYEE") // Phải có 1 trong 2 role này

                        // 3. Các API cho KHÁCH HÀNG
                        .requestMatchers("/cart/**", "/profile/**") // Ví dụ: /cart
                        .hasRole("CUSTOMER") // Phải có role này
                        // Tất cả các API còn lại đều phải xác thực
                        .anyRequest().authenticated()
                )
                // Cấu hình session thành STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Thêm JwtAuthenticationFilter vào *trước* filter mặc định
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // Tắt form login và http basic (vì dùng JWT)
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // 3. THÊM Bean cho CORS (Rất quan trọng cho Vue.js)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Thay đổi "http://localhost:8080" thành URL Vue.js của bạn
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:5173", "http://localhost:4173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho mọi đường dẫn
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