package com.example.datn.service;

import com.example.datn.entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLEncoder;
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

    @Value("${app.mail.from-email:}")
    private String fromEmail;

    // click mã đơn -> FE theo dõi
    @Value("${app.frontend.order-tracking-base:http://localhost:5173/orders/}")
    private String trackingBase;

    private static final Locale VI_VN = new Locale("vi", "VN");

    private static final String FONT =
            "-apple-system,BlinkMacSystemFont,\"Segoe UI\",Roboto,Helvetica,Arial,sans-serif";

    public MailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @PostConstruct
    public void printMailConfigOnce() {
        System.out.println("[MAIL] spring.mail.username = " + env.getProperty("spring.mail.username"));
        System.out.println("[MAIL] trackingBase = " + trackingBase);
    }

    @Async
    public void sendOrderConfirmation(HoaDon hd, List<HoaDonChiTiet> items) {
        if (hd == null) return;
        if (hd.getEmailKhachHang() == null || hd.getEmailKhachHang().isBlank()) return;

        String code = safeTrim(hd.getMa(), "N/A");
        String subject = "[" + nz(fromName) + "] Xác nhận đơn hàng #" + code;
        String trackingUrl = buildTrackingUrl(code);

        String customerName = normalizeText(hd.getTenKhachHang(), "—");
        String customerEmail = normalizeText(hd.getEmailKhachHang(), "—");
        String customerPhone = normalizeText(hd.getSdt(), "—");
        String address = normalizeText(hd.getDiaChiGiaoHang(), "—");

        // ✅ lấy tổng phải trả
        long grandTotal = readLong(hd,
                "getTongTienThanhToan",
                "getTongTienSauGiam",
                "getSoTienPhaiTra",
                "getTongTienCanThanhToan",
                "getThanhTien",
                "getTongTien"
        );
        if (grandTotal <= 0) grandTotal = toLong(hd.getTongTien());

        // ✅ tạm tính từ items
        long subtotal = 0;
        if (items != null) {
            for (HoaDonChiTiet it : items) {
                subtotal += toLong(it != null ? it.getThanhTien() : null);
            }
        }

        long shippingFee = readLong(hd,
                "getPhiVanChuyen", "getPhiGiaoHang", "getTienShip", "getPhiShip", "getPhiVC"
        );

        // ❌ bỏ COD
        // long codFee = readLong(hd, "getPhiCod","getPhuThuCOD","getPhiThuHo","getCodFee","getPhiCOD");

        long discount = readLong(hd,
                "getGiamGia",
                "getTienGiam",
                "getSoTienGiam",
                "getTienGiamGia",
                "getTienKhuyenMai",
                "getDiscount",
                "getGiaTriGiam"
        );

        if (discount <= 0) {
            long calc = (subtotal + shippingFee) - grandTotal;
            discount = Math.max(calc, 0);
        }

        String shipText = shippingFee <= 0 ? "0 ₫" : money(shippingFee);
        String discountText = discount <= 0 ? "0 ₫" : money(discount);

        int totalQty = countTotalQty(items);

        String plain = ""
                + "Cảm ơn bạn đã đặt hàng\n"
                + "Mã đơn: #" + code + "\n"
                + "Email: " + customerEmail + "\n"
                + "Khách hàng: " + customerName + "\n"
                + "SĐT: " + customerPhone + "\n"
                + "Địa chỉ: " + address + "\n"
                + "Tạm tính: " + money(subtotal) + "\n"
                + "Phí vận chuyển: " + shipText + "\n"
                + "Giảm giá: -" + discountText + "\n"
                + "Tổng cộng: " + money(grandTotal) + "\n"
                + "Theo dõi đơn: " + trackingUrl + "\n"
                + "\n" + fromName;

        String rowsHtml = buildRowsNoImage(items);

        String html = """
                <!doctype html>
                <html>
                <head>
                  <meta charset="utf-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1">
                  <title>%s - Xác nhận đơn hàng</title>
                </head>
                <body style="margin:0;background:#f6f7fb;font-family:%s">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f6f7fb;padding:24px 0">
                    <tr>
                      <td align="center" style="padding:0 12px">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:760px">
                          <tr>
                            <td style="background:#fff;border:1px solid #e9e9e9;border-radius:18px;padding:18px">

                              <div style="font-size:26px;font-weight:700;color:#111;margin-bottom:6px">
                                Cảm ơn bạn đã đặt hàng
                              </div>
                              <div style="font-size:13px;color:#6b7280;margin-bottom:14px;line-height:1.6">
                                Một email xác nhận đã được gửi tới <b style="color:#111;font-weight:600">%s</b>. Xin vui lòng kiểm tra hộp thư của bạn.
                              </div>

                              <div style="margin-bottom:14px">
                                <table role="presentation" cellpadding="0" cellspacing="0"
                                       style="border-collapse:separate;border-spacing:8px 0">
                                  <tr>
                <td style="vertical-align:middle">
                                      <span style="display:inline-block;border:1px solid #e5e7eb;border-radius:999px;background:#fff;
                                                   padding:8px 12px;font-weight:600;font-size:13px;line-height:16px;color:#111;white-space:nowrap">
                                        <span style="display:inline-block;width:8px;height:8px;border-radius:999px;background:#16a34a;
                                                     vertical-align:middle;margin-right:8px"></span>
                                        <span style="vertical-align:middle">Đặt hàng thành công</span>
                                      </span>
                                    </td>

                                    <td style="vertical-align:middle">
                                      <span style="display:inline-block;border:1px solid #e5e7eb;border-radius:999px;background:#fff;
                                                   padding:8px 12px;font-weight:600;font-size:13px;line-height:16px;color:#111;white-space:nowrap">
                                        <span style="vertical-align:middle">Mã đơn:</span>
                                        <a href="%s" style="color:#0b57d0;text-decoration:none;font-weight:600;margin-left:6px;vertical-align:middle">#%s</a>
                                      </span>
                                    </td>

                                    <td style="vertical-align:middle">
                                      <span style="display:inline-block;border:1px solid #e5e7eb;border-radius:999px;background:#fff;
                                                   padding:8px 12px;font-weight:600;font-size:13px;line-height:16px;color:#111;white-space:nowrap">
                                        <span style="display:inline-block;width:8px;height:8px;border-radius:999px;background:#f59e0b;
                                                     vertical-align:middle;margin-right:8px"></span>
                                        <span style="vertical-align:middle">%d sản phẩm</span>
                                      </span>
                                    </td>
                                  </tr>
                                </table>
                              </div>

                              <div style="border:1px solid #eef0f3;border-radius:16px;overflow:hidden;margin-bottom:12px">
                                <div style="padding:12px 14px;background:#fafafa;font-weight:600;font-size:14px">Thông tin mua hàng</div>
                                <div style="padding:14px">
                                  <div style="font-weight:600;color:#111;font-size:14px">%s</div>
                                  <div style="margin-top:8px;font-size:14px">
                                    <a style="color:#0b57d0;text-decoration:none" href="mailto:%s">%s</a>
                                  </div>
                                  <div style="margin-top:6px;color:#111;font-size:14px">%s</div>
                                </div>
                              </div>

                              <div style="border:1px solid #eef0f3;border-radius:16px;overflow:hidden;margin-bottom:18px">
                                <div style="padding:12px 14px;background:#fafafa;font-weight:600;font-size:14px">Địa chỉ nhận hàng</div>
                                <div style="padding:14px;color:#111;font-size:14px">%s</div>
                              </div>
                <div style="font-weight:600;color:#111;margin:0 0 10px 0;font-size:15px">
                                Chi tiết đơn hàng
                              </div>

                              <!-- ✅ TABLE CHUẨN GMAIL: colgroup width attribute + th align + table-layout fixed -->
                              <div style="border:1px solid #eef0f3;border-radius:16px;overflow:hidden;margin-bottom:16px">
                                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                                       style="border-collapse:collapse;table-layout:fixed">
                                  <colgroup>
                                    <col width="*" />
                                    <col width="120" />
                                    <col width="60" />
                                    <col width="140" />
                                  </colgroup>

                                  <tr style="background:#fafafa">
                                    <th align="left" style="padding:12px;color:#111;font-weight:600;font-size:13px">Sản phẩm</th>
                                    <th align="right" style="padding:12px;color:#111;font-weight:600;font-size:13px">Đơn giá</th>
                                    <th align="center" style="padding:12px;color:#111;font-weight:600;font-size:13px">SL</th>
                                    <th align="right" style="padding:12px;color:#111;font-weight:600;font-size:13px">Thành tiền</th>
                                  </tr>

                                  %s
                                </table>
                              </div>

                              <div style="border:1px solid #eef0f3;border-radius:16px;overflow:hidden">
                                <div style="padding:12px 14px;background:#fafafa;font-weight:600;font-size:14px">
                                  Đơn hàng <span style="color:#6b7280">#%s</span>
                                </div>
                                <div style="padding:14px">
                                  <div style="display:flex;justify-content:space-between;margin:8px 0;font-size:14px">
                                    <span style="color:#6b7280">Tạm tính: </span><b style="font-weight:600"> %s</b>
                                  </div>
                                  <div style="display:flex;justify-content:space-between;margin:8px 0;font-size:14px">
                                    <span style="color:#6b7280">Phí vận chuyển: </span><b style="font-weight:600"> %s</b>
                                  </div>

                                  <div style="display:flex;justify-content:space-between;margin:8px 0;font-size:14px">
                                    <span style="color:#16a34a;font-weight:600">Giảm giá: </span>
                                    <b style="color:#16a34a;font-weight:600"> -%s</b>
                                  </div>

                                  <div style="height:1px;background:#eef0f3;margin:14px 0"></div>

                                  <div style="display:flex;justify-content:space-between;margin:8px 0;font-size:18px">
                                    <span style="font-weight:700">Tổng cộng: </span><span style="font-weight:700"> %s</span>
                                  </div>

                                  <div style="margin-top:10px;color:#6b7280;font-size:13px;line-height:1.6">
                                    Bạn có thể theo dõi trạng thái đơn hàng bằng mã đơn phía trên hoặc nút “Theo dõi đơn hàng”.
                                  </div>
                <div style="margin-top:12px">
                                    <a href="%s" style="display:inline-block;background:#111;color:#fff;text-decoration:none;
                                                       padding:10px 14px;border-radius:12px;font-weight:600;font-size:14px">
                                      Theo dõi đơn hàng
                                    </a>
                                  </div>
                                </div>
                              </div>

                              <div style="margin-top:14px;color:#6b7280;font-size:13px;line-height:1.6">
                                Nếu bạn không thực hiện đơn hàng này, vui lòng bỏ qua email.
                              </div>
                              <div style="margin-top:6px;font-weight:600">%s</div>

                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(
                esc(fromName),
                esc(FONT),

                esc(customerEmail),

                esc(trackingUrl), esc(code), totalQty,

                esc(customerName),
                esc(customerEmail), esc(customerEmail),
                esc(customerPhone),

                esc(address),

                rowsHtml,

                esc(code),
                money(subtotal),
                esc(shipText),
                esc(discountText),
                money(grandTotal),

                esc(trackingUrl),
                esc(fromName)
        );

        sendTextAndHtml(hd.getEmailKhachHang(), subject, plain, html);
    }

    // ✅ Row chuẩn Gmail: canh top + wrap tên sản phẩm + cột số nowrap
    private String buildRowsNoImage(List<HoaDonChiTiet> items) {
        if (items == null || items.isEmpty()) {
            return """
                      <tr>
                        <td colspan="4" style="padding:12px;font-family:%s;color:#6b7280;font-size:14px">
                          Không có sản phẩm trong đơn.
                        </td>
                      </tr>
                    """.formatted(esc(FONT));
        }

        StringBuilder rows = new StringBuilder();
        for (HoaDonChiTiet it : items) {
            if (it == null) continue;

            String ten = esc(getProductName(it));
            int qty = it.getSoLuong() == null ? 0 : it.getSoLuong();
            long unit = toLong(it.getDonGia());
            long lineTotal = toLong(it.getThanhTien());

            rows.append("""
                                  <tr style="border-top:1px solid #eef0f3">
                                    <td style="padding:12px;vertical-align:top">
                                      <div style="font-family:%s;font-weight:500;color:#111;font-size:14px;line-height:1.35;
                                                  word-break:break-word;overflow-wrap:anywhere">
                                        %s
                                      </div>
                                    </td>
                                    <td align="right" style="padding:12px;white-space:nowrap;vertical-align:top;
                                               font-family:%s;color:#111;font-size:14px">%s</td>
                                    <td align="center" style="padding:12px;white-space:nowrap;vertical-align:top;
                                               font-family:%s;color:#111;font-size:14px">%d</td>
                    <td align="right" style="padding:12px;white-space:nowrap;vertical-align:top;
                                               font-family:%s;font-weight:600;color:#111;font-size:14px">%s</td>
                                  </tr>
                                """.formatted(
                    esc(FONT), ten,
                    esc(FONT), money(unit),
                    esc(FONT), qty,
                    esc(FONT), money(lineTotal)
            ));
        }
        return rows.toString();
    }

    private void sendTextAndHtml(String toEmail, String subject, String plainText, String html) {
        String springUser = env.getProperty("spring.mail.username");

        String safeFromEmail = (fromEmail != null && !fromEmail.isBlank())
                ? fromEmail
                : (springUser != null ? springUser : "");

        safeFromEmail = safeFromEmail.trim();
        if (safeFromEmail.contains(" ")) safeFromEmail = safeFromEmail.split("\\s+")[0];

        if (safeFromEmail.isBlank()) {
            System.out.println("[MAIL] ❌ Chưa có from email -> skip gửi mail.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(safeFromEmail, fromName);
            helper.setReplyTo(safeFromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);

            helper.setText(plainText == null ? "" : plainText, html == null ? "" : html);

            mailSender.send(message);
            System.out.println("[MAIL] ✅ Sent to " + toEmail + " subject=" + subject);

        } catch (MailAuthenticationException e) {
            System.out.println("[MAIL] ❌ AUTH FAIL (535): Gmail không accept username/password (cần App Password).");
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getProductName(HoaDonChiTiet it) {
        if (it == null) return "Sản phẩm";

        if (it.getTenSanPham() != null && !it.getTenSanPham().trim().isBlank()) {
            return it.getTenSanPham().trim();
        }

        ChiTietSanPham ctsp = it.getChiTietSanPham();
        if (ctsp != null) {
            SanPham sp = ctsp.getSanPham();
            if (sp != null) {
                String name = readString(sp, "getTenSanPham", "getTen", "getName");
                if (!"—".equals(name)) return name;
            }
        }
        return "Sản phẩm";
    }

    private String buildTrackingUrl(String code) {
        String base = (trackingBase == null || trackingBase.isBlank())
                ? "http://localhost:5173/orders/"
                : trackingBase.trim();
        if (!base.endsWith("/")) base = base + "/";
        return base + URLEncoder.encode(code == null ? "" : code, StandardCharsets.UTF_8);
    }

    private String money(long vnd) {
        return NumberFormat.getCurrencyInstance(VI_VN).format(vnd);
    }

    private String safeTrim(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        return t.isBlank() ? fallback : t;
    }

    private String normalizeText(String s, String fallback) {
        if (s == null) return fallback;
        String t = s.trim();
        if (t.isBlank() || t.equals("—") || t.equalsIgnoreCase("null")) return fallback;
        return t;
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private long toLong(Object v) {
        if (v == null) return 0;
        if (v instanceof BigDecimal) return ((BigDecimal) v).longValue();
        if (v instanceof Number) return ((Number) v).longValue();
        if (v instanceof String) {
            try {
                return new BigDecimal(((String) v).trim()).longValue();
            } catch (Exception ignore) {
                return 0;
            }
        }
        return 0;
    }

    private long readLong(Object obj, String... methodNames) {
        if (obj == null || methodNames == null) return 0;
        for (String m : methodNames) {
            try {
                Method method = obj.getClass().getMethod(m);
                Object val = method.invoke(obj);
                long n = toLong(val);
                if (n != 0) return n;
            } catch (Exception ignore) {
            }
        }
        return 0;
    }

    private String readString(Object obj, String... methodNames) {
        if (obj == null || methodNames == null) return "—";
        for (String m : methodNames) {
            try {
                Method method = obj.getClass().getMethod(m);
                Object val = method.invoke(obj);
                if (val != null) {
                    String s = String.valueOf(val).trim();
                    if (!s.isBlank()) return s;
                }
            } catch (Exception ignore) {
            }
        }
        return "—";
    }

    private int countTotalQty(List<HoaDonChiTiet> items) {
        if (items == null) return 0;
        int sum = 0;
        for (HoaDonChiTiet it : items) {
            if (it == null) continue;
            Integer q = it.getSoLuong();
            sum += (q == null ? 0 : q);
        }
        return sum;
    }
}