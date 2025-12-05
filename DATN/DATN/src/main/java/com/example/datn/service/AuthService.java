package com.example.datn.service;

import com.example.datn.entity.ChucVu;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.KhachHangRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KhachHangRepository khachHangRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> registerCustomer(String hoTen, String email, String sdt, String taiKhoan, String matKhau) {

        // ===== validate cơ bản =====
        String fullName = safeTrim(hoTen);
        String username = safeTrim(taiKhoan);
        String mail = safeTrim(email).toLowerCase();
        String rawPass = safeTrim(matKhau);
        String phone = normalizePhone(sdt);

        if (fullName.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống!");
        if (username.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản không được để trống!");
        if (mail.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống!");
        if (phone.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại không được để trống!");
        if (rawPass.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống!");

        // ===== check trùng -> 409 =====
        if (khachHangRepository.findByTaiKhoan(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tài khoản đã tồn tại!");
        }
        if (khachHangRepository.existsByEmail(mail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã được sử dụng!");
        }
        if (khachHangRepository.existsBySdt(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Số điện thoại đã được sử dụng!");
        }

        ChucVu roleKhachHang = chucVuRepository.findByMa("CV03")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Không tìm thấy quyền Khách hàng (CV03) trong DB"
                ));

        KhachHang kh = new KhachHang();

        // ✅ FIX LỖI 2627 duplicate (<NULL>) do UNIQUE cột ma:
        kh.setMa(generateMaKhachHang());

        kh.setHoTen(fullName);
        kh.setEmail(mail);
        kh.setSdt(phone);
        kh.setTaiKhoan(username);
        kh.setMatKhau(passwordEncoder.encode(rawPass));
        kh.setChucVu(roleKhachHang);
        kh.setTrangThai((byte) 1);
        kh.setNgayTao(new Date());

        try {
            khachHangRepository.save(kh);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Dữ liệu đã tồn tại hoặc vi phạm ràng buộc (email/sđt/tài khoản/ma).",
                    ex
            );
        }

        return Map.of("message", "Đăng ký thành công!");
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalizePhone(String sdt) {
        if (sdt == null) return "";
        return sdt.replaceAll("\\D", "");
    }

    // ✅ Sinh mã KH không trùng (đủ dùng cho đồ án)
    private String generateMaKhachHang() {
        // YÊU CẦU: trong KhachHangRepository có method existsByMa(String ma)
        String ma;
        do {
            ma = "KH" + System.currentTimeMillis();
        } while (khachHangRepository.existsByMa(ma));
        return ma;
    }
}
