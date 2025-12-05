// src/main/java/com/example/datn/entity/HoaDon.java
package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @Column(name = "ma")
    private String ma;

    @Column(name = "loai_hoa_don")
    private String loaiHoaDon;

    // mô tả ngắn sp: "Giày X x2, Giày Y x1..."
    @Column(name = "ten_san_pham")
    private String tenSanPham;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "email_khach_hang")
    private String emailKhachHang;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @Column(name = "phi_van_chuyen")
    private BigDecimal phiVanChuyen;

    // Tạm tính (chưa ship, chưa giảm)
    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    // Tổng sau giảm (đã ship, đã áp mã, v.v.)
    @Column(name = "tong_tien_sau_giam")
    private BigDecimal tongTienSauGiam;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @Column(name = "nguoi_tao")
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    private String nguoiSua;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    // ✅ THÊM: danh sách hóa đơn chi tiết để join fetch được
    // mappedBy phải đúng tên field trong HoaDonChiTiet (thường là "hoaDon")
    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTietList = new ArrayList<>();
}
