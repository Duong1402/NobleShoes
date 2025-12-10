package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KhachHangRepository extends JpaRepository<KhachHang, UUID> {

    // ===== SEARCH (admin) =====
    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE kh.sdt LIKE CONCAT('%', :keyword, '%') " +
            "   OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KhachHang> timDanhSachKhachHang(@Param("keyword") String keyword);

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE kh.sdt = :keyword " +
            "   OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Optional<KhachHang> timTheoTenHoacSdt(@Param("keyword") String keyword);

    // ===== FIND =====
    Optional<KhachHang> findByTaiKhoan(String taiKhoan);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySdt(String sdt);

    // top by ma (đang dùng ở vài chỗ)
    Optional<KhachHang> findTopByOrderByMaDesc();

    // ===== EXISTS (để báo 409 trùng dữ liệu) =====
    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoanIgnoreCase(String taiKhoan);

    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsBySdt(String sdt);

    // ✅ dùng cho generate mã tránh UNIQUE ma = NULL / check trùng
    boolean existsByMa(String ma);

    // ===== GEN MÃ =====
    @Query(value = "SELECT TOP 1 kh.ma FROM khach_hang kh " +
            "WHERE kh.ma LIKE 'KH%' ORDER BY kh.ma DESC",
            nativeQuery = true)
    String findMaxMaKhachHang();
}
