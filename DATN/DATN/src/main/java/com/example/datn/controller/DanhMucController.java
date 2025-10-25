package com.example.datn.controller;

import com.example.datn.entity.DanhMuc;
import com.example.datn.service.DanhMucService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/danh-muc")
public class DanhMucController {

    private final DanhMucService service;

    public DanhMucController(DanhMucService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả danh mục
    @GetMapping
    public List<DanhMuc> getAll() {
        return service.getAll();
    }

    // ✅ Lấy danh mục theo ID
    @GetMapping("/{id}")
    public DanhMuc getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm mới danh mục (có validate)
    @PostMapping
    public DanhMuc create(@Valid @RequestBody DanhMuc dm) {
        return service.create(dm);
    }

    // ✅ Cập nhật danh mục (có validate)
    @PutMapping("/{id}")
    public DanhMuc update(@PathVariable UUID id, @Valid @RequestBody DanhMuc dm) {
        return service.update(id, dm);
    }

    // ✅ Xóa danh mục
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
