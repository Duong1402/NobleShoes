// src/main/java/com/example/datn/controller/OrderPublicController.java
package com.example.datn.controller;

import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.request.TaoHoaDonRequest;
import com.example.datn.service.HoaDonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/public/orders", "/public/orders"})
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*"},
        allowCredentials = "true"
)
public class OrderPublicController {

    private final HoaDonService hoaDonService;

    public OrderPublicController(HoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HoaDonDetailResponse> getOrderByCode(@PathVariable String code) {
        HoaDonDetailResponse detail = hoaDonService.getHoaDonDetailByCode(code);
        return (detail == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(detail);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HoaDonDetailResponse> createOrder(@Valid @RequestBody TaoHoaDonRequest request) {
        HoaDonDetailResponse created = hoaDonService.createHoaDon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
