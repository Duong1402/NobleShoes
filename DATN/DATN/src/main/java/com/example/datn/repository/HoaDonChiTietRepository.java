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


    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id = :hoaDonId")
    List<HoaDonChiTiet> findAllByHoaDonId(UUID hoaDonId);

    @Query("SELECT SUM(hdct.soLuong) FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id IN :hoaDonIds")
    Long sumSoLuongByHoaDonIdIn(@Param("hoaDonIds") List<UUID> hoaDonIds);

    @Query("SELECT new com.example.datn.dto.thongke.SanPhamBanChayDto(" +
            "COALESCE(sp.hinhAnh.urlAnh1, ''), sp.ten, SUM(hdct.soLuong), hdct.donGia) " +
            "FROM HoaDonChiTiet hdct " +
            "JOIN hdct.chiTietSanPham ctsp " +
            "JOIN ctsp.sanPham sp " +
            "JOIN hdct.hoaDon hd " +
            "WHERE hd.ngayTao BETWEEN :start AND :end AND hd.trangThai = 6 " + // Chỉ tính đơn HOÀN THÀNH
            "GROUP BY sp.hinhAnh.urlAnh1, sp.ten, hdct.donGia " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    Page<SanPhamBanChayDto> findBestSellingProducts(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(hdct.thanhTien), 0) FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id = :idHoaDon AND hdct.trangThai <> 0")
    BigDecimal tongTienHoaDon(@Param("idHoaDon") UUID idHoaDon);

    // 1. Tìm chính xác sản phẩm có giá đó chưa
    @Query("SELECT hdct FROM HoaDonChiTiet hdct " +
            "WHERE hdct.hoaDon.id = :idHoaDon " +
            "AND hdct.chiTietSanPham.id = :idSp " +
            "AND hdct.donGia = :donGia")
    Optional<HoaDonChiTiet> findByIdAndDonGia(@Param("idHoaDon") UUID idHoaDon,
                                              @Param("idSp") UUID idSp,
                                              @Param("donGia") BigDecimal donGia);

    // 2. Tìm danh sách các sản phẩm cùng ID (để check xem có giá cũ không)
    List<HoaDonChiTiet> findByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

    List<HoaDonChiTiet> findByHoaDonId(UUID idHoaDon);

    List<HoaDonChiTiet> findAllByHoaDon_Id(UUID hoaDonId);

    void deleteByHoaDonIdAndChiTietSanPhamId(UUID idHoaDon, UUID idChiTietSanPham);

}