package com.example.datn.repository;

import com.example.datn.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, UUID> {

    // an toàn: tìm theo ma không phân biệt hoa thường
    @Query("select p from PhieuGiamGia p where upper(p.ma) = upper(:ma)")
    Optional<PhieuGiamGia> findByMaIgnoreCase(@Param("ma") String ma);
}
