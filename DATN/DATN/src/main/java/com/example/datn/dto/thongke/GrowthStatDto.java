package com.example.datn.dto.thongke;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class GrowthStatDto {
    private String label;
    private BigDecimal value; // Giá trị (Doanh thu hoặc số lượng)
    private double percentage;
    private String trend; // "up", "down", hoặc "same"
}