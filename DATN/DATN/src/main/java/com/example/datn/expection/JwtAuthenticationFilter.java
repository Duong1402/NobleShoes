package com.example.datn.expection;

import com.example.datn.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService customerUserDetailsService;
    private final UserDetailsService employeeUserDetailsService;

    // 2. THÊM CONSTRUCTOR THỦ CÔNG (Để đảm bảo Spring không tiêm nhầm)
    public JwtAuthenticationFilter(
            JwtService jwtService,
            @Qualifier("khachHangDetailsService") UserDetailsService customerUserDetailsService,
            @Qualifier("nhanVienDetailsService") UserDetailsService employeeUserDetailsService
    ) {
        this.jwtService = jwtService;
        this.customerUserDetailsService = customerUserDetailsService;
        this.employeeUserDetailsService = employeeUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        String username = null;
        String userType = null;

        try {
            username = jwtService.extractUsername(jwt);
            userType = jwtService.extractUserType(jwt);
        } catch (Exception e) {
            System.out.println("Lỗi token: " + e.getMessage());
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Debug để chắc chắn
            System.out.println(">>> FILTER CHECK: UserType=" + userType + ", Username=" + username);

            UserDetails userDetails = null;

            // 3. KIỂM TRA KỸ LOGIC GỌI SERVICE
            if ("CUSTOMER".equals(userType)) {
                System.out.println(">>> Đang gọi KhachHangService");
                userDetails = this.customerUserDetailsService.loadUserByUsername(username);
            }
            else if ("EMPLOYEE".equals(userType)) {
                System.out.println(">>> Đang gọi NhanVienService");
                // Đảm bảo biến này là employeeUserDetailsService
                userDetails = this.employeeUserDetailsService.loadUserByUsername(username);
            }

            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}