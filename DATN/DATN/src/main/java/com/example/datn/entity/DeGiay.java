package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "de_giay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeGiay {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã đế giày không được để trống")
    @Size(max = 50, message = "Mã đế giày không được vượt quá 50 ký tự")
    private String ma;


    @Column(length = 200, nullable = false)
    @NotBlank(message = "Tên đế giày không được để trống")
    @Size(max = 200, message = "Tên đế giày không được vượt quá 200 ký tự")
    private String ten;
}
