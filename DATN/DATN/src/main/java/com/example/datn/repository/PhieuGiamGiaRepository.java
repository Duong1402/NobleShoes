package com.example.datn.repository;

import com.example.datn.entity.PhieuGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, UUID> {

    // (không bắt buộc, JpaRepository đã có) nhưng giữ lại cho code đang gọi
    Page<PhieuGiamGia> findAll(Pageable pageable);

    // ✅ tìm theo mã không phân biệt hoa thường
    @Query("select p from PhieuGiamGia p where upper(p.ma) = upper(:ma)")
    Optional<PhieuGiamGia> findByMaIgnoreCase(@Param("ma") String ma);

    // ✅ coupon còn hiệu lực (native)
    @Query(value = """
            SELECT p.* FROM phieu_giam_gia p
            WHERE CAST(p.ngay_bat_dau AS DATE) <= CAST(GETDATE() AS DATE)
              AND CAST(p.ngay_ket_thuc AS DATE) >= CAST(GETDATE() AS DATE)
              AND p.trang_thai = 1
              AND (
                    1 = 1
                    OR EXISTS (
                        SELECT 1 FROM phieu_giam_gia_ca_nhan pcn
                        WHERE pcn.id_phieu_giam_gia = p.id
                          AND pcn.id_khach_hang = :customerId
                          AND pcn.trang_thai = 1
                    )
              )
            """, nativeQuery = true)
    List<PhieuGiamGia> findValidCouponsForCustomer(@Param("customerId") UUID customerId);
}
