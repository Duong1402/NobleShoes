package com.example.datn.repository;

import com.example.datn.entity.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, UUID> {}
