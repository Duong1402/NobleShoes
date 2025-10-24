package com.example.datn.controller;

import com.example.datn.entity.MucDichSuDung;
import com.example.datn.service.MucDichSuDungService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/muc-dich-su-dung")
public class MucDichSuDungController {

    private final MucDichSuDungService service;

    public MucDichSuDungController(MucDichSuDungService service) {
        this.service = service;
    }

    // ✅ Lấy danh sách tất cả mục đích sử dụng
    @GetMapping
    public List<MucDichSuDung> getAll() {
        return service.getAll();
    }

    // ✅ Lấy mục đích sử dụng theo ID
    @GetMapping("/{id}")
    public MucDichSuDung getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm mới (có validate)
    @PostMapping
    public MucDichSuDung create(@Valid @RequestBody MucDichSuDung mdsd) {
        return service.create(mdsd);
    }

    // ✅ Cập nhật (có validate)
    @PutMapping("/{id}")
    public MucDichSuDung update(@PathVariable UUID id, @Valid @RequestBody MucDichSuDung mdsd) {
        return service.update(id, mdsd);
    }

    // ✅ Xóa theo ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
