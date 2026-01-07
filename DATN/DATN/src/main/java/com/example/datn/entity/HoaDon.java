package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // 🔹 Liên kết với bảng nhân viên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    // 🔹 Liên kết với bảng khách hàng
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    // 🔹 Liên kết với bảng phiếu giảm giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma", length = 50)
    private String ma;

    @Column(name = "loai_hoa_don", length = 50)
    private String loaiHoaDon;

    @Column(name = "ten_san_pham", length = 100)
    private String tenSanPham;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "dia_chi_giao_hang", length = 100)
    private String diaChiGiaoHang;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email_khach_hang", length = 50)
    private String emailKhachHang;

    @Column(name = "tong_tien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", precision = 18, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;

    @Column(name = "nguoi_sua", length = 50)
    private String nguoiSua;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "ghi_chu", length = 100)
    private String ghiChu;

    // Phương thức tính toán giá trị giảm thực tế của Hóa đơn
    @JsonGetter
    public BigDecimal getGiamGiaThucTeHoaDon() {
        return this.tongTien != null && this.tongTienSauGiam != null
                ? this.tongTien.subtract(this.tongTienSauGiam)
                : BigDecimal.ZERO;
    }

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuHoaDon> lichSuHoaDons;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<HoaDonChiTiet> hoaDonChiTiets;
}