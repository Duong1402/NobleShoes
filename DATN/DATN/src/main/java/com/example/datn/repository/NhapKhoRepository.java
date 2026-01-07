package com.example.datn.repository;

import com.example.datn.entity.NhapKho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NhapKhoRepository extends JpaRepository<NhapKho, UUID> {}
