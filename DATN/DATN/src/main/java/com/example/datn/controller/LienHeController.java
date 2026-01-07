package com.example.datn.controller;

import com.example.datn.entity.LienHe;
import com.example.datn.service.LienHeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/lien-he")
public class LienHeController {
    private final LienHeService service;
    public LienHeController(LienHeService service) { this.service = service; }

    @GetMapping
    public List<LienHe> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public LienHe one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("LienHe not found"));
    }

    @PostMapping
    public LienHe create(@RequestBody LienHe obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public LienHe update(@PathVariable UUID id, @RequestBody LienHe obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}
