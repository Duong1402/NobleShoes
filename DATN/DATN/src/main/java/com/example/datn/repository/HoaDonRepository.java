package com.example.datn.repository;

import com.example.datn.dto.thongke.TrangThaiDonHangDto;
import com.example.datn.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID>,
        JpaSpecificationExecutor<HoaDon> {
    List<HoaDon> findByNgayTaoBetween(LocalDate start, LocalDate end);

    @Query("SELECT new com.example.datn.dto.thongke.TrangThaiDonHangDto(hd.trangThai, COUNT(hd.id)) " +
            "FROM HoaDon hd " +
            "WHERE hd.ngayTao BETWEEN :start AND :end " +
            "GROUP BY hd.trangThai")
    List<TrangThaiDonHangDto> findTrangThaiDonHangStats(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}