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
@RequestMapping("/admin/chi-tiet-san-pham")
@RequiredArgsConstructor
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    // 1️⃣ Lấy tất cả chi tiết sản phẩm
    @GetMapping
    public ResponseEntity<List<ChiTietSanPham>> getAllChiTietSanPham() {
        List<ChiTietSanPham> list = chiTietSanPhamService.getAllChiTietSanPham();
        return ResponseEntity.ok(list);
    }

    // 2️⃣ Lấy chi tiết sản phẩm theo ID chi tiết
//    @GetMapping("/{id}")
//    public ResponseEntity<ChiTietSanPham> getChiTietSanPhamById(@PathVariable UUID id) {
//        ChiTietSanPham ct = chiTietSanPhamService.getChiTietSanPhamById(id);
//        return ResponseEntity.ok(ct);
//    }

    // 3️⃣ Lấy chi tiết sản phẩm theo ID sản phẩm
    @GetMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<List<ChiTietSanPhamDTO>> getChiTietSanPhamBySanPhamId(@PathVariable UUID sanPhamId) {
        List<ChiTietSanPhamDTO> list = chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
        return ResponseEntity.ok(list);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateChiTietSanPham(
            @PathVariable UUID id,
            @RequestBody ChiTietSanPhamUpdateDTO dto
    ) {
        try {
            ChiTietSanPham updated = chiTietSanPhamService.updateChiTietSanPham(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace(); // log chi tiết lỗi ra console
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }



}
