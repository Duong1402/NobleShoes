// src/main/java/com/example/datn/model/Response/HoaDonResponse.java
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
    private String loaiHoaDon;

    private String tenSanPham;

    private String tenKhachHang;
    private String emailKhachHang;
    private String sdt;
    private String diaChiGiaoHang;

    private BigDecimal phiVanChuyen;      // ship
    private BigDecimal tongTien;          // tạm tính
    private BigDecimal tongTienSauGiam;   // tổng cộng sau giảm

    private Integer trangThai;
    private LocalDate ngayTao;
    private String ghiChu;

    public HoaDonResponse(HoaDon hd) {
        this.id = hd.getId();
        this.ma = hd.getMa();
        this.loaiHoaDon = hd.getLoaiHoaDon();

        this.tenSanPham = hd.getTenSanPham();

        this.tenKhachHang = hd.getTenKhachHang();
        this.emailKhachHang = hd.getEmailKhachHang();
        this.sdt = hd.getSdt();
        this.diaChiGiaoHang = hd.getDiaChiGiaoHang();

        this.phiVanChuyen = hd.getPhiVanChuyen();
        this.tongTien = hd.getTongTien();
        this.tongTienSauGiam = hd.getTongTienSauGiam();

        this.trangThai = hd.getTrangThai();
        this.ngayTao = hd.getNgayTao();
        this.ghiChu = hd.getGhiChu();
    }
}