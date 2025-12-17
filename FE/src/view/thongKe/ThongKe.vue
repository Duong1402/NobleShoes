<script setup>
import { ref, computed, onMounted, watch } from "vue";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { Doughnut } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale,
} from "chart.js";
import { getOverviewData, getFilteredData } from "../../service/ThongKe.js";
import * as XLSX from "xlsx";

import { Line, Bar } from "vue-chartjs";
import { LinearScale, BarElement, PointElement, LineElement } from "chart.js";

ChartJS.register(LinearScale, BarElement, PointElement, LineElement);

ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale);

const isLoading = ref(true);
const isOverviewLoading = ref(true);
const selectedFilter = ref("NGAY");
const showCustomCard = ref(false);
const customStartDate = ref("");
const customEndDate = ref("");

const itemsPerPage = ref(5);
const currentPage = ref(0);
const totalPages = ref(0);

const lowStockItemsPerPage = ref(5);
const lowStockCurrentPage = ref(0);
const lowStockTotalPages = ref(0);

const overviewStats = ref({
  HOM_NAY: {
    title: "Hôm nay",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-info",
    icon: "fa-regular fa-calendar-check",
  },
  TUAN_NAY: {
    title: "Tuần này",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-warning",
    icon: "fa-regular fa-calendar-minus",
  },
  THANG_NAY: {
    title: "Tháng này",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-primary",
    icon: "fa-calendar-alt",
  },
  NAM_NAY: {
    title: "Năm nay",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-success",
    icon: "fa-calendar",
  },
  TUY_CHINH: {
    title: "Tùy chỉnh",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-info",
    icon: "fa-edit",
  },
});
const bestSellingProducts = ref([]);
const lowStockProducts = ref([]);
const growthStats = ref([]);

const pieChartData = ref({
  labels: ["Đang tải..."],
  datasets: [{ data: [1], backgroundColor: ["#ccc"] }],
});

const pieChartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
});

const TRANG_THAI_MAP = {
  0: { label: "Chờ thanh toán", color: "#ffc107" },
  1: { label: "Chờ xác nhận", color: "#6c757d" },
  2: { label: "Đã xác nhận", color: "#0dcaf0" },
  3: { label: "Đang Chuẩn bị", color: "#6f42c1" },
  4: { label: "Đang giao", color: "#0d6efd" },
  5: { label: "Giao hàng thất bại", color: "#dc3545" },
  6: { label: "Hoàn thành", color: "#198754" },
  7: { label: "Đã hủy", color: "#343a40" },
};

const pieChartLegend = computed(() => {
  const labels = pieChartData.value.labels || [];
  const data = pieChartData.value.datasets[0]?.data || [];
  const colors = pieChartData.value.datasets[0]?.backgroundColor || [];
  const total = data.reduce((a, b) => a + b, 0);
  if (total === 0) return [];
  return labels.map((label, i) => ({
    label,
    color: colors[i],
    percentage: ((data[i] / total) * 100).toFixed(2),
  }));
});

const formatCurrency = (value) => {
  if (value === null || value === undefined) return "0 ₫";
  return value.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
};

const mapOverviewDto = (dto) => {
  if (!dto)
    return { total: "0 ₫", sanPham: 0, donThanhCong: 0, donHuy: 0, donTra: 0 };
  return {
    total: formatCurrency(dto.tongDoanhThu),
    sanPham: dto.soSanPhamDaBan,
    donThanhCong: dto.soDonThanhCong,
    donHuy: dto.soDonHuy,
    donTra: dto.soDonTra,
  };
};

const mapApiDataToPieChart = (apiData) => {
  if (!apiData || apiData.length === 0) {
    return {
      labels: ["Không có dữ liệu"],
      datasets: [{ data: [1], backgroundColor: ["#e0e0e0"] }],
    };
  }
  const labels = [];
  const data = [];
  const backgroundColors = [];
  apiData.forEach((item) => {
    const info = TRANG_THAI_MAP[item.trangThai];
    if (info) {
      labels.push(info.label);
      data.push(item.soLuong);
      backgroundColors.push(info.color);
    }
  });
  return {
    labels: labels,
    datasets: [{ data: data, backgroundColor: backgroundColors }],
  };
};

const loadOverviewData = async () => {
  isOverviewLoading.value = true;
  try {
    const params = {
      page: lowStockCurrentPage.value,
      size: lowStockItemsPerPage.value,
    };
    const response = await getOverviewData(params);
    const data = response.data;

    overviewStats.value.HOM_NAY = {
      ...overviewStats.value.HOM_NAY,
      ...mapOverviewDto(data.homNay),
    };
    overviewStats.value.TUAN_NAY = {
      ...overviewStats.value.TUAN_NAY,
      ...mapOverviewDto(data.tuanNay),
    };
    overviewStats.value.THANG_NAY = {
      ...overviewStats.value.THANG_NAY,
      ...mapOverviewDto(data.thangNay),
    };
    overviewStats.value.NAM_NAY = {
      ...overviewStats.value.NAM_NAY,
      ...mapOverviewDto(data.namNay),
    };

    lowStockProducts.value = data.sanPhamSapHetHang.content;
    lowStockTotalPages.value = data.sanPhamSapHetHang.totalPages;

    growthStats.value = data.growthStats;
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu tổng quan:", error);
  } finally {
    isOverviewLoading.value = false;
  }
};

const loadFilteredData = async () => {
  isLoading.value = true;
  const params = {
    filterType: selectedFilter.value,
    page: currentPage.value,
    size: itemsPerPage.value,
  };
  if (selectedFilter.value === "TUY_CHINH") {
    if (!customStartDate.value || !customEndDate.value) {
      isLoading.value = false;
      return;
    }
    params.tuNgay = customStartDate.value;
    params.denNgay = customEndDate.value;
  }
  try {
    const response = await getFilteredData(params);
    const data = response.data;
    if (data.thongKeTuyChinh) {
      overviewStats.value.TUY_CHINH = {
        ...overviewStats.value.TUY_CHINH,
        ...mapOverviewDto(data.thongKeTuyChinh),
      };
    }
    bestSellingProducts.value = data.sanPhamBanChay.content;
    totalPages.value = data.sanPhamBanChay.totalPages;
    pieChartData.value = mapApiDataToPieChart(data.trangThaiDonHang);
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu lọc:", error);
    bestSellingProducts.value = [];
    pieChartData.value = mapApiDataToPieChart([]);
  } finally {
    isLoading.value = false;
  }
};

const selectFilter = (filter) => {
  selectedFilter.value = filter;
  showCustomCard.value = filter === "TUY_CHINH";
  currentPage.value = 0;
};

const changePage = (page) => {
  if (page < 0 || page >= totalPages.value) return;
  currentPage.value = page;
};

const changeLowStockPage = (page) => {
  if (page < 0 || page >= lowStockTotalPages.value) return;
  lowStockCurrentPage.value = page;
};

onMounted(() => {
  loadOverviewData();
  loadFilteredData();
});

watch(
  [lowStockCurrentPage, lowStockItemsPerPage],
  () => {
    loadOverviewData();
  },
  { immediate: false }
);

watch(
  [selectedFilter, customStartDate, customEndDate, currentPage, itemsPerPage],
  () => {
    loadFilteredData();
  },
  { immediate: false }
);

const handleExportThongKeExcel = async () => {
  try {
    // Không có dữ liệu
    if (!bestSellingProducts.value.length && !lowStockProducts.value.length) {
      notify.warning("Không có dữ liệu thống kê để xuất Excel!");
      return;
    }

    const wb = XLSX.utils.book_new();

    /* ================================
       1) Sheet: SẢN PHẨM BÁN CHẠY
       ================================ */

    const bestHeader = ["Tên sản phẩm", "Số lượng bán", "Giá tiền"];

    const bestRows = bestSellingProducts.value.map((p) => [
      p.tenSanPham,
      p.soLuongBan,
      formatCurrency(p.giaTien),
    ]);

    const wsBest = XLSX.utils.aoa_to_sheet([bestHeader, ...bestRows]);
    XLSX.utils.book_append_sheet(wb, wsBest, "SP Bán Chạy");

    /* ======================================
       2) Sheet: SẢN PHẨM SẮP HẾT HÀNG
       ====================================== */

    const lowHeader = ["Tên sản phẩm", "Số lượng tồn", "Giá bán"];

    const lowRows = lowStockProducts.value.map((p) => [
      p.tenSanPham,
      p.soLuongTon,
      formatCurrency(p.giaBan),
    ]);

    const wsLow = XLSX.utils.aoa_to_sheet([lowHeader, ...lowRows]);
    XLSX.utils.book_append_sheet(wb, wsLow, "SP Sắp Hết Hàng");

    /* ======================
       3) Sheet: TỔNG QUAN
       ====================== */

    const overviewHeader = [
      "Loại thống kê",
      "Doanh thu",
      "Sản phẩm đã bán",
      "Đơn thành công",
      "Đơn hủy",
      "Đơn trả",
    ];

    // Chỉ xuất loại người dùng đang chọn (NGAY – TUAN – …)
    const filter = selectedFilter.value.toUpperCase();

    const currentStat =
      overviewStats.value[filter] || overviewStats.value.TUY_CHINH;

    const overviewRows = [
      [
        currentStat.title,
        currentStat.total,
        currentStat.sanPham,
        currentStat.donThanhCong,
        currentStat.donHuy,
        currentStat.donTra,
      ],
    ];

    const wsOverview = XLSX.utils.aoa_to_sheet([
      overviewHeader,
      ...overviewRows,
    ]);
    XLSX.utils.book_append_sheet(wb, wsOverview, "Tổng Quan");

    /* ======================
        Xuất file
       ====================== */

    XLSX.writeFile(wb, "thong_ke.xlsx");
    notify.success("Xuất Excel thống kê thành công!");
  } catch (error) {
    console.error("Lỗi xuất Excel:", error);
    notify.error("Xuất Excel thống kê thất bại!");
  }
};
const growthChartData = computed(() => {
  if (!growthStats.value || growthStats.value.length === 0) {
    return {
      labels: ["Không có dữ liệu"],
      datasets: [
        {
          label: "Tăng trưởng",
          data: [0],
          backgroundColor: "#f39c12",
          borderColor: "#f39c12",
        },
      ],
    };
  }

  return {
    labels: growthStats.value.map((s) => s.label),
    datasets: [
      {
        label: "Tăng trưởng (%)",
        data: growthStats.value.map((s) => s.percentage),
        borderColor: "#0dcaf0",
        backgroundColor: "rgba(13, 202, 240, 0.3)",
        fill: true,
        tension: 0.3,
      },
    ],
  };
});
const growthChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: { beginAtZero: true },
  },
};
</script>

<template>
  <div class="container-fluid mt-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-3 px-4">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-dark mb-1">Thống kê</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-6">
        <div
          class="card card-stats text-white"
          :class="overviewStats.HOM_NAY.colorClass"
        >
          <div class="card-body text-center">
            <div class="card-icon mb-2">
              <i
                class="fas fa-3x text-white"
                :class="overviewStats.HOM_NAY.icon"
                style="opacity: 0.5"
              ></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">
              {{ overviewStats.HOM_NAY.title }}
            </h4>
            <h2 class="fw-bold text-white mb-2">
              {{ overviewStats.HOM_NAY.total }}
            </h2>
            <div
              class="row g-2 justify-content-center"
              style="font-size: 0.9rem"
            >
              <div class="col-auto">
                Sản phẩm: {{ overviewStats.HOM_NAY.sanPham }}
              </div>
              <div class="col-auto">
                Đơn thành công: {{ overviewStats.HOM_NAY.donThanhCong }}
              </div>
              <div class="col-auto">
                Đơn hủy: {{ overviewStats.HOM_NAY.donHuy }}
              </div>
              <div class="col-auto">
                Đơn trả: {{ overviewStats.HOM_NAY.donTra }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div
          class="card card-stats text-white"
          :class="overviewStats.TUAN_NAY.colorClass"
        >
          <div class="card-body text-center">
            <div class="card-icon mb-2">
              <i
                class="fas fa-3x text-white"
                :class="overviewStats.TUAN_NAY.icon"
                style="opacity: 0.5"
              ></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">
              {{ overviewStats.TUAN_NAY.title }}
            </h4>
            <h2 class="fw-bold text-white mb-2">
              {{ overviewStats.TUAN_NAY.total }}
            </h2>
            <div
              class="row g-2 justify-content-center"
              style="font-size: 0.9rem"
            >
              <div class="col-auto">
                Sản phẩm: {{ overviewStats.TUAN_NAY.sanPham }}
              </div>
              <div class="col-auto">
                Đơn thành công: {{ overviewStats.TUAN_NAY.donThanhCong }}
              </div>
              <div class="col-auto">
                Đơn hủy: {{ overviewStats.TUAN_NAY.donHuy }}
              </div>
              <div class="col-auto">
                Đơn trả: {{ overviewStats.TUAN_NAY.donTra }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col-md-6">
        <div
          class="card card-stats text-white"
          :class="overviewStats.THANG_NAY.colorClass"
        >
          <div class="card-body text-center">
            <div class="card-icon mb-2">
              <i
                class="fas fa-3x text-white"
                :class="overviewStats.THANG_NAY.icon"
                style="opacity: 0.5"
              ></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">
              {{ overviewStats.THANG_NAY.title }}
            </h4>
            <h2 class="fw-bold text-white mb-2">
              {{ overviewStats.THANG_NAY.total }}
            </h2>
            <div
              class="row g-2 justify-content-center"
              style="font-size: 0.9rem"
            >
              <div class="col-auto">
                Sản phẩm: {{ overviewStats.THANG_NAY.sanPham }}
              </div>
              <div class="col-auto">
                Đơn thành công: {{ overviewStats.THANG_NAY.donThanhCong }}
              </div>
              <div class="col-auto">
                Đơn hủy: {{ overviewStats.THANG_NAY.donHuy }}
              </div>
              <div class="col-auto">
                Đơn trả: {{ overviewStats.THANG_NAY.donTra }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
        <div
          class="card card-stats text-white"
          :class="overviewStats.NAM_NAY.colorClass"
        >
          <div class="card-body text-center">
            <div class="card-icon mb-2">
              <i
                class="fas fa-3x text-white"
                :class="overviewStats.NAM_NAY.icon"
                style="opacity: 0.5"
              ></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">
              {{ overviewStats.NAM_NAY.title }}
            </h4>
            <h2 class="fw-bold text-white mb-2">
              {{ overviewStats.NAM_NAY.total }}
            </h2>
            <div
              class="row g-2 justify-content-center"
              style="font-size: 0.9rem"
            >
              <div class="col-auto">
                Sản phẩm: {{ overviewStats.NAM_NAY.sanPham }}
              </div>
              <div class="col-auto">
                Đơn thành công: {{ overviewStats.NAM_NAY.donThanhCong }}
              </div>
              <div class="col-auto">
                Đơn hủy: {{ overviewStats.NAM_NAY.donHuy }}
              </div>
              <div class="col-auto">
                Đơn trả: {{ overviewStats.NAM_NAY.donTra }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row" v-if="showCustomCard">
      <div class="col-12">
        <div
          class="card card-stats text-white"
          :class="overviewStats.TUY_CHINH.colorClass"
        >
          <div class="card-body text-center">
            <div class="card-icon mb-2">
              <i
                class="fas fa-3x text-white"
                :class="overviewStats.TUY_CHINH.icon"
                style="opacity: 0.5"
              ></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">
              {{ overviewStats.TUY_CHINH.title }}
            </h4>
            <h2 class="fw-bold text-white mb-2">
              {{ overviewStats.TUY_CHINH.total }}
            </h2>
            <div
              class="row g-2 justify-content-center"
              style="font-size: 0.9rem"
            >
              <div class="col-auto">
                Sản phẩm: {{ overviewStats.TUY_CHINH.sanPham }}
              </div>
              <div class="col-auto">
                Đơn thành công: {{ overviewStats.TUY_CHINH.donThanhCong }}
              </div>
              <div class="col-auto">
                Đơn hủy: {{ overviewStats.TUY_CHINH.donHuy }}
              </div>
              <div class="col-auto">
                Đơn trả: {{ overviewStats.TUY_CHINH.donTra }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body p-3">
        <h5 class="mb-2 fw-bold">Bộ lọc</h5>
        <div class="d-flex justify-content-between align-items-center">
          <div class="d-flex flex-wrap align-items-center gap-3">
            <div class="btn-group" role="group">
              <button
                type="button"
                class="btn"
                :class="
                  selectedFilter === 'NGÀY' ? 'btn-warning' : 'btn-outline-dark'
                "
                @click="selectFilter('NGAY')"
              >
                NGÀY
              </button>
              <button
                type="button"
                class="btn"
                :class="
                  selectedFilter === 'TUẦN' ? 'btn-warning' : 'btn-outline-dark'
                "
                @click="selectFilter('TUAN')"
              >
                TUẦN
              </button>
              <button
                type="button"
                class="btn"
                :class="
                  selectedFilter === 'THÁNG'
                    ? 'btn-warning'
                    : 'btn-outline-dark'
                "
                @click="selectFilter('THANG')"
              >
                THÁNG
              </button>
              <button
                type="button"
                class="btn"
                :class="
                  selectedFilter === 'NĂM' ? 'btn-warning' : 'btn-outline-dark'
                "
                @click="selectFilter('NAM')"
              >
                NĂM
              </button>
              <button
                type="button"
                class="btn"
                :class="
                  selectedFilter === 'TUY_CHINH'
                    ? 'btn-warning'
                    : 'btn-outline-dark'
                "
                @click="selectFilter('TUY_CHINH')"
              >
                TÙY CHỈNH
              </button>
            </div>
            <div
              v-if="selectedFilter === 'TUY_CHINH'"
              class="d-flex flex-wrap gap-2"
            >
              <div class="input-group input-group-sm" style="width: 180px">
                <span class="input-group-text">Từ ngày</span>
                <input
                  type="date"
                  class="form-control"
                  v-model="customStartDate"
                />
              </div>
              <div class="input-group input-group-sm" style="width: 180px">
                <span class="input-group-text">Đến ngày</span>
                <input
                  type="date"
                  class="form-control"
                  v-model="customEndDate"
                />
              </div>
            </div>
          </div>
          <div>
            <button
              class="btn btn-outline-success"
              @click="handleExportThongKeExcel"
            >
              <i class="fas fa-file-excel me-1"></i> EXPORT TO EXCEL
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-lg-8">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header">
            <h5 class="mb-0 fw-bold">
              Danh sách sản phẩm bán chạy theo
              {{ selectedFilter.toLowerCase() }}
            </h5>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr style="background-color: #f39c12; color: white">
                    <th class="text-center" style="width: 80px">Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Giá tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="isLoading">
                    <td colspan="4" class="text-center p-3">
                      <div
                        class="spinner-border spinner-border-sm"
                        role="status"
                      ></div>
                      <span class="ms-2">Đang tải...</span>
                    </td>
                  </tr>
                  <tr v-else-if="bestSellingProducts.length === 0">
                    <td colspan="4" class="text-center p-3">
                      Không có dữ liệu
                    </td>
                  </tr>
                  <tr
                    v-else
                    v-for="product in bestSellingProducts"
                    :key="product.tenSanPham + product.giaTien"
                  >
                    <td class="text-center">
                      <img
                        :src="product.hinhAnh || '/src/assets/img/logoWeb.png'"
                        alt="product-img"
                        style="
                          width: 50px;
                          height: 50px;
                          object-fit: cover;
                          border-radius: 4px;
                        "
                      />
                    </td>
                    <td>{{ product.tenSanPham }}</td>
                    <td class="text-center">{{ product.soLuongBan }}</td>
                    <td class="text-end fw-bold">
                      {{ formatCurrency(product.giaTien) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div
              class="card-footer d-flex justify-content-between align-items-center"
            >
              <div class="d-flex align-items-center gap-2">
                <span class="text-muted" style="font-size: 0.9rem">Xem</span>
                <select
                  class="form-select form-select-sm"
                  v-model="itemsPerPage"
                  style="width: 75px"
                >
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="25">25</option>
                </select>
                <span class="text-muted" style="font-size: 0.9rem"
                  >sản phẩm</span
                >
              </div>
              <nav v-if="totalPages > 0">
                <ul class="pagination pagination-sm mb-0">
                  <li
                    class="page-item"
                    :class="{ disabled: currentPage === 0 }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="changePage(currentPage - 1)"
                      >&laquo;</a
                    >
                  </li>
                  <li
                    v-for="page in totalPages"
                    :key="page"
                    class="page-item"
                    :class="{ active: page - 1 === currentPage }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="changePage(page - 1)"
                      >{{ page }}</a
                    >
                  </li>
                  <li
                    class="page-item"
                    :class="{ disabled: currentPage === totalPages - 1 }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="changePage(currentPage + 1)"
                      >&raquo;</a
                    >
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>

        <div class="card shadow-sm border-0 mb-4">
          <div
            class="card-header d-flex flex-wrap justify-content-between align-items-center"
          >
            <h5 class="mb-0 fw-bold">Danh sách sản phẩm sắp hết hàng</h5>
            <div class="d-flex align-items-center gap-2">
              <span class="text-nowrap">Số lượng dưới:</span>
              <input
                type="number"
                :value="10"
                class="form-control form-control-sm"
                style="width: 80px"
                disabled
              />
            </div>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr style="background-color: #f39c12; color: white">
                    <th class="text-center" style="width: 80px">Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Giá tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="isOverviewLoading">
                    <td colspan="4" class="text-center p-3">
                      <div
                        class="spinner-border spinner-border-sm"
                        role="status"
                      ></div>
                    </td>
                  </tr>
                  <tr v-else-if="lowStockProducts.length === 0">
                    <td colspan="4" class="text-center p-3">
                      Không có dữ liệu
                    </td>
                  </tr>
                  <tr
                    v-else
                    v-for="product in lowStockProducts"
                    :key="product.tenSanPham + product.giaBan"
                  >
                    <td class="text-center">
                      <img
                        :src="product.hinhAnh || '/src/assets/img/logoWeb.png'"
                        alt="product-img"
                        style="
                          width: 50px;
                          height: 50px;
                          object-fit: cover;
                          border-radius: 4px;
                        "
                      />
                    </td>
                    <td>{{ product.tenSanPham }}</td>
                    <td class="text-center fw-bold text-danger">
                      {{ product.soLuongTon }}
                    </td>
                    <td class="text-end fw-bold">
                      {{ formatCurrency(product.giaBan) }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div
              class="card-footer d-flex justify-content-between align-items-center"
            >
              <div class="d-flex align-items-center gap-2">
                <span class="text-muted" style="font-size: 0.9rem">Xem</span>
                <select
                  class="form-select form-select-sm"
                  v-model="lowStockItemsPerPage"
                  style="width: 75px"
                >
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="25">25</option>
                </select>
                <span class="text-muted" style="font-size: 0.9rem"
                  >sản phẩm</span
                >
              </div>
              <nav v-if="lowStockTotalPages > 0">
                <ul class="pagination pagination-sm mb-0">
                  <li
                    class="page-item"
                    :class="{ disabled: lowStockCurrentPage === 0 }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="
                        changeLowStockPage(lowStockCurrentPage - 1)
                      "
                      >&laquo;</a
                    >
                  </li>
                  <li
                    v-for="page in lowStockTotalPages"
                    :key="page"
                    class="page-item"
                    :class="{ active: page - 1 === lowStockCurrentPage }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="changeLowStockPage(page - 1)"
                      >{{ page }}</a
                    >
                  </li>
                  <li
                    class="page-item"
                    :class="{
                      disabled: lowStockCurrentPage === lowStockTotalPages - 1,
                    }"
                  >
                    <a
                      class="page-link"
                      href="#"
                      @click.prevent="
                        changeLowStockPage(lowStockCurrentPage + 1)
                      "
                      >&raquo;</a
                    >
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header">
            <h5 class="mb-0 fw-bold">
              Biểu đồ trạng thái {{ selectedFilter.toLowerCase() }}
            </h5>
          </div>
          <div class="card-body">
            <div style="height: 250px">
              <Doughnut :data="pieChartData" :options="pieChartOptions" />
            </div>
            <div class="mt-3 row" style="font-size: 0.9rem">
              <div
                v-for="item in pieChartLegend"
                :key="item.label"
                class="col-6 mb-1"
              >
                <div class="d-flex align-items-center justify-content-between">
                  <div class="d-flex align-items-center" style="min-width: 0">
                    <span
                      :style="{
                        backgroundColor: item.color,
                        width: '15px',
                        height: '15px',
                        display: 'inline-block',
                        marginRight: '8px',
                        borderRadius: '3px',
                        flexShrink: 0,
                      }"
                    ></span>
                    <span class="text-truncate" :title="item.label">{{
                      item.label
                    }}</span>
                  </div>
                  <span class="fw-bold ms-2">{{ item.percentage }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div
          class="card shadow-sm border-0"
          style="background-color: #212529; color: white"
        >
          <div class="card-header" style="border-bottom: 1px solid #444">
            <h5 class="mb-0 fw-bold">Tốc độ tăng trưởng của hàng</h5>
          </div>
          <div class="card-body p-0">
            <div v-if="isOverviewLoading" class="text-center p-3">
              <div
                class="spinner-border spinner-border-sm text-white"
                role="status"
              ></div>
              <!-- Biểu đồ đường-->
            </div>
            <div v-if="!isOverviewLoading" style="height: 250px">
              <Line :data="growthChartData" :options="growthChartOptions" />
            </div>
            <!-- Biểu đồ cột-->
            <!-- <div v-if="!isOverviewLoading" style="height: 250px">
              <Bar :data="growthChartData" :options="growthChartOptions" />
            </div> -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* (Giữ nguyên toàn bộ CSS cũ) */
.card.card-stats .card-body {
  padding: 1.25rem;
}
.btn-check:checked + .btn,
.btn.active,
.btn.show,
.btn:first-child:active,
:not(.btn-check) + .btn:active {
  background-color: #f39c12 !important;
  border-color: #f39c12 !important;
  color: white !important;
}
.btn-outline-dark {
  border-color: #ddd;
  color: #333;
}
.btn-outline-dark:hover {
  background-color: #f5f5ff;
  color: #333;
}
.input-group-text {
  background-color: #f8f9fa;
  border-color: #ddd;
}
.form-control {
  border-color: #ddd;
}
.pagination {
  align-items: center;
}
.pagination .page-item .page-link {
  border-radius: 50% !important;
  width: 2.25rem;
  height: 2.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 3px;
  padding: 0;
  font-size: 0.9rem;
}
.pagination .page-item.active .page-link {
  background-color: #f39c12;
  border-color: #f39c12;
  color: white;
}
.pagination .page-item.active .page-link {
  border-radius: 50% !important;
}
.card-footer .form-select-sm {
  font-size: 0.875rem;
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
  border-color: #ddd;
}
/* Màu tím của trạng thái hóa đơn */
.bg-purple {
  background-color: #6f42c1 !important;
  color: #fff !important;
}
</style>
