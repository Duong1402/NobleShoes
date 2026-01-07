package com.example.datn.controller;

import com.example.datn.entity.DanhGia;
import com.example.datn.service.DanhGiaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/danh-gia")
public class DanhGiaController {
    private final DanhGiaService service;
    public DanhGiaController(DanhGiaService service) { this.service = service; }

    @GetMapping
    public List<DanhGia> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public DanhGia one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("DanhGia not found"));
    }

    @PostMapping
    public DanhGia create(@RequestBody DanhGia obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public DanhGia update(@PathVariable UUID id, @RequestBody DanhGia obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
