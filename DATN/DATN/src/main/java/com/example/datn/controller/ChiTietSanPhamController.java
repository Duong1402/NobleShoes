package com.example.datn.controller;

import com.example.datn.dto.ChiTietSanPhamDTO;
import com.example.datn.dto.ChiTietSanPhamResponse;
import com.example.datn.dto.ChiTietSanPhamUpdateDTO;
import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.service.ChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/chi-tiet-san-pham")
@RequiredArgsConstructor
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    // 1️⃣ Lấy tất cả chi tiết sản phẩm
    @GetMapping
    public ResponseEntity<List<ChiTietSanPhamResponse>> getAllChiTietSanPham() {
        List<ChiTietSanPhamResponse> list = chiTietSanPhamService.getAll();
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

    // ✅ Inline update endpoint
    @PutMapping("/inline/{ctspId}")
    public ResponseEntity<?> updateInline(
            @PathVariable UUID ctspId,
            @RequestBody Map<String, Object> payload
    ) {
        try {
            BigDecimal giaBan = payload.get("giaBan") != null
                    ? new BigDecimal(payload.get("giaBan").toString())
                    : null;
            Integer soLuongTon = payload.get("soLuongTon") != null
                    ? Integer.parseInt(payload.get("soLuongTon").toString())
                    : null;

            chiTietSanPhamService.updateGiaBanVaSoLuong(ctspId, giaBan, soLuongTon);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
