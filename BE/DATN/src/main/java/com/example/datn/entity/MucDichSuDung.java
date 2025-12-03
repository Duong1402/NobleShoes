package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "muc_dich_su_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MucDichSuDung {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã mục đích sử dụng không được để trống")
    @Size(max = 50, message = "Mã mục đích sử dụng không được vượt quá 50 ký tự")
    private String ma;

    @Column(length = 200, nullable = false)
    @NotBlank(message = "Tên mục đích sử dụng không được để trống")
    @Size(max = 200, message = "Tên mục đích sử dụng không được vượt quá 200 ký tự")
    private String ten;
}
