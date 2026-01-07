package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "nhap_kho")
public class NhapKho {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @ManyToOne @JoinColumn(name="id_nv")
    private NhanVien nhanVien;

    @Column(name="ngay_nhap")
    private Date ngayNhap;
}
