package com.example.datn.controller;

import com.example.datn.entity.MauSac;
import com.example.datn.service.MauSacService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/mau-sac")
public class MauSacController {

    private final MauSacService service;

    public MauSacController(MauSacService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả màu sắc
    @GetMapping
    public List<MauSac> getAll() {
        return service.getAll();
    }

    // ✅ Lấy màu sắc theo ID
    @GetMapping("/{id}")
    public MauSac getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm màu sắc (có validate)
    @PostMapping
    public MauSac create(@Valid @RequestBody MauSac ms) {
        return service.create(ms);
    }

    // ✅ Cập nhật màu sắc (có validate)
    @PutMapping("/{id}")
    public MauSac update(@PathVariable UUID id, @Valid @RequestBody MauSac ms) {
        return service.update(id, ms);
    }

    // ✅ Xóa màu sắc
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
