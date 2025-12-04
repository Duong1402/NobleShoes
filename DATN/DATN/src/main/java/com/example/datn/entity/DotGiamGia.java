package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dot_giam_gia")
public class DotGiamGia {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "ma", unique = true)
    private String ma;

    @NotBlank(message = "Không được để trống")
    @Column(name = "ten")
    private String ten;

    @NotNull(message = "Không được để trống")
    @Column(name = "gia_tri_giam")
    private Integer giaTriGiam;

    @NotNull(message = "Không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    @Column(name = "so_tien_giam_toi_da")
    private BigDecimal soTienGiamToiDa;

    @NotNull(message = "Không được để trống")
    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @NotNull(message = "Không được để trống")
    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @Column(name = "trang_thai")
    private Integer trangThai;
}