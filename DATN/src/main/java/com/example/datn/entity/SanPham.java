package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
public class SanPham {

    @Id
    private UUID id;

    private String ten;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hinh_anh")
    private HinhAnh hinhAnh;
}