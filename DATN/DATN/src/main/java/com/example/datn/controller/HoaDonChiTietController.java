package com.example.datn.controller;

import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.service.HoaDonChiTietService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/hoa-don-chi-tiet")
public class HoaDonChiTietController {
    private final HoaDonChiTietService service;

    public HoaDonChiTietController(HoaDonChiTietService service) {
        this.service = service;
    }

    @GetMapping
    public List<HoaDonChiTiet> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public HoaDonChiTiet one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("HoaDonChiTiet not found"));
    }

    @PostMapping
    public HoaDonChiTiet create(@RequestBody HoaDonChiTiet obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public HoaDonChiTiet update(@PathVariable UUID id, @RequestBody HoaDonChiTiet obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}