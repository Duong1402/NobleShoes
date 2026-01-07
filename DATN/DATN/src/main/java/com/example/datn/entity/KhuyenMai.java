package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "khuyen_mai")
public class KhuyenMai {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @Column(name="ma", unique=true)
    private String ma;

    @Column(name="ten")
    private String ten;

    @Column(name="hinh_thuc")
    private Integer hinhThuc; // 0: tiền, 1: %

    @Column(name="gia_tri", precision=18, scale=2)
    private BigDecimal giaTri;
}
