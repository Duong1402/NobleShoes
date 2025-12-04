package com.example.datn.service;

import com.example.datn.dto.UserLookupResult;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final KhachHangRepository khachHangRepo;
    private final NhanVienRepository nhanVienRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(KhachHangRepository khachHangRepo, NhanVienRepository nhanVienRepo, PasswordEncoder passwordEncoder) {
        this.khachHangRepo = khachHangRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Tìm kiếm user theo taiKhoan trong cả hai bảng.
     */
    public Optional<UserLookupResult> findByTaiKhoan(String taiKhoan) {
        // 1. Tìm trong bảng NhanVien
        Optional<NhanVien> nvOpt = Optional.ofNullable(nhanVienRepo.findByTaiKhoan(taiKhoan));
        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            return Optional.of(UserLookupResult.builder()
                    .taiKhoan(nv.getTaiKhoan())
                    .email(nv.getEmail())
                    .userType("EMPLOYEE")
                    .build());
        }

        // 2. Tìm trong bảng KhachHang
        Optional<KhachHang> khOpt = khachHangRepo.findByTaiKhoan(taiKhoan);
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            return Optional.of(UserLookupResult.builder()
                    .taiKhoan(kh.getTaiKhoan())
                    .email(kh.getEmail())
                    .userType("CUSTOMER")
                    .build());
        }

        return Optional.empty();
    }

    /**
     * Cập nhật mật khẩu sau khi OTP hợp lệ.
     */
    public void updatePassword(String taiKhoan, String userType, String rawNewPassword) {
        String encodedPassword = passwordEncoder.encode(rawNewPassword);

        if ("EMPLOYEE".equals(userType)) {
            NhanVien nv = nhanVienRepo.findByTaiKhoan(taiKhoan);
            if (nv == null) {
               throw new RuntimeException("Lỗi hệ thống: Không tìm thấy nhân viên.");
            }
            nv.setMatKhau(encodedPassword);
            nhanVienRepo.save(nv);

        } else if ("CUSTOMER".equals(userType)) {
            KhachHang kh = khachHangRepo.findByTaiKhoan(taiKhoan)
                    .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy khách hàng."));
            kh.setMatKhau(encodedPassword);
            khachHangRepo.save(kh);
        }
    }
}
