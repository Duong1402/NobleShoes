package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "ma", length = 50, unique = true)
    private String ma;

    @Column(name = "ho_ten", length = 200)
    private String hoTen;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email", length = 200, unique = true)
    private String email;

    @Column(name = "url_anh", length = 100)
    private String urlAnh;


    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;


    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "dia_chi", length = 100)
    private String diaChi;

    @Column(name = "tai_khoan", length = 100, unique = true)
    private String taiKhoan;

    @Column(name = "mat_khau", length = 200)
    private String matKhau;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @Column(name = "nguoi_tao", length = 20)
    private String nguoiTao;

    @Column(name = "nguoi_sua", length = 20)
    private String nguoiSua;

    // TINYINT -> Integer để dễ mở rộng (0/1)
    @Column(name = "trang_thai")
    private Integer trangThai;
}