package com.example.datn.service;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    @Value("${app.mail.from-name:NobleShoes}")
    private String fromName;

    // app.mail.from-email=${MAIL_USERNAME_ENV}
    @Value("${app.mail.from-email:}")
    private String fromEmail;

    private static final Locale VI_VN = new Locale("vi", "VN");

    public MailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @PostConstruct
    public void printMailConfigOnce() {
        String springUser = env.getProperty("spring.mail.username");
        System.out.println("[MAIL] spring.mail.username = " + springUser);
        if (springUser == null || springUser.isBlank()) {
            System.out.println("[MAIL] ❌ Bạn CHƯA set ENV MAIL_USERNAME_ENV / MAIL_PASSWORD_ENV");
        }
    }

    // ✅ HÀM CŨ (GIỮ) — nếu chỗ khác đang gọi
    @Async
    public void sendOrderConfirmation(String toEmail, String orderCode, String fullName, long grandTotalVnd) {
        if (toEmail == null || toEmail.isBlank()) return;

        String code = (orderCode == null || orderCode.isBlank()) ? "N/A" : orderCode.trim();

        // ✅ Subject KHÔNG esc (để mail preview không bị lạ)
        String subject = "[NobleShoes] Xác nhận đơn hàng #" + code;

        String total = NumberFormat.getCurrencyInstance(VI_VN).format(grandTotalVnd);

        // ✅ Plain text để preview/snippet hiện rõ mã đơn
        String plain = ""
                + "Xác nhận đơn hàng #" + code + "\n"
                + "Khách hàng: " + nz(fullName) + "\n"
                + "Tổng thanh toán: " + total + "\n"
                + "\nNobleShoes";

        String html = """
          <div style="font-family:Arial,sans-serif;line-height:1.6;color:#111">
            <h2 style="margin:0 0 8px">Cảm ơn bạn đã đặt hàng!</h2>

            <p style="margin:0 0 10px">
              <b>Mã đơn hàng:</b> <span style="font-size:16px">#%s</span>
            </p>

            <p style="margin:0 0 12px">Xin chào <b>%s</b>, đơn hàng của bạn đã được tạo thành công.</p>

            <div style="padding:12px;border:1px solid #eee;border-radius:12px;background:#fafafa;margin:12px 0">
              <div><b>Tổng thanh toán:</b> %s</div>
            </div>

            <p style="margin-top:16px;color:#666;font-size:13px">
              Nếu bạn không thực hiện đơn hàng này, vui lòng bỏ qua email.
            </p>
            <p style="margin:0;font-weight:700">%s</p>
          </div>
        """.formatted(esc(code), esc(fullName), total, esc(fromName));

        sendTextAndHtml(toEmail, subject, plain, html);
    }

    // ✅ OVERLOAD — HoaDonService gọi kiểu (HoaDon, List<HoaDonChiTiet>)
    @Async
    public void sendOrderConfirmation(HoaDon hd, List<HoaDonChiTiet> items) {
        if (hd == null) return;
        if (hd.getEmailKhachHang() == null || hd.getEmailKhachHang().isBlank()) return;

        String code = (hd.getMa() == null || hd.getMa().isBlank()) ? "N/A" : hd.getMa().trim();

        // ✅ Subject KHÔNG esc
        String subject = "[NobleShoes] Xác nhận đơn hàng #" + code;

        long totalLong = (hd.getTongTien() == null) ? 0 : hd.getTongTien().longValue();
        String total = NumberFormat.getCurrencyInstance(VI_VN).format(totalLong);

        // ✅ Plain text để preview/snippet hiện rõ mã đơn
        String plain = ""
                + "Xác nhận đơn hàng #" + code + "\n"
                + "Khách hàng: " + nz(hd.getTenKhachHang()) + "\n"
                + "Email: " + nz(hd.getEmailKhachHang()) + "\n"
                + "SĐT: " + nz(hd.getSdt()) + "\n"
                + "Địa chỉ: " + nz(hd.getDiaChiGiaoHang()) + "\n"
                + "Tổng thanh toán: " + total + "\n"
                + "\nNobleShoes";

        StringBuilder rows = new StringBuilder();
        if (items != null) {
            for (HoaDonChiTiet it : items) {
                String ten = esc(it.getTenSanPham());
                String size = it.getSize() == null ? "" : ("Size " + esc(it.getSize()));
                int qty = it.getSoLuong() == null ? 0 : it.getSoLuong();

                long line = (it.getThanhTien() == null ? 0 : it.getThanhTien().longValue());
                String lineMoney = NumberFormat.getCurrencyInstance(VI_VN).format(line);

                rows.append("""
                  <tr>
                    <td style="padding:8px 10px;border-bottom:1px solid #eee">
                      <div style="font-weight:600">%s</div>
                      <div style="color:#666;font-size:12px">%s</div>
                    </td>
                    <td style="padding:8px 10px;border-bottom:1px solid #eee;text-align:center">%d</td>
                    <td style="padding:8px 10px;border-bottom:1px solid #eee;text-align:right">%s</td>
                  </tr>
                """.formatted(ten, size, qty, lineMoney));
            }
        }

        String html = """
          <div style="font-family:Arial,sans-serif;line-height:1.6;color:#111">
            <h2 style="margin:0 0 8px">Cảm ơn bạn đã đặt hàng!</h2>

            <p style="margin:0 0 10px">
              <b>Mã đơn hàng:</b> <span style="font-size:16px">#%s</span>
            </p>

            <p style="margin:0 0 12px">Xin chào <b>%s</b>, đơn hàng của bạn đã được tạo thành công.</p>

            <div style="padding:12px;border:1px solid #eee;border-radius:12px;background:#fafafa;margin:12px 0">
              <div><b>Email:</b> %s</div>
              <div><b>SĐT:</b> %s</div>
              <div><b>Địa chỉ:</b> %s</div>
            </div>

            <h3 style="margin:16px 0 8px;font-size:16px">Chi tiết đơn hàng</h3>
            <table style="width:100%%;border-collapse:collapse;border:1px solid #eee;border-radius:12px;overflow:hidden">
              <thead>
                <tr style="background:#f4f4f4">
                  <th style="text-align:left;padding:10px">Sản phẩm</th>
                  <th style="text-align:center;padding:10px;width:80px">SL</th>
                  <th style="text-align:right;padding:10px;width:160px">Thành tiền</th>
                </tr>
              </thead>
              <tbody>%s</tbody>
            </table>

            <div style="margin-top:12px;text-align:right">
              <div style="font-size:14px;color:#666">Tổng thanh toán</div>
              <div style="font-size:18px;font-weight:700">%s</div>
            </div>

            <p style="margin-top:16px;color:#666;font-size:13px">
              Nếu bạn không thực hiện đơn hàng này, vui lòng bỏ qua email.
            </p>
            <p style="margin:0;font-weight:700">%s</p>
          </div>
        """.formatted(
                esc(code),
                esc(hd.getTenKhachHang()),
                esc(hd.getEmailKhachHang()),
                esc(hd.getSdt()),
                esc(hd.getDiaChiGiaoHang()),
                rows.toString(),
                total,
                esc(fromName)
        );

        sendTextAndHtml(hd.getEmailKhachHang(), subject, plain, html);
    }

    // ✅ Gửi BOTH: text + html để email preview/snippet hiện mã đơn rõ
    private void sendTextAndHtml(String toEmail, String subject, String plainText, String html) {
        String springUser = env.getProperty("spring.mail.username");

        // fromEmail fallback
        String safeFromEmail = (fromEmail != null && !fromEmail.isBlank())
                ? fromEmail
                : (springUser != null ? springUser : "");

        // ✅ tránh lỗi whitespace / dính "MAIL_PASSWORD_ENV=..."
        safeFromEmail = safeFromEmail.trim();
        if (safeFromEmail.contains(" ")) safeFromEmail = safeFromEmail.split("\\s+")[0];

        if (safeFromEmail.isBlank()) {
            System.out.println("[MAIL] ❌ Chưa có from email (ENV chưa set) -> skip gửi mail.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(safeFromEmail, fromName);
            helper.setReplyTo(safeFromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // ✅ setText(text, html)
            helper.setText(plainText == null ? "" : plainText, html);

            mailSender.send(message);
            System.out.println("[MAIL] ✅ Sent to " + toEmail + " subject=" + subject);

        } catch (MailAuthenticationException e) {
            System.out.println("[MAIL] ❌ AUTH FAIL (535): Gmail không accept username/password.");
            System.out.println("[MAIL] -> BẮT BUỘC dùng App Password 16 ký tự + bật 2FA.");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
