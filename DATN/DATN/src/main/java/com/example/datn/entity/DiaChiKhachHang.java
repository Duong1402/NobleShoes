package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "dia_chi_khach_hang")
public class DiaChiKhachHang {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_khach_hang", nullable=false)
    private KhachHang khachHang;

    @Column(name="ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name="sdt")
    private String sdt;

    @Column(name="dia_chi")
    private String diaChi;

    @Column(name="mac_dinh")
    private Boolean macDinh;
}
