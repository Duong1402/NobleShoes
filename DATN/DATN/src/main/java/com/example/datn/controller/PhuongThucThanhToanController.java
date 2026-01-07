package com.example.datn.controller;

import com.example.datn.entity.PhuongThucThanhToan;
import com.example.datn.service.PhuongThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/phuong-thuc-thanh-toan")
@CrossOrigin(origins = "http://localhost:5173")
public class PhuongThucThanhToanController {
    @Autowired
    private PhuongThucThanhToanService phuongThucThanhToanService;

    public PhuongThucThanhToanController(PhuongThucThanhToanService service) {
        this.phuongThucThanhToanService = service;
    }

    // 🔹 Lấy danh sách tất cả phương thức thanh toán
    @GetMapping
    public ResponseEntity<List<PhuongThucThanhToan>> getAll() {
        return ResponseEntity.ok(phuongThucThanhToanService.findAll());
    }

    // 🔹 Lấy chi tiết 1 phương thức theo id
    @GetMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> getById(@PathVariable UUID id) {
        return phuongThucThanhToanService.findAll().stream()
                .filter(pt -> pt.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Thêm mới phương thức thanh toán
    @PostMapping
    public ResponseEntity<PhuongThucThanhToan> create(@RequestBody PhuongThucThanhToan request) {
        return ResponseEntity.ok(phuongThucThanhToanService.save(request));
    }

    // 🔹 Cập nhật phương thức thanh toán
    @PutMapping("/{id}")
    public ResponseEntity<PhuongThucThanhToan> update(
            @PathVariable UUID id,
            @RequestBody PhuongThucThanhToan request
    ) {
        request.setId(id);
        return ResponseEntity.ok(phuongThucThanhToanService.save(request));
    }

    // 🔹 Xóa phương thức thanh toán
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        phuongThucThanhToanService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
