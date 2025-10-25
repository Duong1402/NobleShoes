package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.service.NhanVienService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/NhanVien")
public class NhanVienController {
    private final NhanVienService service;
    public NhanVienController(NhanVienService service) { this.service = service; }

    @GetMapping
    public List<NhanVien> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhanVien one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("NhanVien not found"));
    }

    @PostMapping
    public NhanVien create(@RequestBody NhanVien obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public NhanVien update(@PathVariable UUID id, @RequestBody NhanVien obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
