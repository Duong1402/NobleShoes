package com.example.datn.repository;

import com.example.datn.entity.DanhMuc;
import com.example.datn.entity.DayGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DayGiayRepository extends JpaRepository<DayGiay, UUID> {
    Optional<DayGiay> findTopByOrderByMaDesc();
}
