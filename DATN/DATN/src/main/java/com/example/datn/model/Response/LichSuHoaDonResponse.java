package com.example.datn.model.Response;

import com.example.datn.entity.LichSuHoaDon;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LichSuHoaDonResponse {

    private LocalDateTime thoiGian;
    private String nguoiChinhSua;
    private String ghiChu;

    public LichSuHoaDonResponse(LichSuHoaDon ls) {
        this.thoiGian = ls.getThoiGian();
        this.nguoiChinhSua = ls.getNguoiThucHien();
        this.ghiChu = ls.getGhiChu();
    }
}