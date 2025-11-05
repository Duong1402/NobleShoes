package com.example.datn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
public class NhanVien {

    @Id
    private UUID id;
    @Column(name = "ho_ten")
    private String hoTen;
}