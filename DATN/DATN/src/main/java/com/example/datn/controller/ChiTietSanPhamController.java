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
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:*")
@RequestMapping
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    @GetMapping("/api/public/chi-tiet-san-pham/san-pham/{sanPhamId}")
    public ResponseEntity<List<ChiTietSanPhamDTO>> getChiTietSanPhamBySanPhamIdPublic(
            @PathVariable UUID sanPhamId
    ) {
        List<ChiTietSanPhamDTO> list = chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin/chi-tiet-san-pham")
    public ResponseEntity<List<ChiTietSanPhamResponse>> getAllChiTietSanPham() {
        List<ChiTietSanPhamResponse> list = chiTietSanPhamService.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/admin/chi-tiet-san-pham/san-pham/{sanPhamId}")
    public ResponseEntity<List<ChiTietSanPhamDTO>> getChiTietSanPhamBySanPhamIdAdmin(
            @PathVariable UUID sanPhamId
    ) {
        List<ChiTietSanPhamDTO> list = chiTietSanPhamService.getChiTietSanPhamBySanPhamId(sanPhamId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/admin/chi-tiet-san-pham/{id}")
    public ResponseEntity<?> updateChiTietSanPham(
            @PathVariable UUID id,
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

    @PutMapping("/admin/chi-tiet-san-pham/inline/{ctspId}")
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
