package com.example.datn.repository;

import com.example.datn.dto.ChiTietSanPhamDTO;
import com.example.datn.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, UUID> {

    @Query("SELECT new com.example.datn.dto.ChiTietSanPhamDTO(" +
            "ct.id, sp.ma, sp.ten, " +
            "d.ten, t.ten, x.ten, dg.ten, dy.ten, md.ten, " +
            "ct.giaBan, ct.moTa, " +
            "m.ten, k.ten, cl.ten, " +
            "h.urlAnh1, h.urlAnh2, h.urlAnh3) " +
            "FROM ChiTietSanPham ct " +
            "JOIN ct.sanPham sp " +
            "LEFT JOIN sp.danhMuc d " +
            "LEFT JOIN sp.thuongHieu t " +
            "LEFT JOIN sp.xuatXu x " +
            "LEFT JOIN sp.deGiay dg " +
            "LEFT JOIN sp.dayGiay dy " +
            "LEFT JOIN sp.mucDichSuDung md " +
            "LEFT JOIN ct.mauSac m " +
            "LEFT JOIN ct.kichThuoc k " +
            "LEFT JOIN ct.chatLieu cl " +
            "LEFT JOIN sp.hinhAnh h " +
            "WHERE sp.id = :sanPhamId")
    List<ChiTietSanPhamDTO> findChiTietSanPhamDTOBySanPhamId(@Param("sanPhamId") UUID sanPhamId);

    @Query("SELECT COUNT(ct) FROM ChiTietSanPham ct WHERE ct.sanPham.id = :sanPhamId")
    int countBySanPhamId(UUID sanPhamId);
}
