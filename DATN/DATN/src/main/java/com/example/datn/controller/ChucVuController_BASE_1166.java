package com.example.datn.controller;

import com.example.datn.entity.ChucVu;
import com.example.datn.service.ChucVuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/chuc-vu")
public class ChucVuController {
    private final ChucVuService service;
    public ChucVuController(ChucVuService service) { this.service = service; }

    @GetMapping
    public List<ChucVu> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public ChucVu one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("ChucVu not found"));
    }

    @PostMapping
    public ChucVu create(@RequestBody ChucVu obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public ChucVu update(@PathVariable UUID id, @RequestBody ChucVu obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
