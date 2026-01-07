package com.example.datn.controller;

import com.example.datn.entity.BinhLuan;
import com.example.datn.service.BinhLuanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/binh-luan")
public class BinhLuanController {
    private final BinhLuanService service;
    public BinhLuanController(BinhLuanService service) { this.service = service; }

    @GetMapping
    public List<BinhLuan> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public BinhLuan one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("BinhLuan not found"));
    }

    @PostMapping
    public BinhLuan create(@RequestBody BinhLuan obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public BinhLuan update(@PathVariable UUID id, @RequestBody BinhLuan obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
