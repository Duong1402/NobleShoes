// src/main/java/com/example/datn/controller/OrderMeController.java
package com.example.datn.controller;

import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.service.HoaDonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({
        "/api/hoa-don",
        "/api/orders",
        "/api/order",
        "/api/customer",
        "/api/khach-hang"
})
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*"},
        allowCredentials = "true"
)
public class OrderMeController {

    private final HoaDonService hoaDonService;

    public OrderMeController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    // ✅ map đủ kiểu FE hay gọi
    @GetMapping({"/my", "/me", "/orders", "/orders/my", "/orders/me"})
    public ResponseEntity<List<HoaDonResponse>> myOrders(Authentication authentication) {
        String email = extractEmail(authentication);
        return ResponseEntity.ok(hoaDonService.getMyOrdersByEmail(email));
    }

    private String extractEmail(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;

        Object p = auth.getPrincipal();
        if (p == null) return null;

        // ✅ Java 15: không dùng pattern matching
        if (p instanceof UserDetails) {
            UserDetails ud = (UserDetails) p;
            return ud.getUsername(); // thường là email
        }

        if (p instanceof String) {
            return (String) p;
        }

        // fallback: thử reflect getEmail()
        try {
            java.lang.reflect.Method m = p.getClass().getMethod("getEmail");
            Object v = m.invoke(p);
            return v != null ? v.toString() : null;
        } catch (Exception ignore) {}

        return null;
    }
}
