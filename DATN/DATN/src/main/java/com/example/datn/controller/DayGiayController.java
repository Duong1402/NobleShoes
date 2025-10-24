package com.example.datn.controller;

import com.example.datn.entity.DayGiay;
import com.example.datn.service.DayGiayService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/day-giay")
public class DayGiayController {

    private final DayGiayService service;

    public DayGiayController(DayGiayService service) {
        this.service = service;
    }

    @GetMapping
    public List<DayGiay> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DayGiay getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    public DayGiay create(@RequestBody DayGiay dg) {
        return service.create(dg);
    }

    @PutMapping("/{id}")
    public DayGiay update(@PathVariable UUID id, @RequestBody DayGiay dg) {
        return service.update(id, dg);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
