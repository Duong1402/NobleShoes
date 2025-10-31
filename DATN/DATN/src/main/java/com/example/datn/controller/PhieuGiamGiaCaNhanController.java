package com.example.datn.controller;

import com.example.datn.entity.PhieuGiamGiaCaNhan;
import com.example.datn.service.PhieuGiamGiaCaNhanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/phieu-giam-gia-ca-nhan")
public class PhieuGiamGiaCaNhanController {
    @Autowired
    private PhieuGiamGiaCaNhanService service;

    @GetMapping
    public List<PhieuGiamGiaCaNhan> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PhieuGiamGiaCaNhan one(@PathVariable UUID id) {
        return service.findById(id).orElseThrow(() -> new NoSuchElementException("Phieu giam gia ca nhan is not found"));
    }

    @PostMapping
    public PhieuGiamGiaCaNhan create(@Valid @RequestBody PhieuGiamGiaCaNhan obj) { return service.save(obj); }

    @PutMapping("/{id}")
    public PhieuGiamGiaCaNhan update(@PathVariable UUID id,@Valid @RequestBody PhieuGiamGiaCaNhan obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.deleteById(id); }
}