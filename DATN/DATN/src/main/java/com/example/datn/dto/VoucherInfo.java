package com.example.datn.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class VoucherInfo {
    private UUID id;
    private String ma;
    private String ten;

    private Boolean hinhThucGiamGia;

    private BigDecimal giaTriGiam;
    private BigDecimal giaTriGiamToiDa;
    private BigDecimal giaTriGiamToiThieu;

    private Date ngayBatDau;
    private Date ngayKetThuc;
}
