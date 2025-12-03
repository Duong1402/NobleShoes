package com.example.datn.repository;

import com.example.datn.entity.MucDichSuDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MucDichSuDungRepository extends JpaRepository<MucDichSuDung, UUID> {
    Optional<MucDichSuDung> findTopByOrderByMaDesc();
}
