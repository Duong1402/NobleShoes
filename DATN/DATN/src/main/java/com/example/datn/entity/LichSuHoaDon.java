package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lich_su_hoa_don")
@Getter
@Setter
public class LichSuHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Hoặc GenerationType.AUTO
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon hoaDon;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_thuc_hien")
    private String nguoiThucHien;

    @Column(name = "ghi_chu")
    private String ghiChu;
}