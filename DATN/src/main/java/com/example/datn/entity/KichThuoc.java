package com.example.datn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
public class KichThuoc {
    @Id
    private UUID id;
    private String ten;
}
