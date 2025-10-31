package com.example.datn.controller;

import com.example.datn.entity.KichThuoc;
import com.example.datn.service.KichThuocService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/kich-thuoc")
public class KichThuocController {

    private final KichThuocService service;

    public KichThuocController(KichThuocService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả kích thước
    @GetMapping
    public List<KichThuoc> getAll() {
        return service.getAll();
    }

    // ✅ Lấy kích thước theo ID
    @GetMapping("/{id}")
    public KichThuoc getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm kích thước (có validate)
    @PostMapping
    public KichThuoc create(@Valid @RequestBody KichThuoc kt) {
        return service.create(kt);
    }

    // ✅ Cập nhật kích thước (có validate)
    @PutMapping("/{id}")
    public KichThuoc update(@PathVariable UUID id, @Valid @RequestBody KichThuoc kt) {
        return service.update(id, kt);
    }

    // ✅ Xóa kích thước
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
