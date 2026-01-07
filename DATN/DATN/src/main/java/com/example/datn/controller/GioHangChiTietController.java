package com.example.datn.controller;

import com.example.datn.entity.GioHangChiTiet;
import com.example.datn.service.GioHangChiTietService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/gio-hang-chi-tiet")
public class GioHangChiTietController {
    private final GioHangChiTietService service;
    public GioHangChiTietController(GioHangChiTietService service) { this.service = service; }

    @GetMapping
    public List<GioHangChiTiet> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public GioHangChiTiet one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("GioHangChiTiet not found"));
    }

    @PostMapping
    public GioHangChiTiet create(@RequestBody GioHangChiTiet obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public GioHangChiTiet update(@PathVariable UUID id, @RequestBody GioHangChiTiet obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
