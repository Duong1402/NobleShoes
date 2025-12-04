package com.example.datn.controller;

import com.example.datn.dto.SanPhamRequest;
import com.example.datn.entity.SanPham;
import com.example.datn.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/san-pham")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @PostMapping
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamRequest request) {
        try {
            sanPhamService.saveSanPham(request);
            // THÀNH CÔNG: Trả về HTTP 200 OK
            return ResponseEntity.ok(Map.of("message", "Thêm sản phẩm thành công!"));
        } catch (RuntimeException e) {
            // THẤT BẠI: Service ném ra lỗi nghiệp vụ (ví dụ: Tên đã tồn tại)

            // 1. Trích xuất thông báo lỗi. Ví dụ: "Lỗi: Tên sản phẩm đã tồn tại!" -> "Tên sản phẩm đã tồn tại!"
            String errorMessage = e.getMessage() != null ? e.getMessage().replace("Lỗi: ", "") : "Dữ liệu không hợp lệ.";

            // 2. Trả về HTTP Status 400 Bad Request kèm theo body JSON
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", errorMessage));
        }
    }

    // 🆕 API mới: Lấy danh sách sản phẩm + số lượng chi tiết
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sanPhamService.getAll());
    }


    // nhận request param (giữ nguyên)
    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThaiParam(
            @PathVariable UUID id,
            @RequestParam("value") boolean value) {
        sanPhamService.updateTrangThai(id, value);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }

    // nhận body JSON: { "value": true }
    @PatchMapping("/{id}/trang-thai-body")
    public ResponseEntity<?> updateTrangThaiBody(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        boolean value = Boolean.parseBoolean(String.valueOf(body.get("value")));
        sanPhamService.updateTrangThai(id, value);
        return ResponseEntity.ok("Cập nhật trạng thái thành công (body)!");
    }


}
