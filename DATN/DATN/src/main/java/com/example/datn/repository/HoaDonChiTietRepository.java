package com.example.datn.repository;

import com.example.datn.dto.thongke.SanPhamBanChayDto;
import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, UUID> {

    // ===== LẤY CHI TIẾT THEO HÓA ĐƠN =====

    // ✅ Cách 1 (Derived query chuẩn): field hoaDon.id
    List<HoaDonChiTiet> findAllByHoaDon_Id(UUID hoaDonId);

    // ✅ Cách 2 (để tương thích code cũ nếu đang gọi)
    List<HoaDonChiTiet> findByHoaDonId(UUID idHoaDon);

    // ✅ Query explicit (tương thích)
    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id = :hoaDonId")
    List<HoaDonChiTiet> findAllByHoaDonId(@Param("hoaDonId") UUID hoaDonId);

    // ===== TÍNH TOÁN / THỐNG KÊ =====

    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id IN :hoaDonIds")
    Long sumSoLuongByHoaDonIdIn(@Param("hoaDonIds") List<UUID> hoaDonIds);

    @Query("SELECT COALESCE(SUM(hdct.thanhTien), 0) FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id = :idHoaDon AND hdct.trangThai <> 0")
    BigDecimal tongTienHoaDon(@Param("idHoaDon") UUID idHoaDon);

    @Query("SELECT new com.example.datn.dto.thongke.SanPhamBanChayDto(" +
            "COALESCE(sp.hinhAnh.urlAnh1, ''), sp.ten, SUM(hdct.soLuong), hdct.donGia) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.chiTietSanPham ctsp " +
            "JOIN ctsp.sanPham sp " +
            "JOIN hdct.hoaDon hd " +
            "WHERE hd.ngayTao BETWEEN :start AND :end AND hd.trangThai = 5 " +
            "GROUP BY sp.hinhAnh.urlAnh1, sp.ten, hdct.donGia " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    Page<SanPhamBanChayDto> findBestSellingProducts(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );

    // ===== CRUD THEO CẶP HÓA ĐƠN - CTSP =====

    void deleteByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

    // Danh sách các dòng cùng 1 ctsp trong cùng hóa đơn (để check giá cũ / tách dòng)
    List<HoaDonChiTiet> findByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

    // ⚠️ Tên method hiện tại hơi gây hiểu nhầm (thực tế là tìm theo hoaDonId + ctspId + donGia)
    // Mình giữ nguyên signature để không vỡ chỗ gọi.
    @Query("SELECT hdct FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id = :idHoaDon " +
            "AND hdct.chiTietSanPham.id = :idSp " +
            "AND hdct.donGia = :donGia")
    Optional<HoaDonChiTiet> findByIdAndDonGia(
            @Param("idHoaDon") UUID idHoaDon,
            @Param("idSp") UUID idSp,
            @Param("donGia") BigDecimal donGia
    );
}
