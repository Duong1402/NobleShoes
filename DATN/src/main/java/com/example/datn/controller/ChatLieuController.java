package com.example.datn.controller;

import com.example.datn.entity.ChatLieu;
import com.example.datn.service.ChatLieuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/admin/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService service;

    public ChatLieuController(ChatLieuService service) {
        this.service = service;
    }

    // ✅ Lấy tất cả
    @GetMapping
    public List<ChatLieu> getAll() {
        return service.getAll();
    }

    // ✅ Lấy theo ID
    @GetMapping("/{id}")
    public ChatLieu getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm mới (có validate)
    @PostMapping
    public ChatLieu create(@Valid @RequestBody ChatLieu cl) {
        return service.create(cl);
    }

    // ✅ Cập nhật (có validate)
    @PutMapping("/{id}")
    public ChatLieu update(@PathVariable UUID id, @Valid @RequestBody ChatLieu cl) {
        return service.update(id, cl);
    }

    // ✅ Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
