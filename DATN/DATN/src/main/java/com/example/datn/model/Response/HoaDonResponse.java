package com.example.datn.model.Response;

import com.example.datn.entity.HoaDon;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class HoaDonResponse {
    private UUID id;
    private String ma;
    private String tenKhachHang;
    private String tenNhanVien;
    private LocalDate ngayTao;
    private BigDecimal tongTien;
    private String loaiHoaDon;
    private Integer trangThai;

    public HoaDonResponse(HoaDon hd) {
        this.id = hd.getId();
        this.ma = hd.getMa();

        if (hd.getTenKhachHang() != null && !hd.getTenKhachHang().isEmpty()) {
            this.tenKhachHang = hd.getTenKhachHang();
        } else if (hd.getKhachHang() != null) {
            this.tenKhachHang = hd.getKhachHang().getHoTen();
        } else {
            this.tenKhachHang = "Không có";
        }


        this.tenNhanVien = (hd.getNhanVien() != null) ? hd.getNhanVien().getHoTen() : "N/A";

        this.ngayTao = hd.getNgayTao();
        this.tongTien = hd.getTongTien();
        this.loaiHoaDon = hd.getLoaiHoaDon();
        this.trangThai = hd.getTrangThai();
    }
}