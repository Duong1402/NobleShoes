package com.example.datn.controller;

import com.example.datn.entity.DiaChiKhachHang;
import com.example.datn.service.DiaChiKhachHangService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/DiaChikhach-hang")
public class DiaChiKhachHangController {
    private final DiaChiKhachHangService service;
    public DiaChiKhachHangController(DiaChiKhachHangService service) { this.service = service; }

    @GetMapping
    public List<DiaChiKhachHang> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public DiaChiKhachHang one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("DiaChiKhachHang not found"));
    }

    @PostMapping
    public DiaChiKhachHang create(@RequestBody DiaChiKhachHang obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public DiaChiKhachHang update(@PathVariable UUID id, @RequestBody DiaChiKhachHang obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
