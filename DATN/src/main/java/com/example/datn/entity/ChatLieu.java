package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "chat_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieu {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, length = 50)
    @NotBlank(message = "Mã chất liệu không được để trống")
    @Size(max = 50, message = "Mã chất liệu không được vượt quá 50 ký tự")
    private String ma;

    @Column(columnDefinition = "nvarchar(200)", nullable = false)
    @NotBlank(message = "Tên chất liệu không được để trống")
    @Size(max = 200, message = "Tên chất liệu không được vượt quá 200 ký tự")
    private String ten;
}
