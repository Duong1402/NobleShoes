package com.example.datn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
public class HinhAnh {
    @Id
    private UUID id;
    @Column(name = "url_anh1")
    private String url1;
}
