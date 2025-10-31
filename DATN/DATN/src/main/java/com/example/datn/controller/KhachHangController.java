package com.example.datn.controller;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.service.KhachHangService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173"})

@RestController
@RequestMapping("/admin/khach-hang")
public class KhachHangController {
    private final KhachHangService service;
    public KhachHangController(KhachHangService service) { this.service = service; }

    @GetMapping
    public List<KhachHang> all() { return service.findAll(); }

    @GetMapping("/page")
    public Page<KhachHang> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ma") String sortBy) {
        return service.findAllPage(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public KhachHang one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("KhachHang not found"));
    }

    @PostMapping
    public KhachHang create(@RequestBody KhachHang obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public KhachHang update(@PathVariable UUID id, @RequestBody KhachHang obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}