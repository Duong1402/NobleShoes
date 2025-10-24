package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "xuat_xu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuatXu {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã xuất xứ không được để trống")
    @Size(max = 50, message = "Mã xuất xứ không được vượt quá 50 ký tự")
    private String ma;

    @Column(columnDefinition = "nvarchar(200)", nullable = false)
    @NotBlank(message = "Tên xuất xứ không được để trống")
    @Size(max = 200, message = "Tên xuất xứ không được vượt quá 200 ký tự")
    private String ten;
}
