package com.example.datn.controller; // Hoặc package controller của bạn

import com.example.datn.service.JwtService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    // 1. Inject 2 provider đã tạo ở SecurityConfig
    private final AuthenticationProvider customerAuthProvider;

    private final AuthenticationProvider employeeAuthProvider;

    private String jwtToken;

    public AuthController(
            JwtService jwtService,
            @Qualifier("customerAuthenticationProvider") AuthenticationProvider customerAuthProvider,
            @Qualifier("employeeAuthenticationProvider") AuthenticationProvider employeeAuthProvider
    ) {
        this.jwtService = jwtService;
        this.customerAuthProvider = customerAuthProvider;
        this.employeeAuthProvider = employeeAuthProvider;
    }

    @Data
    @NoArgsConstructor
    static class LoginRequest{
            private String username;
            private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginResponse{
        private String token;
    }

    // 2. Tạo API cho Khách Hàng
    @PostMapping("/login/customer")
    public ResponseEntity<LoginResponse> loginCustomer(@RequestBody LoginRequest loginRequest) {

        // Xác thực bằng provider của Khách Hàng
        Authentication authentication = customerAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Tạo token với claim "CUSTOMER"
        jwtToken = jwtService.generateToken(userDetails, "CUSTOMER");
        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }

    // 3. Tạo API cho Nhân Viên
    @PostMapping("/login/employee")
    public ResponseEntity<LoginResponse> loginEmployee(@RequestBody LoginRequest loginRequest) {

        // Xác thực bằng provider của Nhân Viên
        Authentication authentication = employeeAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Tạo token với claim "EMPLOYEE"
        jwtToken = jwtService.generateToken(userDetails, "EMPLOYEE");
        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }
}