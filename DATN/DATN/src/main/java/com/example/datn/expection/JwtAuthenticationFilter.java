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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/public/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/error")
                || path.startsWith("/images/")
                || path.startsWith("/uploads/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // ✅ Không có Bearer -> cho qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();

        // ✅ Token rỗng -> cho qua
        if (jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ✅ Nếu đã có auth rồi thì thôi
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtService.extractUsername(jwt);
            if (username == null || username.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            String userType = null;
            try {
                userType = jwtService.extractUserType(jwt); // claim "userType" (CUSTOMER/EMPLOYEE)
            } catch (Exception ignore) {
                // token cũ không có claim userType -> fallback phía dưới
            }

            // ✅ DEBUG nhẹ (cần thì giữ, không cần thì xoá)
            // System.out.println(">>> JWT FILTER: userType=" + userType + ", username=" + username);

            UserDetails userDetails = resolveUserDetails(request, username, userType);

            if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception ex) {
            // ✅ Không set auth nếu token lỗi
            SecurityContextHolder.clearContext();
            System.out.println("[JWT FILTER] Token invalid: "
                    + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Resolve userDetails theo:
     * 1) userType trong token (CUSTOMER/EMPLOYEE)
     * 2) nếu thiếu userType -> đoán theo URL (/admin hoặc /api/admin -> EMPLOYEE)
     * 3) fallback thử cả 2 service
     */
    private UserDetails resolveUserDetails(HttpServletRequest request, String username, String userType) {
        // 1) Có userType rõ ràng
        if ("CUSTOMER".equalsIgnoreCase(userType)) {
            return tryLoad(customerUserDetailsService, username);
        }
        if ("EMPLOYEE".equalsIgnoreCase(userType)) {
            return tryLoad(employeeUserDetailsService, username);
        }

        // 2) Không có userType -> đoán theo path
        String path = request.getServletPath();
        if (path.startsWith("/api/admin/") || path.startsWith("/admin/")) {
            UserDetails emp = tryLoad(employeeUserDetailsService, username);
            if (emp != null) return emp;
            return tryLoad(customerUserDetailsService, username);
        }

        // 3) Fallback: thử cả 2
        UserDetails emp = tryLoad(employeeUserDetailsService, username);
        if (emp != null) return emp;

        return tryLoad(customerUserDetailsService, username);
    }

    private UserDetails tryLoad(UserDetailsService service, String username) {
        try {
            return service.loadUserByUsername(username);
        } catch (Exception ex) {
            return null;
        }
    }
}