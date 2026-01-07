package com.example.datn.controller;

import com.example.datn.entity.NhapKho;
import com.example.datn.service.NhapKhoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/Nhapkho")
public class NhapKhoController {
    private final NhapKhoService service;
    public NhapKhoController(NhapKhoService service) { this.service = service; }

    @GetMapping
    public List<NhapKho> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public NhapKho one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("NhapKho not found"));
    }

    @PostMapping
    public NhapKho create(@RequestBody NhapKho obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public NhapKho update(@PathVariable UUID id, @RequestBody NhapKho obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
