package com.example.datn.controller;

import com.example.datn.entity.DotGiamGia;
import com.example.datn.service.DotGiamGiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/dot-giam-gia")
public class DotGiamGiaController {
    @Autowired
    private DotGiamGiaService service;

    @GetMapping
    public Page<DotGiamGia> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "ma") String sortBy) {
        return service.findAll(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public DotGiamGia one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("KhuyenMai not found"));
    }

    @PostMapping
    public DotGiamGia create( @Valid @RequestBody DotGiamGia obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public DotGiamGia update(@PathVariable UUID id,@Valid @RequestBody DotGiamGia obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.deleteById(id);
    }
}