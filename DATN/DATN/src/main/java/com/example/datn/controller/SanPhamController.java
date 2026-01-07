package com.example.datn.controller;

import com.example.datn.dto.SanPhamRequest;
import com.example.datn.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/san-pham")
@CrossOrigin(originPatterns = "http://localhost:*")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    // POST: /admin/san-pham
    @PostMapping
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamRequest request) {
        try {
            sanPhamService.saveSanPham(request);
            return ResponseEntity.ok(Map.of("message", "Thêm sản phẩm thành công!"));
        } catch (RuntimeException e) {
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Dữ liệu không hợp lệ."
                    : e.getMessage().replace("Lỗi: ", "");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", msg));
        }
    }

    // GET: /admin/san-pham/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdmin() {
        return ResponseEntity.ok(sanPhamService.getAll());
    }

    // PATCH: /admin/san-pham/{id}/trang-thai?value=true
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThaiParam(
            @PathVariable UUID id,
            @RequestParam("value") boolean value
    ) {
        sanPhamService.updateTrangThai(id, value);
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công!"));
    }

    // PATCH: /admin/san-pham/{id}/trang-thai-body  body: { "value": true }
    @PatchMapping("/{id}/trang-thai-body")
    public ResponseEntity<?> updateTrangThaiBody(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body
    ) {
        boolean value = Boolean.parseBoolean(String.valueOf(body.get("value")));
        sanPhamService.updateTrangThai(id, value);
        return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công (body)!"));
    }
}
