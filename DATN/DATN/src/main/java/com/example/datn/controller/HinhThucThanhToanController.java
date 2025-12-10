package com.example.datn.controller;

import com.example.datn.entity.HinhThucThanhToan;
import com.example.datn.service.HinhThucThanhToanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/hinh-thuc-thanh-toan")
@CrossOrigin(origins = "http://localhost:5173")
public class HinhThucThanhToanController {

    private final HinhThucThanhToanService service;

    public HinhThucThanhToanController(HinhThucThanhToanService service) {
        this.service = service;
    }

    // 🔹 Lấy tất cả
    @GetMapping
    public ResponseEntity<List<HinhThucThanhToan>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // 🔹 Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<HinhThucThanhToan> getById(@PathVariable UUID id) {
        HinhThucThanhToan result = service.findById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    // 🔹 Lấy theo ID hóa đơn
    @GetMapping("/hoa-don/{idHoaDon}")
    public ResponseEntity<List<HinhThucThanhToan>> getByHoaDon(@PathVariable UUID idHoaDon) {
        return ResponseEntity.ok(service.findByHoaDon(idHoaDon));
    }

    // 🔹 Thêm mới
    @PostMapping
    public ResponseEntity<HinhThucThanhToan> create(@RequestBody HinhThucThanhToan request) {
        return ResponseEntity.ok(service.save(request));
    }

    // 🔹 Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<HinhThucThanhToan> update(
            @PathVariable UUID id,
            @RequestBody HinhThucThanhToan request
    ) {
        request.setId(id);
        return ResponseEntity.ok(service.save(request));
    }

    // 🔹 Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
