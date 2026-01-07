package com.example.datn.repository;

import com.example.datn.entity.LienHe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LienHeRepository extends JpaRepository<LienHe, UUID> {}
