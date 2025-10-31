package com.example.datn.repository;

import com.example.datn.entity.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, UUID> {
    boolean existsByMa(String ma);

    Optional<ChucVu> findTopByOrderByMaDesc();
}