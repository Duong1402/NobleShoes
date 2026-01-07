package com.example.datn.repository;

import com.example.datn.entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DanhGiaRepository extends JpaRepository<DanhGia, UUID> {}
