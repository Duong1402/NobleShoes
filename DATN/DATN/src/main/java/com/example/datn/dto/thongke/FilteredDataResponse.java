package com.example.datn.dto.thongke;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FilteredDataResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OverviewStatsDto thongKeTuyChinh;
    private Page<SanPhamBanChayDto> sanPhamBanChay;
    private List<TrangThaiDonHangDto> trangThaiDonHang;
}