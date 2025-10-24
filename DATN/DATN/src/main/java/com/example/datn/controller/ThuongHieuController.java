package com.example.datn.controller;

import com.example.datn.entity.ThuongHieu;
import com.example.datn.service.ThuongHieuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/thuong-hieu")
public class ThuongHieuController {

    private final ThuongHieuService service;

    public ThuongHieuController(ThuongHieuService service) {
        this.service = service;
    }

    // ✅ Lấy danh sách tất cả thương hiệu
    @GetMapping
    public ResponseEntity<List<ThuongHieu>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ✅ Lấy thương hiệu theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getById(@PathVariable UUID id) {
        ThuongHieu th = service.getById(id);
        return th != null ? ResponseEntity.ok(th) : ResponseEntity.notFound().build();
    }

    // ✅ Thêm mới thương hiệu (có validate)
    @PostMapping
    public ResponseEntity<ThuongHieu> create(@Valid @RequestBody ThuongHieu th) {
        ThuongHieu created = service.create(th);
        return ResponseEntity.ok(created);
    }

    // ✅ Cập nhật thương hiệu
    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieu> update(@PathVariable UUID id, @Valid @RequestBody ThuongHieu th) {
        ThuongHieu updated = service.update(id, th);
        return ResponseEntity.ok(updated);
    }

    // ✅ Xóa thương hiệu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
