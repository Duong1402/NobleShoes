package com.example.datn.controller;

import com.example.datn.entity.DeGiay;
import com.example.datn.service.DeGiayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/de-giay")
public class DeGiayController {

    private final DeGiayService service;

    public DeGiayController(DeGiayService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả đế giày
    @GetMapping
    public List<DeGiay> getAll() {
        return service.getAll();
    }

    // ✅ Lấy theo ID
    @GetMapping("/{id}")
    public DeGiay getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm mới (có validate)
    @PostMapping
    public DeGiay create(@Valid @RequestBody DeGiay dg) {
        return service.create(dg);
    }

    // ✅ Cập nhật (có validate)
    @PutMapping("/{id}")
    public DeGiay update(@PathVariable UUID id, @Valid @RequestBody DeGiay dg) {
        return service.update(id, dg);
    }

    // ✅ Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
