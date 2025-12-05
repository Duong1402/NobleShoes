// src/main/java/com/example/datn/repository/HoaDonRepository.java
package com.example.datn.repository;

import com.example.datn.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HoaDonRepository
        extends JpaRepository<HoaDon, UUID>, JpaSpecificationExecutor<HoaDon> {

    Optional<HoaDon> findByMa(String ma);

    // ✅ THÊM DÒNG NÀY (để dùng trong OrderMeController / HoaDonService)
    List<HoaDon> findAllByEmailKhachHangIgnoreCaseOrderByNgayTaoDesc(String emailKhachHang);
}
