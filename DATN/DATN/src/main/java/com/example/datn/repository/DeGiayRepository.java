package com.example.datn.repository;

import com.example.datn.entity.DeGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeGiayRepository extends JpaRepository<DeGiay, UUID> {
    Optional<DeGiay> findTopByOrderByMaDesc();
}
