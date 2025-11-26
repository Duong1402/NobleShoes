package com.example.datn.controller; // Hoặc package controller của bạn

import com.example.datn.dto.RegisterRequest;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.KhachHangRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    // 1. Inject 2 provider đã tạo ở SecurityConfig
    private final AuthenticationProvider customerAuthProvider;

    private final AuthenticationProvider employeeAuthProvider;

    private String jwtToken;

    private final KhachHangRepository khachHangRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            JwtService jwtService,
            @Qualifier("customerAuthenticationProvider") AuthenticationProvider customerAuthProvider,
            @Qualifier("employeeAuthenticationProvider") AuthenticationProvider employeeAuthProvider,
            KhachHangRepository khachHangRepository, ChucVuRepository chucVuRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.customerAuthProvider = customerAuthProvider;
        this.employeeAuthProvider = employeeAuthProvider;
        this.khachHangRepository = khachHangRepository;
        this.chucVuRepository = chucVuRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Data
    @NoArgsConstructor
    static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginResponse {
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // 1. Check trùng username/email
        if (khachHangRepository.findByTaiKhoan(request.getTaiKhoan()).isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
        // (Bạn có thể check thêm email nếu muốn)
        if (khachHangRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email này đã được sử dụng!");
        }

        // 2. Lấy Chức Vụ "Khách Hàng"
        // Giả sử trong DB bạn mã là 'CUSTOMER'
        ChucVu roleKhachHang = chucVuRepository.findByMa("CV03")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền Khách hàng trong DB"));

        // 3. Tạo đối tượng Khách Hàng
        KhachHang kh = new KhachHang();
        kh.setHoTen(request.getHoTen());
        kh.setEmail(request.getEmail());
        kh.setSdt(request.getSdt());
        kh.setTaiKhoan(request.getTaiKhoan());

        // ⚠️ QUAN TRỌNG: Mã hóa mật khẩu
        kh.setMatKhau(passwordEncoder.encode(request.getMatKhau()));

        kh.setChucVu(roleKhachHang); // Gán chức vụ
        kh.setTrangThai((byte) 1);   // 1: Hoạt động
        kh.setNgayTao(new Date());

        // 4. Lưu vào DB
        khachHangRepository.save(kh);

        return ResponseEntity.ok("Đăng ký thành công!");
    }
}