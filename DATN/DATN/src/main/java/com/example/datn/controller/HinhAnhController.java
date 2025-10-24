package com.example.datn.controller;

import com.example.datn.entity.HinhAnh;
import com.example.datn.service.HinhAnhService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/hinh-anh")
public class HinhAnhController {

    private final HinhAnhService service;

    public HinhAnhController(HinhAnhService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả hình ảnh
    @GetMapping
    public List<HinhAnh> getAll() {
        return service.getAll();
    }

    // ✅ Lấy hình ảnh theo ID
    @GetMapping("/{id}")
    public HinhAnh getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm hình ảnh (có validate)
    @PostMapping
    public HinhAnh create(@Valid @RequestBody HinhAnh ha) {
        return service.create(ha);
    }

    // ✅ Cập nhật hình ảnh (có validate)
    @PutMapping("/{id}")
    public HinhAnh update(@PathVariable UUID id, @Valid @RequestBody HinhAnh ha) {
        return service.update(id, ha);
    }

    // ✅ Xóa hình ảnh
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
