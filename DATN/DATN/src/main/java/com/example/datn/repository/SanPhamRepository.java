package com.example.datn.repository;

import com.example.datn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, UUID> {

    Optional<SanPham> findTopByOrderByMaDesc();

    boolean existsByTen(String ten);

    // --- Lấy danh sách sản phẩm kèm số lượng chi tiết (native query)
    @Query(value = """
            SELECT 
                sp.id AS id,
                sp.ma AS ma,
                sp.ten AS ten,
                sp.ngay_tao AS ngayTao,
                sp.trang_thai AS trangThai,
                th.ten AS thuongHieu,
                dm.ten AS danhMuc,
                xx.ten AS xuatXu,
                COUNT(ctsp.id) AS soLuongChiTiet
            FROM san_pham sp
            LEFT JOIN chi_tiet_san_pham ctsp ON sp.id = ctsp.id_san_pham
            LEFT JOIN thuong_hieu th ON sp.id_thuong_hieu = th.id
            LEFT JOIN danh_muc dm ON sp.id_danh_muc = dm.id
            LEFT JOIN xuat_xu xx ON sp.id_xuat_xu = xx.id
            GROUP BY 
                sp.id, sp.ma, sp.ten, sp.ngay_tao, sp.trang_thai,
                th.ten, dm.ten, xx.ten
            ORDER BY sp.ngay_tao DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getDanhSachSanPhamVaChiTiet();

    // --- Tìm theo giá bán ---
    @Query("""
                SELECT sp FROM SanPham sp 
                JOIN sp.chiTietSanPhams ct 
                WHERE ct.giaBan <= :gia
            """)
    List<SanPham> findByChiTietSanPhamsGiaBanLessThanEqual(BigDecimal gia);

    // --- Lấy 5 sản phẩm ngẫu nhiên ---
    @Query(value = "SELECT TOP 5 * FROM san_pham ORDER BY NEWID()", nativeQuery = true)
    List<SanPham> findTop5Random();

    // --- Tìm kiếm đa trường ---
    @Query("""
                SELECT DISTINCT sp FROM SanPham sp
                JOIN sp.chiTietSanPhams ct
                LEFT JOIN sp.thuongHieu th
                LEFT JOIN sp.danhMuc dm
                LEFT JOIN ct.mauSac ms
                LEFT JOIN ct.kichThuoc kt
                LEFT JOIN ct.chatLieu cl
                WHERE LOWER(sp.ten) LIKE %:keyword% 
                   OR LOWER(th.ten) LIKE %:keyword%
                   OR LOWER(dm.ten) LIKE %:keyword%
                   OR LOWER(ms.ten) LIKE %:keyword%
                   OR LOWER(kt.ten) LIKE %:keyword%
                   OR LOWER(cl.ten) LIKE %:keyword%
            """)
    List<SanPham> searchByKeyword(@Param("keyword") String keyword);

}
