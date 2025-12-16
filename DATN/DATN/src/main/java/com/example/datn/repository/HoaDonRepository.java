package com.example.datn.repository;

import com.example.datn.dto.thongke.TrangThaiDonHangDto;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID>,
        JpaSpecificationExecutor<HoaDon> {

    Optional<HoaDon> findByMa(String ma);

    List<HoaDon> findByNgayTaoBetween(LocalDate start, LocalDate end);

    List<HoaDon> findAllByEmailKhachHangIgnoreCaseOrderByNgayTaoDesc(String emailKhachHang);

    @Query(value = """
                SELECT CONCAT('HD', RIGHT(CONCAT('0',
                    CAST(ISNULL(MAX(CAST(SUBSTRING(ma, 3, LEN(ma)) AS BIGINT)), 0) + 1 AS VARCHAR)
                ), 10))
                FROM hoa_don
                WHERE ma LIKE 'HD%'
            """, nativeQuery = true)
    String getNextMaHoaDon();

    @Modifying
    @Transactional
    @Query("UPDATE HoaDon h SET h.tongTien = :tongTien, h.tongTienSauGiam = :tongTienSauGiam, h.phieuGiamGia = :phieuGiamGia WHERE h.id = :id")
    void updateTienVaKhuyenMai(@Param("id") UUID id,
                               @Param("tongTien") BigDecimal tongTien,
                               @Param("tongTienSauGiam") BigDecimal tongTienSauGiam,
                               @Param("phieuGiamGia") PhieuGiamGia phieuGiamGia);

    @Query("SELECT new com.example.datn.dto.thongke.TrangThaiDonHangDto(hd.trangThai, COUNT(hd.id)) " +
            "FROM HoaDon hd " +
            "WHERE hd.ngayTao BETWEEN :start AND :end " +
            "GROUP BY hd.trangThai")
    List<TrangThaiDonHangDto> findTrangThaiDonHangStats(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT h FROM HoaDon h LEFT JOIN FETCH h.phieuGiamGia WHERE h.id = :id")
    Optional<HoaDon> findByIdWithPhieuGiamGia(UUID id);
}