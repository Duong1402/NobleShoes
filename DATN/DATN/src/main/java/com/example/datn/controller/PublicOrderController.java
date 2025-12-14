// src/main/java/com/example/datn/controller/PublicOrderController.java
package com.example.datn.controller;

import com.example.datn.model.request.CancelOrderRequest;
import com.example.datn.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/orders")
@RequiredArgsConstructor
public class PublicOrderController {

    private final HoaDonService hoaDonService;

    @PatchMapping("/{code}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String code, @RequestBody CancelOrderRequest req) {
        hoaDonService.cancelPublicByCode(code, req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> detail(@PathVariable String code) {
        return ResponseEntity.ok(hoaDonService.getHoaDonDetailByCode(code));
    }
}
