package com.example.datn.repository;

import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


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

}