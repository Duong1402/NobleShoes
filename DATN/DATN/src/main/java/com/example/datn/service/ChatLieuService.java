package com.example.datn.service;

import com.example.datn.entity.ChatLieu;
import com.example.datn.repository.ChatLieuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatLieuService {

    private final ChatLieuRepository repository;

    public ChatLieuService(ChatLieuRepository repository) {
        this.repository = repository;
    }

    public synchronized ChatLieu create(ChatLieu newCl) {

        String newMa = generateNewMa();
        newCl.setMa(newMa);

        return repository.save(newCl);
    }

    /**
     * Sinh mã tự động: CL01, CL02, ...
     * yêu cầu repository có findTopByOrderByMaDesc()
     */
    private String generateNewMa() {
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(ChatLieu::getMa)
                .orElse("CL00");

        // Lấy phần số của mã
        String number = lastMa.replaceAll("[^0-9]", "");

        int currentNum;
        try {
            currentNum = Integer.parseInt(number);
        } catch (Exception e) {
            currentNum = 0;
        }

        int next = currentNum + 1;

        return "CL" + String.format("%02d", next); // CL01
    }

    public ChatLieu update(UUID id, ChatLieu clDetails) {
        ChatLieu existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chất Liệu không tồn tại với id: " + id));

        // Update tên
        existing.setTen(clDetails.getTen());

        // Không cho sửa mã
        return repository.save(existing);
    }

    public List<ChatLieu> getAll() {
        return repository.findAll();
    }

    public Optional<ChatLieu> getById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
