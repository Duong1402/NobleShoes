package com.example.datn.controller;

import com.example.datn.entity.NhaCungCap;
import com.example.datn.service.NhaCungCapService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/nha-cung-cap")
public class NhaCungCapController {
    private final NhaCungCapService service;
    public NhaCungCapController(NhaCungCapService service) { this.service = service; }

    @GetMapping
    public List<NhaCungCap> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhaCungCap one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("NhaCungCap not found"));
    }

    @PostMapping
    public NhaCungCap create(@RequestBody NhaCungCap obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public NhaCungCap update(@PathVariable UUID id, @RequestBody NhaCungCap obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
