package com.example.datn.repository;

import com.example.datn.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, UUID> {
}
