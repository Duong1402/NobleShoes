package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "binh_luan")
public class BinhLuan {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_khach")
    private KhachHang khachHang;

    @ManyToOne @JoinColumn(name="id_sp")
    private SanPham sanPham;

    @Column(name="noi_dung", length=1000)
    private String noiDung;

    @Column(name="ngay")
    private Date ngay;
}
