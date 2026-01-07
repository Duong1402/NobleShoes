package com.example.datn.dto.thongke;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OverviewResponse {
    private OverviewStatsDto homNay;
    private OverviewStatsDto tuanNay;
    private OverviewStatsDto thangNay;
    private OverviewStatsDto namNay;
    private Page<SanPhamSapHetHangDto> sanPhamSapHetHang;
    private List<GrowthStatDto> growthStats;
}