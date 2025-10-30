package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "ma", unique = true)
    private String ma;

    @NotNull(message = "Không được để trống")
    @Column(name = "ten")
    private String ten;

    @NotNull(message = "Không được để trống")
    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @NotNull(message = "Không được để trống")
    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @NotNull(message = "Không được để trống")
    @Column(name = "hinh_thuc_giam_gia")
    private Boolean hinhThucGiamGia; // 0: tien, 1: %

    @NotNull(message = "Không được để trống")
    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @NotNull(message = "Không được để trống")
    @Column(name = "gia_tri_giam_toi_thieu")
    private BigDecimal giaTriGiamToiThieu;

    @NotNull(message = "Không được để trống")
    @Column(name = "gia_tri_giam_toi_da")
    private BigDecimal giaTriGiamToiDa;

    @NotNull(message = "Không được để trống")
    @Column(name = "trang_thai")
    private Boolean trangThai; //0: Ngung hoat dong, 1: Dang hoat dong

    @Column(name = "mo_ta")
    private String moTa;
}
