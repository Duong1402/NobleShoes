package com.example.datn.repository;

import com.example.datn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NhanVienRepository extends JpaRepository<NhanVien, UUID> {
    boolean existsByMa(String ma);

    boolean existsByEmail(String email);

    boolean existsByTaiKhoan(String taiKhoan);

    @Query(value = "SELECT * FROM nhan_vien ORDER BY ma DESC", nativeQuery = true)
    List<NhanVien> findAllOrderByMaDesc();

    @Query(value = """
                SELECT CONCAT('NV', RIGHT(CONCAT('00000',
                    CAST(ISNULL(MAX(CAST(SUBSTRING(ma, 3, LEN(ma)) AS INT)), 0) + 1 AS VARCHAR)
                ), 5))
                FROM nhan_vien
            """, nativeQuery = true)
    String getNextMaNhanVien();
}