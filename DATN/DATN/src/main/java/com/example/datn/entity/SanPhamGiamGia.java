package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "san_pham_giam_gia")
public class SanPhamGiamGia {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_ctsp", nullable=false)
    private ChiTietSanPham chiTietSanPham;

    @ManyToOne @JoinColumn(name="id_km", nullable=false)
    private KhuyenMai khuyenMai;
}
