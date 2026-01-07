package com.example.datn.service;

import com.example.datn.entity.ChucVu;
import com.example.datn.repository.ChucVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ChucVuService {

    @Autowired
    private ChucVuRepository chucVuRepository;

    public List<ChucVu> getAll() {
        return chucVuRepository.findAll();
    }

    public ChucVu getById(UUID id) {
        return chucVuRepository.findById(id).orElse(null);
    }

    public ChucVu create(ChucVu chucVu) {
        if (chucVuRepository.existsByMa(chucVu.getMa())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã Chức vụ đã tồn tại"
            );
        }

        // Sinh mã mới
        String prefix = "CV";
        String lastMa = chucVuRepository.findTopByOrderByMaDesc()
                .map(ChucVu::getMa)
                .orElse(null);

        String newMa;
        if (lastMa == null) {
            newMa = prefix + String.format("%05d", 1);
        } else {
            int lastNumber = Integer.parseInt(lastMa.replace(prefix, ""));
            newMa = prefix + String.format("%05d", lastNumber + 1);
        }

        chucVu.setMa(newMa);

        return chucVuRepository.save(chucVu);
    }

    public ChucVu update(UUID id, ChucVu chucVu) {
        ChucVu existing = chucVuRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Không tìm thấy chức vụ có ID: " + id
        ));
        existing.setTen(chucVu.getTen());
        existing.setMoTa(chucVu.getMoTa());
        return chucVuRepository.save(existing);
    }

    public void delete(UUID id) {
        chucVuRepository.deleteById(id);
    }
}
