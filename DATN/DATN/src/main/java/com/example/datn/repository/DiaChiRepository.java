package com.example.datn.repository;

import com.example.datn.entity.DiaChi;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaChiRepository extends JpaRepository<DiaChi, UUID> {

    List<DiaChi> findAllByKhachHangId(UUID idKhachHang);

    @Query("SELECT MAX(d.ma) FROM DiaChi d WHERE d.ma LIKE 'DC%'")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    String findMaxMa();

    @Modifying
    @Query("UPDATE DiaChi dc SET dc.macDinh = FALSE " +
            "WHERE dc.khachHang.id = :khachHangId " +
            "AND (:diaChiId IS NULL OR dc.id != :diaChiId)")
    void updateAllDefaultsToFalse(@Param("khachHangId") UUID khachHangId, @Param("diaChiId") UUID diaChiId);


    // Tùy chọn: Tìm địa chỉ mặc định
    Optional<DiaChi> findByKhachHangIdAndMacDinhTrue(UUID khachHangId);
}
