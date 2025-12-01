package com.example.datn.repository;

import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, UUID> {


    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id = :hoaDonId")
    List<HoaDonChiTiet> findAllByHoaDonId(UUID hoaDonId);

    Optional<HoaDonChiTiet> findByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

    List<HoaDonChiTiet> findByHoaDonId(UUID idHoaDon);

    void deleteByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

    @Query("SELECT COALESCE(SUM(hdct.thanhTien), 0) FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id = :idHoaDon AND hdct.trangThai <> 0")
    BigDecimal tongTienHoaDon(@Param("idHoaDon") UUID idHoaDon);

}