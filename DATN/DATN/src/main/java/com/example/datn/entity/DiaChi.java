package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "ma", unique = true, length = 50)
    private String ma;

    @Column(name = "thanh_pho", length = 30)
    private String thanhPho;

    @Column(name = "huyen", length = 20)
    private String huyen;

    @Column(name = "xa", length = 20)
    private String xa;

    @Column(name = "dia_chi_cu_the", length = 50)
    private String diaChiCuThe;

    @Column(name = "mac_dinh")
    private Boolean macDinh = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    @JsonBackReference
    private KhachHang khachHang;

}
