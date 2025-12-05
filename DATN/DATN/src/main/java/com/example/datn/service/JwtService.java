package com.example.datn.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserType(String token) {
        return extractClaim(token, claims -> claims.get("userType", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * FIX:
     * - Nếu secret là Base64 thường => decode BASE64
     * - Nếu secret là Base64URL (có '-' '_' ) => decode BASE64URL
     * - Còn lại => dùng raw bytes UTF-8
     *
     * HS256 yêu cầu key >= 32 bytes (256-bit).
     */
    private Key getSignInKey() {
        String sk = (secretKey == null) ? "" : secretKey.trim();
        if (sk.isEmpty()) {
            throw new IllegalStateException("JWT secret-key is empty");
        }

        byte[] keyBytes;

        // Base64URL hay Base64 thường?
        boolean looksBase64 = sk.matches("^[A-Za-z0-9+/=\\r\\n]+$");
        boolean looksBase64Url = sk.matches("^[A-Za-z0-9\\-_]+$");

        if (looksBase64) {
            keyBytes = Decoders.BASE64.decode(sk);
        } else if (looksBase64Url) {
            // nếu bạn dùng base64url (thường có '_' hoặc '-') thì decode base64url
            keyBytes = Decoders.BASE64URL.decode(sk);
        } else {
            // secret string thường
            keyBytes = sk.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT secret too short. Need >= 32 bytes for HS256.");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
