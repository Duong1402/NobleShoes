package com.example.datn.controller;

import com.example.datn.dto.SanPhamRequest;
import com.example.datn.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/san-pham")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

//    @GetMapping
//    public List<SanPham> getAll() {
//        return sanPhamService.getAll();
//    }

    @PostMapping
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamRequest request) {
        sanPhamService.saveSanPham(request);
        return ResponseEntity.ok("Thêm sản phẩm thành công!");
    }

//    @PutMapping("/{id}")
//    public SanPham update(@PathVariable UUID id, @Valid @RequestBody SanPham sp) {
//        return sanPhamService.update(id, sp);
//    }


    // 🆕 API mới: Lấy danh sách sản phẩm + số lượng chi tiết
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sanPhamService.getAll());
    }
//    @PatchMapping("/{id}/trang-thai")
//    public ResponseEntity<?> updateTrangThai(
//            @PathVariable UUID id,
//            @RequestParam("value") boolean value) {
//        sanPhamService.updateTrangThai(id, value);
//        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
//    }


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
