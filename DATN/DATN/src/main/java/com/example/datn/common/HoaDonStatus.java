package com.example.datn.common;

public final class HoaDonStatus {

    private HoaDonStatus() {
    }

    // 0 = Chờ thanh toán (mặc định khi tạo hóa đơn)
    public static final int CHO_THANH_TOAN = 0;

    // 1 = Chờ xác nhận
    public static final int CHO_XAC_NHAN = 1;

    // 2 = Đã xác nhận  👈 trừ kho ở trạng thái này
    public static final int DA_XAC_NHAN = 2;

    // 3 = Đang chuẩn bị
    public static final int DANG_CHUAN_BI = 3;

    // 4 = Đang giao
    public static final int DANG_GIAO = 4;

    // 5 = Giao hàng thất bại
    public static final int GIAO_HANG_THAT_BAI = 5;

    // 6 = Hoàn thành
    public static final int HOAN_THANH = 6;

    // 7 = Đã hủy
    public static final int DA_HUY = 7;
}
