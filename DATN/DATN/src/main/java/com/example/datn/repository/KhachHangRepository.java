package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface KhachHangRepository extends JpaRepository<KhachHang, UUID> {
    Optional<KhachHang> findByHoTenOrSdt(String sdt, String hoTen);

    Optional<KhachHang> findBySdt(String sdt);

    @Query("SELECT kh FROM KhachHang kh " +
            "WHERE kh.sdt = :keyword OR LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Optional<KhachHang> timTheoTenHoacSdt(@Param("keyword") String keyword);

    @Query(value = "SELECT TOP 1 kh.ma FROM khach_hang kh WHERE kh.ma LIKE 'KH%' ORDER BY kh.ma DESC",
            nativeQuery = true)
    String findMaxMaKhachHang();
}