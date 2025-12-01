package com.example.datn.repository;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID>,
        JpaSpecificationExecutor<HoaDon> {

    @Query(value = """
                SELECT CONCAT('HD', RIGHT(CONCAT('00',
                    CAST(ISNULL(MAX(CAST(SUBSTRING(ma, 3, LEN(ma)) AS INT)), 0) + 1 AS VARCHAR)
                ), 2))
                FROM hoa_don
            """, nativeQuery = true)
    String getNextMaHoaDon();

    @Modifying
    @Transactional
    @Query("UPDATE HoaDon h SET h.tongTien = :tongTien, h.tongTienSauGiam = :tongTienSauGiam, h.phieuGiamGia = :phieuGiamGia WHERE h.id = :id")
    void updateTienVaKhuyenMai(@Param("id") UUID id,
                               @Param("tongTien") BigDecimal tongTien,
                               @Param("tongTienSauGiam") BigDecimal tongTienSauGiam,
                               @Param("phieuGiamGia") PhieuGiamGia phieuGiamGia);
}