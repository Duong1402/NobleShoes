package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "thuong_hieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieu {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã thương hiệu không được để trống")
    @Size(max = 50, message = "Mã thương hiệu không được vượt quá 50 ký tự")
    private String ma;

    @Column(length = 200, nullable = false)
    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 200, message = "Tên thương hiệu không được vượt quá 200 ký tự")
    private String ten;
}
