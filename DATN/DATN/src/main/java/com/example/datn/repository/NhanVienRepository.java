package com.example.datn.repository;

import com.example.datn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NhanVienRepository extends JpaRepository<NhanVien, UUID> {
    boolean existsByMa(String ma);

    boolean existsByEmail(String email);

    boolean existsByTaiKhoan(String taiKhoan);

    @Query(value = "SELECT TOP 1 ma FROM nhan_vien ORDER BY ma DESC", nativeQuery = true)
    String findLatestMa();

    @Query(value = "SELECT * FROM nhan_vien ORDER BY ma DESC", nativeQuery = true)
    List<NhanVien> findAllOrderByMaDesc();

    Optional<NhanVien> findNhanVienByTaiKhoanAndMatKhau(String taiKhoan, String matKhau);

    Optional<NhanVien> findByTaiKhoan(String username);
}