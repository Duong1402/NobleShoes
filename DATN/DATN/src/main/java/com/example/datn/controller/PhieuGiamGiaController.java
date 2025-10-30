package com.example.datn.controller;

import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.service.PhieuGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/phieu-giam-gia")
public class PhieuGiamGiaController {
    @Autowired
    private  PhieuGiamGiaService service;
    public PhieuGiamGiaController(PhieuGiamGiaService service) { this.service = service; }

    @GetMapping
    public List<PhieuGiamGia> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public PhieuGiamGia one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("KhuyenMai not found"));
    }

    @PostMapping
    public PhieuGiamGia create(@RequestBody PhieuGiamGia obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public PhieuGiamGia update(@PathVariable UUID id, @RequestBody PhieuGiamGia obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
