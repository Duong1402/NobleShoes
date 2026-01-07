package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "hinh_anh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HinhAnh {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã hình ảnh không được để trống")
    @Size(max = 50, message = "Mã hình ảnh không được vượt quá 50 ký tự")
    private String ma;

    @Column(name = "url_anh1")
    private String urlAnh1;

    @Column(name = "url_anh2")
    private String urlAnh2;

    @Column(name = "url_anh3")
    private String urlAnh3;

}
