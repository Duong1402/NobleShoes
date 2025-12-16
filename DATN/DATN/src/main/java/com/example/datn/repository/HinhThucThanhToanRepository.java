package com.example.datn.repository;

import com.example.datn.entity.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, UUID> {

    // 🔹 Nếu cần tìm theo id hóa đơn
    List<HinhThucThanhToan> findByHoaDon_Id(UUID idHoaDon);
}
