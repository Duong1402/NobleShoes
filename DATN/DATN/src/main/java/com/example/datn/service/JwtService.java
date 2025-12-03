package com.example.datn.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // Sửa import
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // Thêm import
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // 1. Lấy secret key từ application.properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    // 2. Lấy thời gian hết hạn từ application.properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Trích xuất username (Subject) từ token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Trích xuất userType (Custom Claim) từ token
    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("userType", String.class));
    }

    // Hàm trích xuất 1 claim cụ thể
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Hàm tạo token CHÍNH (dùng cho cả KhachHang và NhanVien)
    public String generateToken(UserDetails userDetails, String userType) {
        // --- BẮT ĐẦU DEBUG ---
        System.out.println("--- DEBUG (JwtService.java): Đang tạo Token ---");
        System.out.println("--- DEBUG: Các quyền (Authorities) nhận được là: " + userDetails.getAuthorities() + " ---");
        // --- KẾT THÚC DEBUG ---

        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType); // Thêm userType vào claim

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Dùng biến expiration
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Hàm kiểm tra token hợp lệ
    // (Quan trọng: so sánh cả username và ngày hết hạn)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --- CÁC HÀM HELPER BỊ THIẾU ---

    // Kiểm tra token hết hạn
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Lấy ngày hết hạn
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Lấy TẤT CẢ claims từ token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Lấy key để ký (dùng Base64)
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Giải mã Base64
        return Keys.hmacShaKeyFor(keyBytes);
    }
}