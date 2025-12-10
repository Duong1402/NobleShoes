package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phieu_giam_gia_ca_nhan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PhieuGiamGiaCaNhan {

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
    @Column(name = "ngay_nhan")
    private Date ngayNhan;

    @NotNull(message = "Không được để trống")
    @Column(name = "ngay_het_han")
    private Date ngayHetHan;

    // 0/1 => Boolean false/true
    @Column(name = "trang_thai")
    private Boolean trangThai;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;
}
