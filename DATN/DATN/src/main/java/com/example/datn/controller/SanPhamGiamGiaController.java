package com.example.datn.controller;

import com.example.datn.entity.SanPhamGiamGia;
import com.example.datn.service.SanPhamGiamGiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/san-pham-giam-gia")
public class SanPhamGiamGiaController {
    private final SanPhamGiamGiaService service;
    public SanPhamGiamGiaController(SanPhamGiamGiaService service) { this.service = service; }

    @GetMapping
    public List<SanPhamGiamGia> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public SanPhamGiamGia one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("SanPhamGiamGia not found"));
    }

    @PostMapping
    public SanPhamGiamGia create(@RequestBody SanPhamGiamGia obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public SanPhamGiamGia update(@PathVariable UUID id, @RequestBody SanPhamGiamGia obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
