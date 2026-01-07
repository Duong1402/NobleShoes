package com.example.datn.controller;

import com.example.datn.entity.Kho;
import com.example.datn.service.KhoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/kho")
public class KhoController {
    private final KhoService service;
    public KhoController(KhoService service) { this.service = service; }

    @GetMapping
    public List<Kho> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Kho one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("Kho not found"));
    }

    @PostMapping
    public Kho create(@RequestBody Kho obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public Kho update(@PathVariable UUID id, @RequestBody Kho obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
