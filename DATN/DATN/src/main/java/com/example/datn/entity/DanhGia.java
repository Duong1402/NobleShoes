package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "danh_gia")
public class DanhGia {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_khach")
    private KhachHang khachHang;

    @ManyToOne @JoinColumn(name="id_sp")
    private SanPham sanPham;

    @Column(name="sao")
    private Integer sao;

    @Column(name="noi_dung", length=1000)
    private String noiDung;
}
