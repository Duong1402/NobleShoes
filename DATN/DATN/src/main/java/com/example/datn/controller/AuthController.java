package com.example.datn.controller; // Hoặc package controller của bạn

import com.example.datn.dto.*;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PasswordResetToken;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.PasswordResetTokenRepository;
import com.example.datn.service.EmailService;
import com.example.datn.service.JwtService;
import com.example.datn.service.KhachHangService;
import com.example.datn.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationProvider customerAuthProvider;
    private final AuthenticationProvider employeeAuthProvider;
    private String jwtToken;
    private final KhachHangService khachHangService;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private UserService userService; // Dịch vụ tìm KhachHang/NhanVien
    @Autowired
    private EmailService emailService; // Dịch vụ gửi email (phải được cấu hình)



    public AuthController(
            JwtService jwtService,
            @Qualifier("customerAuthenticationProvider") AuthenticationProvider customerAuthProvider,
            @Qualifier("employeeAuthenticationProvider") AuthenticationProvider employeeAuthProvider,
            KhachHangService khachHangService, ChucVuRepository chucVuRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.customerAuthProvider = customerAuthProvider;
        this.employeeAuthProvider = employeeAuthProvider;
        this.khachHangService = khachHangService;
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
        if (khachHangService.findByTaiKhoan(request.getTaiKhoan()).isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
        }
        // (Bạn có thể check thêm email nếu muốn)
        if (khachHangService.existsByEmail(request.getEmail())) {
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
        khachHangService.save(kh);

        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgotPasswordRequest request) {
        String taiKhoan = request.getTaiKhoan();

        // 1. TÌM TÀI KHOẢN & XÁC ĐỊNH LOẠI USER
        Optional<UserLookupResult> userOpt = userService.findByTaiKhoan(taiKhoan);
        if (userOpt.isEmpty()) {
            // Trả về thông báo chung để tránh lộ thông tin tài khoản tồn tại hay không
            return ResponseEntity.ok("Nếu tài khoản tồn tại, mã OTP sẽ được gửi qua email.");
        }

        // 2. TẠO OTP VÀ THỜI HẠN
        String otpCode = OtpUtil.generateOTP();
        Date expiryDate = OtpUtil.calculateExpiryDate();

        // 3. XÓA TOKEN CŨ & LƯU TOKEN MỚI
        tokenRepository.deleteByTaiKhoan(taiKhoan);

        PasswordResetToken newToken = new PasswordResetToken();
        newToken.setTaiKhoan(taiKhoan);
        newToken.setUserType(userOpt.get().getUserType()); // Lấy loại user từ service
        newToken.setToken(otpCode);
        newToken.setExpiryDate(expiryDate);
        tokenRepository.save(newToken);

        // 4. GỬI EMAIL
        // Giả sử EmailService của bạn đã có hàm này
        emailService.sendPasswordResetOtp(userOpt.get().getEmail(), otpCode);

        return ResponseEntity.ok("Mã OTP (6 chữ số) đã được gửi đến email của bạn.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        // 1. TÌM VÀ KIỂM TRA OTP
        PasswordResetToken token = tokenRepository.findByToken(otp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ."));

        // 2. KIỂM TRA HẾT HẠN
        if (token.getExpiryDate().before(new Date())) {
            tokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        // 3. CẬP NHẬT MẬT KHẨU
        String taiKhoan = token.getTaiKhoan();
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Giả sử service của bạn có hàm cập nhật mật khẩu theo tài khoản
        userService.updatePassword(taiKhoan, token.getUserType(), encodedPassword);

        // 4. XÓA TOKEN SAU KHI SỬ DỤNG
        tokenRepository.delete(token);

        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }

}