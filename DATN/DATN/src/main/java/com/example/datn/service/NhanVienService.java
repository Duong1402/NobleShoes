package com.example.datn.service;

import com.example.datn.entity.ChucVu;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.ChucVuRepository;
import com.example.datn.repository.NhanVienRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
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

    private final NhanVienRepository nhanVienRepository;
    private final ChucVuRepository chucVuRepository;
    private final JavaMailSender mailSender;

    private final Object maLock = new Object();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // đổi link này theo FE của bạn (bản 1: /login-employee, bản 2: /login)
    private static final String EMPLOYEE_LOGIN_URL = "http://localhost:5173/login-employee";

    public NhanVienService(NhanVienRepository nhanVienRepository,
                           ChucVuRepository chucVuRepository,
                           JavaMailSender mailSender) {
        this.nhanVienRepository = nhanVienRepository;
        this.chucVuRepository = chucVuRepository;
        this.mailSender = mailSender;
    }

    public List<NhanVien> getAll() {
        return nhanVienRepository.findAllOrderByMaDesc();
    }

    public NhanVien getById(UUID id) {
        return nhanVienRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID: " + id));
    }

    @Transactional
    public NhanVien create(NhanVien nv) {
        // ===== 1) Sinh mã tự động (NV00001...) + chống trùng =====
        synchronized (maLock) {
            String latestMa = getLatestMaFromDb(); // lấy mã mới nhất từ DB (dựa trên findAllOrderByMaDesc)
            String newMa = generateNextMa(latestMa);

            // nếu DB có trường hợp trùng do dữ liệu/đồng bộ, tăng tiếp cho đến khi không trùng
            while (nhanVienRepository.existsByMa(newMa)) {
                newMa = generateNextMa(newMa);
            }
            nv.setMa(newMa);
        }

        // ===== 2) Tự sinh tài khoản + mật khẩu =====
        String taiKhoan = generateUsernameFromName(nv.getHoTen());
        String matKhauRaw = generateRandomPassword();
        String matKhauMaHoa = encoder.encode(matKhauRaw);

        nv.setTaiKhoan(taiKhoan);
        nv.setMatKhau(matKhauMaHoa);

        // ===== 3) Validate chức vụ (nếu FE gửi) =====
        if (nv.getChucVu() != null && nv.getChucVu().getId() != null) {
            ChucVu cv = chucVuRepository.findById(nv.getChucVu().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức vụ không tồn tại"));
            nv.setChucVu(cv);
        }

        // ===== 4) Lưu =====
        NhanVien saved = nhanVienRepository.save(nv);

        // ===== 5) Gửi email tài khoản =====
        if (nv.getEmail() != null && !nv.getEmail().trim().isEmpty()) {
            try {
                sendAccountEmail(nv.getEmail(), nv.getHoTen(), taiKhoan, matKhauRaw);
            } catch (MessagingException e) {
                System.err.println("❌ Gửi email thất bại: " + e.getMessage());
            }
        }

        return saved;
    }

    public NhanVien update(UUID id, NhanVien updated) {
        NhanVien existing = nhanVienRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có ID: " + id));

        // Giữ nguyên mã nhân viên (theo bản 2)
        updated.setMa(existing.getMa());

        // Check email trùng (theo bản 1)
        if (updated.getEmail() != null
                && !updated.getEmail().equals(existing.getEmail())
                && nhanVienRepository.existsByEmail(updated.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email này đã được sử dụng!");
        }

        existing.setHoTen(updated.getHoTen());
        existing.setSdt(updated.getSdt());
        existing.setEmail(updated.getEmail());
        existing.setGioiTinh(updated.getGioiTinh());
        existing.setNgaySinh(updated.getNgaySinh());
        existing.setDiaChi(updated.getDiaChi());
        existing.setCccd(updated.getCccd());

        // urlAnh: nếu FE gửi null thì giữ nguyên; nếu gửi "" thì clear về null
        if (updated.getUrlAnh() != null) {
            String v = updated.getUrlAnh().trim();
            existing.setUrlAnh(v.isEmpty() ? null : v);
        }

        // trangThai: chỉ cập nhật khi có gửi (theo bản 1)
        if (updated.getTrangThai() != null) {
            existing.setTrangThai(updated.getTrangThai());
        }

        // nguoiSua (theo bản 2) - nếu entity có field này
        if (updated.getNguoiSua() != null) {
            existing.setNguoiSua(updated.getNguoiSua());
        }

        // taiKhoan: chỉ đổi khi FE gửi (giữ chức năng bản 2 nhưng an toàn hơn)
        if (updated.getTaiKhoan() != null && !updated.getTaiKhoan().trim().isEmpty()) {
            String newTk = updated.getTaiKhoan().trim();
            if (!newTk.equals(existing.getTaiKhoan()) && nhanVienRepository.existsByTaiKhoan(newTk)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tài khoản đã tồn tại!");
            }
            existing.setTaiKhoan(newTk);
        }

        // matKhau: nếu FE gửi thì cập nhật; hỗ trợ raw hoặc đã hash
        if (updated.getMatKhau() != null && !updated.getMatKhau().trim().isEmpty()) {
            String pw = updated.getMatKhau().trim();
            if (pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$") || pw.startsWith("$2$")) {
                existing.setMatKhau(pw); // đã là BCrypt
            } else {
                existing.setMatKhau(encoder.encode(pw)); // raw -> encode
            }
        }

        // Chức vụ: validate theo id (theo bản 1)
        if (updated.getChucVu() != null && updated.getChucVu().getId() != null) {
            ChucVu cv = chucVuRepository.findById(updated.getChucVu().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chức vụ không tồn tại"));
            existing.setChucVu(cv);
        } else if (updated.getChucVu() != null) {
            // nếu FE gửi cả object chucVu không có id: vẫn set (giữ hành vi bản 2)
            existing.setChucVu(updated.getChucVu());
        }

        return nhanVienRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!nhanVienRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên để xóa");
        }
        nhanVienRepository.deleteById(id);
    }

    // ================== Helpers ==================

    private String getLatestMaFromDb() {
        List<NhanVien> list = nhanVienRepository.findAllOrderByMaDesc();
        if (list == null || list.isEmpty() || list.get(0).getMa() == null) return null;
        return list.get(0).getMa();
    }

    private String generateNextMa(String latestMa) {
        if (latestMa == null || latestMa.isEmpty() || latestMa.length() < 3) {
            return "NV00001";
        }
        // lấy phần số sau "NV"
        String numPart = latestMa.substring(2).replaceAll("[^0-9]", "");
        int number = 0;
        try {
            number = Integer.parseInt(numPart);
        } catch (Exception ignored) { }
        return String.format("NV%05d", number + 1);
    }

    private String generateUsernameFromName(String hoTen) {
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return "user" + System.currentTimeMillis();
        }

        String normalized = java.text.Normalizer.normalize(hoTen, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "d")
                .toLowerCase();

        String[] parts = normalized.trim().split("\\s+");
        String ten = parts[parts.length - 1];
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) prefix.append(parts[i].charAt(0));
        }

        String base = ten + prefix;
        String finalUsername = base;
        int count = 1;

        while (nhanVienRepository.existsByTaiKhoan(finalUsername)) {
            int rand = (int) (Math.random() * 900 + 100); // 100–999
            finalUsername = base + rand + (count++);
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
                  <p>👉 <a href="%s" style="color: #28a745; font-weight: bold;">Đăng nhập ngay</a></p>
                  <p style="margin-top: 20px;">Trân trọng,<br>Phòng Nhân sự</p>
                </div>
                """.formatted(name, username, password, EMPLOYEE_LOGIN_URL);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
