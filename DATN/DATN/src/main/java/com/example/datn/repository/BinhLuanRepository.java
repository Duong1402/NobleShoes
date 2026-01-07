package com.example.datn.repository;

import com.example.datn.entity.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BinhLuanRepository extends JpaRepository<BinhLuan, UUID> {}
