package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "nhap_kho_chi_tiet")
public class NhapKhoChiTiet {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_nhap")
    private NhapKho nhapKho;

    @ManyToOne @JoinColumn(name="id_ctsp")
    private ChiTietSanPham chiTietSanPham;

    @Column(name="so_luong")
    private Integer soLuong;
}
