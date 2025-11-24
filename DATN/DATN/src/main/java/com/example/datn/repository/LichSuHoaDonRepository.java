package com.example.datn.repository;

import com.example.datn.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, UUID> {

    List<LichSuHoaDon> findAllByHoaDonIdOrderByThoiGianAsc(UUID hoaDonId);
}
