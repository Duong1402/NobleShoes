package com.example.datn.model.Response;

import com.example.datn.dto.VoucherInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoaDonVoucherCheckResponse {

    private boolean coMaTotHon;
    private VoucherInfo phieuGiamGiaDangApDung;
    private VoucherInfo phieuGiamGiaTotNhat;
    private BigDecimal soTienGiamHienTai;
    private BigDecimal soTienGiamTotNhat;
}
