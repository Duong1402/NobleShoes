package com.example.datn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Entity
@Table(name = "mau_sac")
@Getter
@Setter
public class MauSac {
    @Id
    private UUID id;
    private String ten;
}
