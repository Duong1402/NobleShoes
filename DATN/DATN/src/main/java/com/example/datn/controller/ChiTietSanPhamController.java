package com.example.datn.controller;

import com.example.datn.dto.ChiTietSanPhamDTO;
import com.example.datn.dto.ChiTietSanPhamUpdateDTO;
import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.service.ChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",  // FE admin
        "http://localhost:5176"   // FE client
})
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    /* ============ PUBLIC API CHO CLIENT ============ */

    /**
     * Lấy danh sách chi tiết sản phẩm theo ID sản phẩm cho FE client.
     * GET: http://localhost:8080/api/public/chi-tiet-san-pham/san-pham/{sanPhamId}
     */
    @GetMapping("/api/public/chi-tiet-san-pham/san-pham/{sanPhamId}")
    public ResponseEntity<List<ChiTietSanPhamDTO>> getChiTietSanPhamBySanPhamIdPublic(
            @PathVariable("sanPhamId") UUID sanPhamId
    ) {
        // 🔴 SAI: chiTietSanPhamService.getChiTietSanPhamPublicBySanPhamId(...)
        // ✅ ĐÚNG: gọi đúng tên hàm có trong service
        List<ChiTietSanPhamDTO> list =
                chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
        return ResponseEntity.ok(list);
    }

    /* ============ API ADMIN (GIỮ PATH CŨ) ============ */

    /**
     * Lấy tất cả chi tiết sản phẩm cho admin.
     * GET: http://localhost:8080/admin/chi-tiet-san-pham
     */
    @GetMapping("/admin/chi-tiet-san-pham")
    public ResponseEntity<List<ChiTietSanPham>> getAllChiTietSanPham() {
        List<ChiTietSanPham> list = chiTietSanPhamService.getAllChiTietSanPham();
        return ResponseEntity.ok(list);
    }

    /**
     * Lấy danh sách chi tiết theo ID sản phẩm cho admin.
     * GET: http://localhost:8080/admin/chi-tiet-san-pham/san-pham/{sanPhamId}
     */
    @GetMapping("/admin/chi-tiet-san-pham/san-pham/{sanPhamId}")
    public ResponseEntity<List<ChiTietSanPhamDTO>> getChiTietSanPhamBySanPhamIdAdmin(
            @PathVariable("sanPhamId") UUID sanPhamId
    ) {
        List<ChiTietSanPhamDTO> list =
                chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
        return ResponseEntity.ok(list);
    }

    /**
     * Cập nhật chi tiết sản phẩm (admin).
     * PUT: http://localhost:8080/admin/chi-tiet-san-pham/{id}
     */
    @PutMapping("/admin/chi-tiet-san-pham/{id}")
    public ResponseEntity<?> updateChiTietSanPham(
            @PathVariable("id") UUID id,
            @RequestBody ChiTietSanPhamUpdateDTO dto
    ) {
        try {
            ChiTietSanPham updated = chiTietSanPhamService.updateChiTietSanPham(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
