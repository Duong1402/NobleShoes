package com.example.datn.controller;

import com.example.datn.entity.XuatXu;
import com.example.datn.service.XuatXuService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/xuat-xu")
public class XuatXuController {

    private final XuatXuService service;

    public XuatXuController(XuatXuService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả xuất xứ
    @GetMapping
    public ResponseEntity<List<XuatXu>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ✅ Lấy chi tiết xuất xứ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<XuatXu> getById(@PathVariable UUID id) {
        XuatXu xx = service.getById(id);
        return xx != null ? ResponseEntity.ok(xx) : ResponseEntity.notFound().build();
    }

    // ✅ Thêm mới xuất xứ (có validate)
    @PostMapping
    public ResponseEntity<XuatXu> create(@Valid @RequestBody XuatXu xx) {
        XuatXu created = service.create(xx);
        return ResponseEntity.ok(created);
    }

    // ✅ Cập nhật xuất xứ
    @PutMapping("/{id}")
    public ResponseEntity<XuatXu> update(@PathVariable UUID id, @Valid @RequestBody XuatXu xx) {
        XuatXu updated = service.update(id, xx);
        return ResponseEntity.ok(updated);
    }

    // ✅ Xóa xuất xứ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
