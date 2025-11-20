package com.example.datn.repository;

import com.example.datn.dto.thongke.SanPhamBanChayDto;
import com.example.datn.entity.HoaDonChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
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
            "WHERE hd.ngayTao BETWEEN :start AND :end AND hd.trangThai = 5 " + // Chỉ tính đơn HOÀN THÀNH
            "GROUP BY sp.hinhAnh.urlAnh1, sp.ten, hdct.donGia " +
            "ORDER BY SUM(hdct.soLuong) DESC")
    Page<SanPhamBanChayDto> findBestSellingProducts(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            Pageable pageable
    );
}