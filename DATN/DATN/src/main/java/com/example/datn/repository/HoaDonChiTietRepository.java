// src/main/java/com/example/datn/repository/HoaDonChiTietRepository.java
package com.example.datn.repository;

import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, UUID> {

    // Lấy tất cả chi tiết theo ID hóa đơn (map vào field hoaDon.id)
    List<HoaDonChiTiet> findAllByHoaDon_Id(UUID hoaDonId);

    // (nếu cần sau này)
    // List<HoaDonChiTiet> findAllByChiTietSanPham_Id(UUID ctspId);
}
