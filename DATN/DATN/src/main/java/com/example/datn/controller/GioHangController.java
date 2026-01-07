package com.example.datn.controller;

import com.example.datn.entity.GioHang;
import com.example.datn.service.GioHangService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {
    private final GioHangService service;
    public GioHangController(GioHangService service) { this.service = service; }

    @GetMapping
    public List<GioHang> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public GioHang one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("GioHang not found"));
    }

    @PostMapping
    public GioHang create(@RequestBody GioHang obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public GioHang update(@PathVariable UUID id, @RequestBody GioHang obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
