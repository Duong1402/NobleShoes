package com.example.datn.controller;

import com.example.datn.entity.DayGiay;
import com.example.datn.service.DayGiayService;
import jakarta.validation.Valid; // Thêm import cho @Valid
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Thêm import cho ResponseEntity
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Optional; // Có thể không cần nhưng giúp code rõ ràng hơn

@RestController
@RequestMapping("/admin/day-giay")
public class DayGiayController {

    private final DayGiayService service;

    public DayGiayController(DayGiayService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. GET ALL (200 OK)
    // ----------------------------------------------------
    @GetMapping
    public List<DayGiay> getAll() {
        return service.getAll();
    }

    // ----------------------------------------------------
    // 2. GET BY ID (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DayGiay> getById(@PathVariable UUID id) {
        // DayGiayService đã được refactor để trả về Optional
        Optional<DayGiay> dayGiay = service.getById(id);

        return dayGiay
                .map(dg -> ResponseEntity.ok(dg)) // Nếu tìm thấy, trả về 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về 404 NOT FOUND
    }

    // ----------------------------------------------------
    // 3. CREATE (201 CREATED)
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<DayGiay> create( @RequestBody DayGiay dg) { // THÊM @Valid
        DayGiay createdDg = service.create(dg);
        // Trả về 201 Created là chuẩn RESTful
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDg);
    }

    // ----------------------------------------------------
    // 4. UPDATE (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DayGiay> update(@PathVariable UUID id, @Valid @RequestBody DayGiay dg) { // THÊM @Valid
        try {
            DayGiay updatedDg = service.update(id, dg);
            return ResponseEntity.ok(updatedDg); // 200 OK nếu cập nhật thành công
        } catch (RuntimeException e) {
            // Xử lý lỗi nếu Service ném ra Exception (vì không tìm thấy ID)
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        }
    }

    // ----------------------------------------------------
    // 5. DELETE (204 NO CONTENT)
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Giả sử service.delete(id) không ném lỗi nếu không tìm thấy.
        // Để trả về 404 chuẩn, bạn nên kiểm tra tồn tại trước khi xóa,
        // hoặc để @ControllerAdvice xử lý Exception do DB ném ra khi cố gắng xóa bản ghi không tồn tại.
        service.delete(id);

        // Trả về 204 No Content là chuẩn RESTful khi xóa thành công
        return ResponseEntity.noContent().build();
    }
}