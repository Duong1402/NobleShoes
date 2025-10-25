package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/nhan-vien")
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping
    public ResponseEntity<List<NhanVien>> getAll() {
        return ResponseEntity.ok(nhanVienService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable UUID id) {
        NhanVien nv = nhanVienService.getById(id);
        return ResponseEntity.ok(nv);
    }

    @PostMapping
    public ResponseEntity<NhanVien> create(@RequestBody NhanVien nhanVien) {
        NhanVien created = nhanVienService.create(nhanVien);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NhanVien> update(@PathVariable UUID id, @RequestBody NhanVien nhanVien) {
        NhanVien updated = nhanVienService.update(id, nhanVien);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        nhanVienService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
