package com.example.datn.controller;

import com.example.datn.dto.SanPhamRequest;
import com.example.datn.entity.SanPham;
import com.example.datn.service.SanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @GetMapping
    public List<SanPham> getAll() {
        return sanPhamService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamRequest request) {
        sanPhamService.saveSanPham(request);
        return ResponseEntity.ok("Thêm sản phẩm thành công!");
    }

    @PutMapping("/{id}")
    public SanPham update(@PathVariable UUID id, @Valid @RequestBody SanPham sp) {
        return sanPhamService.update(id, sp);
    }
}
