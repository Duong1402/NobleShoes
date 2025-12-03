package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSac {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã màu sắc không được để trống")
    @Size(max = 50, message = "Mã màu sắc không được vượt quá 50 ký tự")
    private String ma;

    @Column(columnDefinition = "nvarchar(200)", nullable = false)
    @NotBlank(message = "Tên màu sắc không được để trống")
    @Size(max = 200, message = "Tên màu sắc không được vượt quá 200 ký tự")
    private String ten;
}
