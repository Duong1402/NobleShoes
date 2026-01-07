package com.example.datn.controller;

import com.example.datn.entity.KhuyenMai;
import com.example.datn.service.KhuyenMaiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/khuyen-mai")
public class KhuyenMaiController {
    private final KhuyenMaiService service;
    public KhuyenMaiController(KhuyenMaiService service) { this.service = service; }

    @GetMapping
    public List<KhuyenMai> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public KhuyenMai one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("KhuyenMai not found"));
    }

    @PostMapping
    public KhuyenMai create(@RequestBody KhuyenMai obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public KhuyenMai update(@PathVariable UUID id, @RequestBody KhuyenMai obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
