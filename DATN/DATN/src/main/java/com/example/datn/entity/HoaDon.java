package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "ma")
    private String ma;

    @Column(name = "loai_hoa_don")
    private String loaiHoaDon;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;
}