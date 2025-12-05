package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KhachHangRepository extends JpaRepository<KhachHang, UUID> {

    // ===== FIND =====
    Optional<KhachHang> findByTaiKhoan(String taiKhoan);
    Optional<KhachHang> findByEmail(String email);

    // ===== EXISTS (để báo 409 trùng dữ liệu) =====
    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoanIgnoreCase(String taiKhoan);

    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsBySdt(String sdt);

    // ✅ thêm để tránh lỗi UNIQUE ma = NULL và để generateMaKhachHang() check trùng
    boolean existsByMa(String ma);
}
