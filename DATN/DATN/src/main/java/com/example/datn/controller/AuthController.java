package com.example.datn.controller;

import com.example.datn.dto.*;
import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PasswordResetToken;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.repository.PasswordResetTokenRepository;
import com.example.datn.service.EmailService;
import com.example.datn.service.JwtService;
import com.example.datn.service.UserService;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Validated
@CrossOrigin(originPatterns = "http://localhost:*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationProvider customerAuthProvider;
    private final AuthenticationProvider employeeAuthProvider;

    private final KhachHangRepository khachHangRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    private final PasswordResetTokenRepository tokenRepository;
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(
            JwtService jwtService,
            @Qualifier("customerAuthenticationProvider") AuthenticationProvider customerAuthProvider,
            @Qualifier("employeeAuthenticationProvider") AuthenticationProvider employeeAuthProvider,
            KhachHangRepository khachHangRepository,
            ChucVuRepository chucVuRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository tokenRepository,
            UserService userService,
            EmailService emailService
    ) {
        this.jwtService = jwtService;
        this.customerAuthProvider = customerAuthProvider;
        this.employeeAuthProvider = employeeAuthProvider;
        this.khachHangRepository = khachHangRepository;
        this.chucVuRepository = chucVuRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.userService = userService;
        this.emailService = emailService;
    }

    // ===== DTOs (login/register/me) =====
    @Data
    @NoArgsConstructor
    public static class LoginRequest {
        @JsonAlias({"username", "taiKhoan"})
        private String username;

        @JsonAlias({"password", "matKhau"})
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Object id;
        private String taiKhoan;
        private String hoTen;
        private String email;
        private String sdt;
        private String diaChi;
        private String urlAnh;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String userType;
        private UserResponse user; // CUSTOMER có, EMPLOYEE có thể null
    }

    @Data
    @NoArgsConstructor
    public static class RegisterCustomerRequest {
        @NotBlank(message = "Họ tên không được trống")
        @JsonAlias({"hoTen", "fullName", "name"})
        private String hoTen;

        @NotBlank(message = "Email không được trống")
        @Email(message = "Email không đúng định dạng")
        @JsonAlias({"email"})
        private String email;

        @NotBlank(message = "Số điện thoại không được trống")
        @JsonAlias({"sdt", "soDienThoai", "phone"})
        private String sdt;

        @NotBlank(message = "Tài khoản không được trống")
        @Size(min = 3, message = "Tài khoản tối thiểu 3 ký tự")
        @JsonAlias({"taiKhoan", "username", "userName"})
        private String taiKhoan;

        @NotBlank(message = "Mật khẩu không được trống")
        @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
        @JsonAlias({"matKhau", "password"})
        private String matKhau;
    }

    private UserResponse toUserResponse(KhachHang kh) {
        if (kh == null) return null;
        return new UserResponse(
                kh.getId(),
                kh.getTaiKhoan(),
                kh.getHoTen(),
                kh.getEmail(),
                kh.getSdt(),
                kh.getDiaChi(),
                kh.getUrlAnh()
        );
    }

    // ===== Exception handlers (giữ như bản 2) =====
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Bad credentials"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", msg));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDup(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex) {
        ex.printStackTrace();
        String m = ex.getMessage();
        String msg = (m == null || m.trim().isEmpty()) ? "Server error" : m;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", msg));
    }

    // ===== API =====

    @PostMapping("/login/customer")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = customerAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails, "CUSTOMER");

        // lấy user ngay để FE show header
        String username = userDetails.getUsername();
        KhachHang kh = khachHangRepository.findByTaiKhoan(username).orElse(null);
        if (kh == null) kh = khachHangRepository.findByEmail(username).orElse(null);

        return ResponseEntity.ok(new LoginResponse(jwtToken, "CUSTOMER", toUserResponse(kh)));
    }

    @PostMapping("/login/employee")
    public ResponseEntity<?> loginEmployee(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = employeeAuthProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails, "EMPLOYEE");
        return ResponseEntity.ok(new LoginResponse(jwtToken, "EMPLOYEE", null));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        Object p = authentication.getPrincipal();
        String username;
        if (p instanceof UserDetails) {
            username = ((UserDetails) p).getUsername();
        } else {
            username = (p == null) ? null : String.valueOf(p);
        }

        if (username == null || username.trim().isEmpty() || "anonymousUser".equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        KhachHang kh = khachHangRepository.findByTaiKhoan(username).orElse(null);
        if (kh == null) kh = khachHangRepository.findByEmail(username).orElse(null);

        if (kh == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(toUserResponse(kh));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterCustomerRequest request) {

        String taiKhoan = request.getTaiKhoan().trim();
        String email = request.getEmail().trim().toLowerCase();
        String sdt = request.getSdt().replaceAll("\\D", "");

        if (!sdt.matches("^\\d{10}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại phải đủ 10 số!"));
        }

        if (khachHangRepository.findByTaiKhoan(taiKhoan).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Tài khoản đã tồn tại!"));
        }
        if (khachHangRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email này đã được sử dụng!"));
        }
        if (khachHangRepository.existsBySdt(sdt)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Số điện thoại đã được sử dụng!"));
        }

        ChucVu roleKhachHang = chucVuRepository.findByMa("CV03")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy quyền Khách hàng (CV03) trong DB"));

        KhachHang kh = new KhachHang();
        kh.setHoTen(request.getHoTen().trim());
        kh.setEmail(email);
        kh.setSdt(sdt);
        kh.setTaiKhoan(taiKhoan);
        kh.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        kh.setChucVu(roleKhachHang);
        kh.setTrangThai((byte) 1);
        kh.setNgayTao(new Date());

        khachHangRepository.save(kh);

        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công!"));
    }

    // ===== Forgot / Reset password (giữ từ bản 1) =====

    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody ForgotPasswordRequest request) {
        String taiKhoan = request.getTaiKhoan();

        Optional<UserLookupResult> userOpt = userService.findByTaiKhoan(taiKhoan);
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok("Nếu tài khoản tồn tại, mã OTP sẽ được gửi qua email.");
        }

        String otpCode = OtpUtil.generateOTP();
        Date expiryDate = OtpUtil.calculateExpiryDate();

        tokenRepository.deleteByTaiKhoan(taiKhoan);

        PasswordResetToken newToken = new PasswordResetToken();
        newToken.setTaiKhoan(taiKhoan);
        newToken.setUserType(userOpt.get().getUserType());
        newToken.setToken(otpCode);
        newToken.setExpiryDate(expiryDate);
        tokenRepository.save(newToken);

        emailService.sendPasswordResetOtp(userOpt.get().getEmail(), otpCode);

        return ResponseEntity.ok("Mã OTP (6 chữ số) đã được gửi đến email của bạn.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        PasswordResetToken token = tokenRepository.findByToken(otp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP không hợp lệ."));

        if (token.getExpiryDate().before(new Date())) {
            tokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        String taiKhoan = token.getTaiKhoan();
        String encodedPassword = passwordEncoder.encode(newPassword);

        userService.updatePassword(taiKhoan, token.getUserType(), encodedPassword);

        tokenRepository.delete(token);

        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
    }
}
