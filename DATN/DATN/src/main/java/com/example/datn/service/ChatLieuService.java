package com.example.datn.service;

import com.example.datn.entity.ChatLieu;
import com.example.datn.repository.ChatLieuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ChatLieuService {

    private final ChatLieuRepository repo;

    public ChatLieuService(ChatLieuRepository repo) {
        this.repo = repo;
    }

    public List<ChatLieu> getAll() {
        return repo.findAll();
    }

    public ChatLieu getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public ChatLieu create(ChatLieu cl) {
        return repo.save(cl);
    }

    public ChatLieu update(UUID id, ChatLieu cl) {
        cl.setId(id);
        return repo.save(cl);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
