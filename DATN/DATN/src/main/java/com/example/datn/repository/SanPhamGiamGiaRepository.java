package com.example.datn.repository;

import com.example.datn.entity.SanPhamGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SanPhamGiamGiaRepository extends JpaRepository<SanPhamGiamGia, UUID> {}
