package com.example.datn.repository;

import com.example.datn.entity.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, UUID> {}
