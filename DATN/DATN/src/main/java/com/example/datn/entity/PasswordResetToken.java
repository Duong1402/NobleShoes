package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "password_reset_token")
@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "tai_khoan", nullable = false)
    private String taiKhoan;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;
}
