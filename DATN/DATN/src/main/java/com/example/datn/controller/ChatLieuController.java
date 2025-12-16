package com.example.datn.controller;

import com.example.datn.entity.ChatLieu;
import com.example.datn.service.ChatLieuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/chat-lieu")
public class ChatLieuController {

    private final ChatLieuService service;

    public ChatLieuController(ChatLieuService service) {
        this.service = service;
    }

    // Lấy tất cả chất liệu
    @GetMapping
    public List<ChatLieu> getAll() {
        return service.getAll();
    }

    // Lấy theo ID (UUID)
    @GetMapping("/{id}")
    public Optional<ChatLieu> getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // ✅ Thêm mới chất liệu - BỎ @Valid
    // Logic sinh mã sẽ xử lý trong service.create(cl)
    @PostMapping
    public ChatLieu create(@RequestBody ChatLieu cl) {
        return service.create(cl);
    }

    // Cập nhật chất liệu - GIỮ @Valid để kiểm tra 'ten'
    @PutMapping("/{id}")
    public ChatLieu update(@PathVariable UUID id, @Valid @RequestBody ChatLieu cl) {
        return service.update(id, cl);
    }

    // Xóa chất liệu
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
