package com.example.datn.repository;

import com.example.datn.entity.NhapKhoChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NhapKhoChiTietRepository extends JpaRepository<NhapKhoChiTiet, UUID> {}
