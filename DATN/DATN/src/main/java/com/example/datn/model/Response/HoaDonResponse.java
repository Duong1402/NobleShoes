package com.example.datn.model.Response;

import com.example.datn.entity.DiaChi;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.LichSuHoaDon;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class HoaDonResponse {
    private UUID id;
    private String ma;
    private String tenKhachHang;
    private String sdt;
    private String tenNhanVien;
    private LocalDate ngayTao;
    private BigDecimal tongTien;
    private String loaiHoaDon;
    private Integer trangThai;
    private String diaChiGiaoHang;

    private List<LichSuHoaDon> lichSuHoaDon;

    public HoaDonResponse(HoaDon hd) {
        this.id = hd.getId();
        this.ma = hd.getMa();

        if (hd.getTenKhachHang() != null && !hd.getTenKhachHang().isEmpty()) {
            this.tenKhachHang = hd.getTenKhachHang();
        } else if (hd.getKhachHang() != null) {
            this.tenKhachHang = hd.getKhachHang().getHoTen();
        } else {
            this.tenKhachHang = "Khách lẻ";
        }

        this.sdt = hd.getSdt();
        this.tenNhanVien = (hd.getNhanVien() != null) ? hd.getNhanVien().getMa() : "N/A";
        this.ngayTao = hd.getNgayTao();
        this.tongTien = hd.getTongTien();
        this.trangThai = hd.getTrangThai();

        String diaChiHienTai = hd.getDiaChiGiaoHang();

        if (diaChiHienTai != null && !diaChiHienTai.trim().isEmpty()) {
            // CASE 1: Đã có địa chỉ trong hóa đơn -> Dùng luôn
            this.diaChiGiaoHang = diaChiHienTai;
            this.loaiHoaDon = "Online";
        }
        else {
            // CASE 2: Hóa đơn chưa có địa chỉ -> Thử tìm trong sổ địa chỉ Khách Hàng
            // Phải check kỹ hd.getKhachHang() để tránh lỗi NullPointer
            if (hd.getKhachHang() != null) {

                // ⚠️ Lưu ý: Kiểm tra tên hàm getDanhSachDiaChi() hay getListDiaChi() bên Entity nhé
                List<DiaChi> listDC = hd.getKhachHang().getDanhSachDiaChi();

                if (listDC == null) {
                    System.out.println("DEBUG LIST ĐỊA CHỈ: NULL");
                } else {
                    System.out.println("DEBUG LIST ĐỊA CHỈ SIZE: " + listDC.size());
                }

                if (listDC != null && !listDC.isEmpty()) {
                    // Lấy cái đầu tiên
                    DiaChi dc = listDC.get(0);

                    System.out.println("--- CHI TIẾT ĐỊA CHỈ ĐẦU TIÊN ---");
                    System.out.println("ID: " + dc.getId());
                    System.out.println("Cụ thể: " + dc.getDiaChiCuThe());
                    System.out.println("Xã: " + dc.getXa());
                    System.out.println("Huyện: " + dc.getHuyen());
                    System.out.println("TP: " + dc.getThanhPho());

                    // Ghép chuỗi
                    String diaChiGhep = "";
                    if (dc.getDiaChiCuThe() != null) diaChiGhep += dc.getDiaChiCuThe();
                    if (dc.getXa() != null) diaChiGhep += ", " + dc.getXa();
                    if (dc.getHuyen() != null) diaChiGhep += ", " + dc.getHuyen();
                    if (dc.getThanhPho() != null) diaChiGhep += ", " + dc.getThanhPho();

                    // Xử lý dấu phẩy thừa
                    if (diaChiGhep.startsWith(", ")) diaChiGhep = diaChiGhep.substring(2);

                    this.diaChiGiaoHang = diaChiGhep;
                    this.loaiHoaDon = "Online"; // Có địa chỉ gợi ý -> Hiện form
                } else {
                    // Khách hàng có tài khoản nhưng chưa lưu địa chỉ nào
                    this.diaChiGiaoHang = "";
                    this.loaiHoaDon = (hd.getLoaiHoaDon() != null) ? hd.getLoaiHoaDon() : "Tại quầy";
                }
            } else {
                // CASE 3: Khách lẻ (null) -> Không có địa chỉ
                this.diaChiGiaoHang = "";
                this.loaiHoaDon = (hd.getLoaiHoaDon() != null) ? hd.getLoaiHoaDon() : "Tại quầy";
            }
        }

        // Lịch sử
        if (hd.getLichSuHoaDons() != null) {
            this.lichSuHoaDon = hd.getLichSuHoaDons()
                    .stream()
                    .sorted((a, b) -> {
                        if (a.getThoiGian() == null) return -1;
                        if (b.getThoiGian() == null) return 1;
                        return a.getThoiGian().compareTo(b.getThoiGian());
                    })
                    .collect(Collectors.toList());
        }
    }
}