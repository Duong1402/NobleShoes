package com.example.datn.repository;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.entity.PhieuGiamGiaCaNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, UUID> {

    @Query("SELECT p FROM PhieuGiamGiaCaNhan p " +
            "WHERE p.khachHang = :khachHang AND p.phieuGiamGia = :phieuGiamGia")
    Optional<PhieuGiamGiaCaNhan> findByKhachHangAndPhieuGiamGia(
            @Param("khachHang") KhachHang khachHang,
            @Param("phieuGiamGia") PhieuGiamGia phieuGiamGia
    );
}
