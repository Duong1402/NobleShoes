package com.example.datn.repository;

import com.example.datn.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, UUID> {

    Page<PhieuGiamGia> findAll(Pageable pageable);

    @Query("select p from PhieuGiamGia p where upper(p.ma) = upper(:ma)")
    Optional<PhieuGiamGia> findByMaIgnoreCase(@Param("ma") String ma);

    @Query(value = """
                SELECT p.* FROM phieu_giam_gia p
                WHERE 
                CAST(p.ngay_bat_dau AS DATE) <= CAST(GETDATE() AS DATE) 
                
                AND CAST(p.ngay_ket_thuc AS DATE) >= CAST(GETDATE() AS DATE)
                
                AND p.trang_thai = 1
                AND (
                    1 = 1 
                    OR 
                    EXISTS (
                        SELECT 1 FROM phieu_giam_gia_ca_nhan pcn 
                        WHERE pcn.id_phieu_giam_gia = p.id 
                        AND pcn.id_khach_hang = :customerId 
                        AND pcn.trang_thai = 1
                    )
                )
            """, nativeQuery = true)
    List<PhieuGiamGia> findValidCouponsForCustomer(@Param("customerId") UUID customerId);

    @Query("""
            SELECT pgg
            FROM PhieuGiamGia pgg
            WHERE pgg.trangThai = true
              AND (pgg.ngayBatDau IS NULL OR pgg.ngayBatDau <= CURRENT_TIMESTAMP)
              AND (pgg.ngayKetThuc IS NULL OR pgg.ngayKetThuc >= CURRENT_TIMESTAMP)
              AND (:tongTien >= COALESCE(pgg.giaTriGiamToiThieu, 0))
            ORDER BY
              CASE
                WHEN pgg.hinhThucGiamGia = true
                  THEN LEAST(
                    :tongTien * pgg.giaTriGiam / 100,
                    pgg.giaTriGiamToiDa
                  )
                ELSE pgg.giaTriGiam
              END DESC
            """)
    List<PhieuGiamGia> findVoucherTotNhat(@Param("tongTien") BigDecimal tongTien);


}