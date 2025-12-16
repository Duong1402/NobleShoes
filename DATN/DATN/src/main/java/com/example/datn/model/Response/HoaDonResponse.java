package com.example.datn.model.Response;

import com.example.datn.entity.DiaChi;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.LichSuHoaDon;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class HoaDonResponse {

    private UUID id;
    private String ma;
    private String loaiHoaDon;   // Online / Tại quầy / ...
    private String tenSanPham;   // mô tả ngắn “Giày X x2,...” (nếu có)
    private String tenKhachHang;
    private String emailKhachHang;
    private String sdt;
    private String tenNhanVien;
    private String diaChiGiaoHang;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private Integer trangThai;
    private LocalDate ngayTao;
    private String ghiChu;
    private List<LichSuHoaDon> lichSuHoaDon;

    public HoaDonResponse(HoaDon hd) {
        if (hd == null) return;

        this.id = hd.getId();
        this.ma = hd.getMa();
        this.tenSanPham = hd.getTenSanPham();

        // ===== KHÁCH HÀNG =====
        String tenHD = hd.getTenKhachHang();
        if (tenHD != null && !tenHD.trim().isEmpty()) {
            this.tenKhachHang = tenHD;
        } else if (hd.getKhachHang() != null && hd.getKhachHang().getHoTen() != null) {
            this.tenKhachHang = hd.getKhachHang().getHoTen();
        } else {
            this.tenKhachHang = "Khách lẻ";
        }

        this.emailKhachHang = hd.getEmailKhachHang();
        if (hd.getSdt() != null && !hd.getSdt().trim().isEmpty()) {
            this.sdt = hd.getSdt();
        } else if (hd.getKhachHang() != null) {
            this.sdt = hd.getKhachHang().getSdt();
        } else {
            this.sdt = null; // Hoặc "" tùy bạn
        }

        // ===== NHÂN VIÊN =====
        if (hd.getNhanVien() != null) {
            this.tenNhanVien = hd.getNhanVien().getHoTen();
        } else {
            this.tenNhanVien = "Chờ xử lý";
        }

        // ===== TÀI CHÍNH =====
        this.phiVanChuyen = hd.getPhiVanChuyen();
        this.tongTien = hd.getTongTien();
        this.tongTienSauGiam = hd.getTongTienSauGiam();
        this.trangThai = hd.getTrangThai();
        this.ngayTao = hd.getNgayTao();
        this.ghiChu = hd.getGhiChu();

        // ===== ĐỊA CHỈ + LOẠI HÓA ĐƠN =====
        String diaChiHD = hd.getDiaChiGiaoHang();
        if (diaChiHD != null && !diaChiHD.trim().isEmpty()) {
            // đã có địa chỉ trong hóa đơn
            this.diaChiGiaoHang = diaChiHD;
        } else {
            // fallback: lấy từ sổ địa chỉ khách hàng (nếu có)
            this.diaChiGiaoHang = buildDiaChiFromKhachHang(hd);
        }

        // loaiHoaDon: ưu tiên giá trị thực trong DB, nếu null thì suy ra
        if (hd.getLoaiHoaDon() != null && !hd.getLoaiHoaDon().trim().isEmpty()) {
            this.loaiHoaDon = hd.getLoaiHoaDon();
        } else {
            // có địa chỉ => Online, không có => Tại quầy
            this.loaiHoaDon = (this.diaChiGiaoHang != null && !this.diaChiGiaoHang.trim().isEmpty())
                    ? "Online"
                    : "Tại quầy";
        }

        // ===== LỊCH SỬ =====
        if (hd.getLichSuHoaDons() != null) {
            this.lichSuHoaDon = hd.getLichSuHoaDons().stream()
                    .sorted(Comparator.comparing(LichSuHoaDon::getThoiGian).reversed()) // .reversed() để mới nhất lên đầu
                    .collect(Collectors.toList());
        } else {
            this.lichSuHoaDon = Collections.emptyList();
        }
    }

    private String buildDiaChiFromKhachHang(HoaDon hd) {
        if (hd.getKhachHang() == null) return "";
        List<DiaChi> list = hd.getKhachHang().getDanhSachDiaChi();
        if (list == null || list.isEmpty()) return "";

        DiaChi dc = list.get(0);

        StringBuilder sb = new StringBuilder();
        if (dc.getDiaChiCuThe() != null && !dc.getDiaChiCuThe().isBlank()) sb.append(dc.getDiaChiCuThe());
        if (dc.getXa() != null && !dc.getXa().isBlank()) appendComma(sb).append(dc.getXa());
        if (dc.getHuyen() != null && !dc.getHuyen().isBlank()) appendComma(sb).append(dc.getHuyen());
        if (dc.getThanhPho() != null && !dc.getThanhPho().isBlank()) appendComma(sb).append(dc.getThanhPho());

        return sb.toString();
    }

    private StringBuilder appendComma(StringBuilder sb) {
        if (sb.length() > 0) sb.append(", ");
        return sb;
    }

}