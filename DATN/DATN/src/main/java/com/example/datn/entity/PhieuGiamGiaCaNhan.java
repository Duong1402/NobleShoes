package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_giam_gia_ca_nhan")
public class PhieuGiamGiaCaNhan {
    @Id
    @GeneratedValue()
    @Column(name = "id")
    private UUID id;

    @Column(name = "ma", unique = true)
    private String ma;

    @NotBlank(message = "Không được để trống")
    @Column(name = "ten")
    private String ten;

    @NotBlank(message = "Không được để trống")
    @Column(name = "ngay_nhan")
    private Date ngayNhan;

    @NotBlank(message = "Không được để trống")
    @Column(name = "ngay_het_han")
    private Date ngayHetHan;

    @NotBlank(message = "Không được để trống")
    @Column(name = "trang_thai")
    private Boolean trangThai; //0 la false, 1 la true

    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;
}
