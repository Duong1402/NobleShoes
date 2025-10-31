package com.example.datn.controller;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import com.example.datn.service.NhanVienService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/nhan-vien")
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @GetMapping
    public ResponseEntity<List<NhanVien>> getAll() {
        return ResponseEntity.ok(nhanVienService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NhanVien> getById(@PathVariable UUID id) {
        NhanVien nv = nhanVienService.getById(id);
        return ResponseEntity.ok(nv);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = nhanVienRepository.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }


    @PostMapping
    public ResponseEntity<NhanVien> create(@RequestBody NhanVien nhanVien) throws MessagingException {
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
