package com.example.datn.controller;

import com.example.datn.entity.NhapKhoChiTiet;
import com.example.datn.service.NhapKhoChiTietService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/NhapkhoChiTiet")
public class NhapKhoChiTietController {
    private final NhapKhoChiTietService service;
    public NhapKhoChiTietController(NhapKhoChiTietService service) { this.service = service; }

    @GetMapping
    public List<NhapKhoChiTiet> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhapKhoChiTiet one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("NhapKhoChiTiet not found"));
    }

    @PostMapping
    public NhapKhoChiTiet create(@RequestBody NhapKhoChiTiet obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public NhapKhoChiTiet update(@PathVariable UUID id, @RequestBody NhapKhoChiTiet obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
