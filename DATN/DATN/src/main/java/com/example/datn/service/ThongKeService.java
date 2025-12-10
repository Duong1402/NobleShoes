package com.example.datn.service;

import com.example.datn.dto.thongke.*;
import com.example.datn.entity.HoaDon;
import com.example.datn.repository.ChiTietSanPhamRepository;
import com.example.datn.repository.HoaDonChiTietRepository;
import com.example.datn.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private static final int TRANG_THAI_HUY = 0;
    private static final int TRANG_THAI_HOAN_THANH = 5;


    public OverviewResponse getOverviewData(Pageable lowStockPageable) {
        LocalDate today = LocalDate.now();


        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate startOfYear = today.withDayOfYear(1);
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());

        OverviewStatsDto homNay = getStatsForDateRange(today, today);
        OverviewStatsDto tuanNay = getStatsForDateRange(startOfWeek, endOfWeek);
        OverviewStatsDto thangNay = getStatsForDateRange(startOfMonth, endOfMonth);
        OverviewStatsDto namNay = getStatsForDateRange(startOfYear, endOfYear);


        LocalDate yesterday = today.minusDays(1);
        LocalDate startOfLastWeek = startOfWeek.minusWeeks(1);
        LocalDate endOfLastWeek = endOfWeek.minusWeeks(1);
        LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDate endOfLastMonth = startOfLastMonth.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate startOfLastYear = startOfYear.minusYears(1);
        LocalDate endOfLastYear = startOfLastYear.with(TemporalAdjusters.lastDayOfYear());

        OverviewStatsDto homQua = getStatsForDateRange(yesterday, yesterday);
        OverviewStatsDto tuanTruoc = getStatsForDateRange(startOfLastWeek, endOfLastWeek);
        OverviewStatsDto thangTruoc = getStatsForDateRange(startOfLastMonth, endOfLastMonth);
        OverviewStatsDto namTruoc = getStatsForDateRange(startOfLastYear, endOfLastYear);

        List<GrowthStatDto> growthStats = new ArrayList<>();

        // Doanh thu
        growthStats.add(createGrowthStat("Doanh thu ngày", homNay.getTongDoanhThu(), homQua.getTongDoanhThu()));
        growthStats.add(createGrowthStat("Doanh thu Tuần", tuanNay.getTongDoanhThu(), tuanTruoc.getTongDoanhThu()));
        growthStats.add(createGrowthStat("Doanh thu Tháng", thangNay.getTongDoanhThu(), thangTruoc.getTongDoanhThu()));
        growthStats.add(createGrowthStat("Doanh thu năm", namNay.getTongDoanhThu(), namTruoc.getTongDoanhThu()));

        // Đơn hàng (Thành công)
        growthStats.add(createGrowthStat("Đơn hàng ngày", homNay.getSoDonThanhCong(), homQua.getSoDonThanhCong()));
        growthStats.add(createGrowthStat("Đơn hàng tuần", tuanNay.getSoDonThanhCong(), tuanTruoc.getSoDonThanhCong()));
        growthStats.add(createGrowthStat("Đơn hàng tháng", thangNay.getSoDonThanhCong(), thangTruoc.getSoDonThanhCong()));
        growthStats.add(createGrowthStat("Đơn hàng năm", namNay.getSoDonThanhCong(), namTruoc.getSoDonThanhCong()));

        // Sản phẩm
        growthStats.add(createGrowthStat("Sản phẩm ngày", homNay.getSoSanPhamDaBan(), homQua.getSoSanPhamDaBan()));
        growthStats.add(createGrowthStat("Sản phẩm tuần", tuanNay.getSoSanPhamDaBan(), tuanTruoc.getSoSanPhamDaBan()));
        growthStats.add(createGrowthStat("Sản phẩm tháng", thangNay.getSoSanPhamDaBan(), thangTruoc.getSoSanPhamDaBan()));
        growthStats.add(createGrowthStat("Sản phẩm năm", namNay.getSoSanPhamDaBan(), namTruoc.getSoSanPhamDaBan()));


        Page<SanPhamSapHetHangDto> sapHetHangPage = chiTietSanPhamRepository.findSanPhamSapHetHang(lowStockPageable);

        return new OverviewResponse(homNay, tuanNay, thangNay, namNay, sapHetHangPage, growthStats);
    }

    private double calculatePercentageChange(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            if (current.compareTo(BigDecimal.ZERO) > 0) return 100.0;
            return 0.0;
        }
        if (current.compareTo(BigDecimal.ZERO) == 0) {
            return -100.0;
        }

        return current.subtract(previous)
                .divide(previous, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }


    private double calculatePercentageChange(long current, long previous) {
        return calculatePercentageChange(BigDecimal.valueOf(current), BigDecimal.valueOf(previous));
    }

    private GrowthStatDto createGrowthStat(String label, BigDecimal currentValue, BigDecimal previousValue) {
        double perc = calculatePercentageChange(currentValue, previousValue);
        String trend = (perc > 0) ? "up" : ((perc < 0) ? "down" : "same");
        return new GrowthStatDto(label, currentValue, perc, trend);
    }


    private GrowthStatDto createGrowthStat(String label, long currentValue, long previousValue) {
        double perc = calculatePercentageChange(currentValue, previousValue);
        String trend = (perc > 0) ? "up" : ((perc < 0) ? "down" : "same");
        return new GrowthStatDto(label, BigDecimal.valueOf(currentValue), perc, trend);
    }


    public FilteredDataResponse getFilteredData(ThongKeFilterRequest filter, Pageable pageable) {

        LocalDate[] dateRange = getDateRangeFromFilter(
                filter.getFilterType(),
                filter.getTuNgay(),
                filter.getDenNgay()
        );
        LocalDate start = dateRange[0];
        LocalDate end = dateRange[1];
        OverviewStatsDto thongKeTuyChinh = null;
        if ("TUY_CHINH".equals(filter.getFilterType())) {
            thongKeTuyChinh = getStatsForDateRange(start, end);
        }
        Page<SanPhamBanChayDto> sanPhamBanChay = hoaDonChiTietRepository.findBestSellingProducts(start, end, pageable);
        List<TrangThaiDonHangDto> trangThaiDonHang = hoaDonRepository.findTrangThaiDonHangStats(start, end);
        return new FilteredDataResponse(thongKeTuyChinh, sanPhamBanChay, trangThaiDonHang);
    }


    public OverviewStatsDto getStatsForDateRange(LocalDate start, LocalDate end) {
        List<HoaDon> hoaDons = hoaDonRepository.findByNgayTaoBetween(start, end);
        if (hoaDons.isEmpty()) {
            return new OverviewStatsDto();
        }
        List<HoaDon> donThanhCong = hoaDons.stream()
                .filter(hd -> hd.getTrangThai() == TRANG_THAI_HOAN_THANH)
                .collect(Collectors.toList());
        List<HoaDon> donHuy = hoaDons.stream()
                .filter(hd -> hd.getTrangThai() == TRANG_THAI_HUY)
                .collect(Collectors.toList());
        BigDecimal tongDoanhThu = donThanhCong.stream()
                .map(HoaDon::getTongTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long soSanPhamDaBan = 0;
        if (!donThanhCong.isEmpty()) {
            List<UUID> ids = donThanhCong.stream().map(HoaDon::getId).collect(Collectors.toList());
            Long sum = hoaDonChiTietRepository.sumSoLuongByHoaDonIdIn(ids);
            if (sum != null) {
                soSanPhamDaBan = sum;
            }
        }
        return new OverviewStatsDto(
                tongDoanhThu,
                donThanhCong.size(),
                donHuy.size(),
                soSanPhamDaBan,
                0
        );
    }


    private LocalDate[] getDateRangeFromFilter(String filterType, LocalDate tuNgay, LocalDate denNgay) {
        LocalDate start, end;
        LocalDate today = LocalDate.now();
        switch (filterType) {
            case "NGAY":
                start = today;
                end = today;
                break;
            case "TUAN":
                start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                end = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case "THANG":
                start = today.withDayOfMonth(1);
                end = today.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case "NAM":
                start = today.withDayOfYear(1);
                end = today.with(TemporalAdjusters.lastDayOfYear());
                break;
            case "TUY_CHINH":
                if (tuNgay == null || denNgay == null) {
                    throw new IllegalArgumentException("Ngày bắt đầu và kết thúc là bắt buộc cho bộ lọc tùy chỉnh.");
                }
                start = tuNgay;
                end = denNgay;
                break;
            default:
                throw new IllegalArgumentException("Loại bộ lọc không hợp lệ: " + filterType);
        }
        return new LocalDate[]{start, end};
    }
}