package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "kho")
public class Kho {
    @Id @GeneratedValue
    @Column(name="id", columnDefinition="uniqueidentifier")
    private UUID id;

    @Column(name="ten")
    private String ten;
}
