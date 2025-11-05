package com.example.datn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
public class KhachHang {

    @Id
    private UUID id;

    private String hoTen;
    private String sdt;
}