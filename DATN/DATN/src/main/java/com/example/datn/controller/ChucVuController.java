package com.example.datn.controller;

import com.example.datn.entity.ChucVu;
import com.example.datn.service.ChucVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/chuc-vu")
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    @GetMapping
    public List<ChucVu> getAll() {
        return chucVuService.getAll();
    }

    @GetMapping("/{id}")
    public ChucVu getById(@PathVariable UUID id) {
        return chucVuService.getById(id);
    }

    @PostMapping
    public ChucVu create(@RequestBody ChucVu chucVu) {
        return chucVuService.create(chucVu);
    }

    @PutMapping("/{id}")
    public ChucVu update(@PathVariable UUID id, @RequestBody ChucVu chucVu) {
        return chucVuService.update(id, chucVu);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        chucVuService.delete(id);
    }
}