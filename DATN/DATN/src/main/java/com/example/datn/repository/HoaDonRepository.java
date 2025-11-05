package com.example.datn.repository;

import com.example.datn.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, UUID>,
        JpaSpecificationExecutor<HoaDon> {

    @Query(value = """
                SELECT CONCAT('HD', RIGHT(CONCAT('00',
                    CAST(ISNULL(MAX(CAST(SUBSTRING(ma, 3, LEN(ma)) AS INT)), 0) + 1 AS VARCHAR)
                ), 2))
                FROM hoa_don
            """, nativeQuery = true)
    String getNextMaHoaDon();
}