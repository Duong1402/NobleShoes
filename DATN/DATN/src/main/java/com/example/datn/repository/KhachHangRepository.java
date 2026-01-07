package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KhachHangRepository extends JpaRepository<KhachHang, UUID> {

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE kh.sdt LIKE CONCAT('%', :keyword, '%') " +
            "   OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<KhachHang> timDanhSachKhachHang(@Param("keyword") String keyword);

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE kh.sdt = :keyword " +
            "   OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Optional<KhachHang> timTheoTenHoacSdt(@Param("keyword") String keyword);


    Optional<KhachHang> findByTaiKhoan(String taiKhoan);
    Optional<KhachHang> findByEmail(String email);
    Optional<KhachHang> findBySdt(String sdt);
    Optional<KhachHang> findByMaIgnoreCase(String ma);

    Optional<KhachHang> findTopByOrderByMaDesc();


    boolean existsByMa(String ma);

    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByTaiKhoanIgnoreCase(String taiKhoan);

    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    boolean existsBySdt(String sdt);


    @Query(value = "SELECT TOP 1 kh.ma FROM khach_hang kh " +
            "WHERE kh.ma LIKE 'KH%' ORDER BY kh.ma DESC",
            nativeQuery = true)
    String findMaxMaKhachHang();
}