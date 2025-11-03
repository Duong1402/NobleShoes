package com.example.datn.service;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    private final Object maLock = new Object();

    @Autowired
    private JavaMailSender mailSender;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<NhanVien> getAll() {
        return nhanVienRepository.findAllOrderByMaDesc();
    }

    public NhanVien getById(UUID id) {
        return nhanVienRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID: " + id));
    }

    @Transactional
    public NhanVien create(NhanVien nv) {
        // Sinh mã tự động
        synchronized (maLock) {
            String newMa = nhanVienRepository.getNextMaNhanVien();
            nv.setMa(newMa);

            if (nhanVienRepository.existsByMa(newMa)) {
                throw new RuntimeException("Mã nhân viên đã tồn tại" + newMa);
            }
        }


        // Tự động sinh tài khoản và mật khẩu
        String taiKhoan = generateUsernameFromName(nv.getHoTen());
        String matKhauRaw = generateRandomPassword();
        String matKhauMaHoa = encoder.encode(matKhauRaw);

        nv.setTaiKhoan(taiKhoan);
        nv.setMatKhau(matKhauMaHoa);

        // Lưu trước để có ID rồi mới gửi mail
        NhanVien saved = nhanVienRepository.save(nv);

        // Gửi email thông báo
        if (nv.getEmail() != null && !nv.getEmail().isEmpty()) {
            try {
                sendAccountEmail(nv.getEmail(), nv.getHoTen(), taiKhoan, matKhauRaw);
            } catch (MessagingException e) {
                System.err.println("❌ Gửi email thất bại: " + e.getMessage());
            }
        }

        return saved;
    }

    private String generateUsernameFromName(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "user" + System.currentTimeMillis();
        }

        // Bỏ dấu tiếng Việt, chuyển về không dấu + lowercase
        String normalized = java.text.Normalizer.normalize(hoTen, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "d")
                .toLowerCase();

        String[] parts = normalized.trim().split("\\s+");
        String ten = parts[parts.length - 1]; // tên chính (từ cuối)
        StringBuilder prefix = new StringBuilder();

        for (int i = 0; i < parts.length - 1; i++) {
            prefix.append(parts[i].charAt(0));
        }

        String baseUsername = ten + prefix;

        // Đảm bảo không trùng trong DB
        String finalUsername = baseUsername;
        int count = 1;

        while (nhanVienRepository.existsByTaiKhoan(finalUsername)) {
            int rand = (int) (Math.random() * 900 + 100); // số ngẫu nhiên 100–999
            finalUsername = baseUsername + rand + (count++);
        }

        return finalUsername;
    }


    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void sendAccountEmail(String to, String name, String username, String password) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("🎉 Chào mừng bạn đến với hệ thống quản lý NobleShoes!");

        String htmlContent = """
                <div style="font-family: Arial, sans-serif; padding: 20px; border-radius: 10px; background: #f8f9fa;">
                  <h2 style="color: #007bff;">Xin chào %s!</h2>
                  <p>Bạn đã được thêm vào hệ thống quản lý nhân sự của công ty.</p>
                  <p>Dưới đây là thông tin đăng nhập của bạn:</p>
                  <ul>
                    <li><b>Tài khoản:</b> %s</li>
                    <li><b>Mật khẩu:</b> %s</li>
                  </ul>
                  <p>👉 <a href="http://localhost:5173/login" style="color: #28a745; font-weight: bold;">Đăng nhập ngay</a></p>
                  <p style="margin-top: 20px;">Trân trọng,<br>Phòng Nhân sự</p>
                </div>
                """.formatted(name, username, password);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    public NhanVien update(UUID id, NhanVien updated) {
        NhanVien existing = nhanVienRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID: " + id));

        // Giữ nguyên mã nhân viên
        updated.setMa(existing.getMa());
        existing.setHoTen(updated.getHoTen());
        existing.setSdt(updated.getSdt());
        existing.setEmail(updated.getEmail());
        existing.setUrlAnh(updated.getUrlAnh());
        existing.setGioiTinh(updated.getGioiTinh());
        existing.setNgaySinh(updated.getNgaySinh());
        existing.setDiaChi(updated.getDiaChi());
        existing.setCccd(updated.getCccd());
        existing.setTaiKhoan(updated.getTaiKhoan());
        existing.setMatKhau(updated.getMatKhau());
        existing.setNguoiSua(updated.getNguoiSua());
        existing.setTrangThai(updated.getTrangThai());
        existing.setChucVu(updated.getChucVu());

        // @PreUpdate trong entity sẽ tự thêm ngaySua
        return nhanVienRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!nhanVienRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên để xóa");
        }
        nhanVienRepository.deleteById(id);
    }
}