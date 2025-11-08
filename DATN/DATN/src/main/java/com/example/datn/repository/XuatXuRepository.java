package com.example.datn.repository;

import com.example.datn.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface XuatXuRepository extends JpaRepository<XuatXu, UUID> {
    Optional<XuatXu> findTopByOrderByMaDesc();
}