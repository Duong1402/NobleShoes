<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import {
  searchHoaDon,
  getHoaDonById,
  updateHoaDon,
} from "@/service/HoaDonService";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
import QRCode from "qrcode";
import * as XLSX from "xlsx";
import { Html5Qrcode } from "html5-qrcode";

const router = useRouter();
const notify = useNotify();

const LOAI_HOA_DON = ["Online", "Tại cửa hàng"];

const getTodayDate = () => {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};

const TRANG_THAI_HOA_DON = {
  0: { text: "Chờ thanh toán", class: "bg-warning text-dark" },
  1: { text: "Chờ xác nhận", class: "bg-secondary" },
  2: { text: "Đã xác nhận", class: "bg-info" },
  3: { text: "Đang giao", class: "bg-primary" },
  4: { text: "Hoàn thành", class: "bg-success" },
  5: { text: "Đã hủy", class: "bg-danger" },
};

const tabs = ref([
  { label: "Tất cả", value: "" },
  { label: "Chờ thanh toán", value: 0 },
  { label: "Chờ xác nhận", value: 1 },
  { label: "Đã xác nhận", value: 2 },
  { label: "Đang giao", value: 3 },
  { label: "Hoàn thành", value: 4 },
  { label: "Đã hủy", value: 5 },
]);

const activeTab = ref("");

const handleTabClick = (tab) => {
  activeTab.value = tab.value;
  filter.trangThai = tab.value;
  loadHoaDon(0);
};

const hoaDonList = ref([]);
const pagination = ref({ page: 0, size: 10, totalPages: 0, totalElements: 0 });

const filter = reactive({
  ma: "",
  tenKhachOrNhanVien: "",
  sdt: "",
  ngayTu: "",
  ngayDen: "",
  loaiDon: "",
  trangThai: "",
});

onMounted(() => {
  const todayStr = getTodayDate();
  filter.ngayTu = todayStr;
  filter.ngayDen = todayStr;

  loadHoaDon();
});

const loadHoaDon = async (page = 0) => {
  try {
    const params = { page, size: pagination.value.size };
    if (filter.ma) params.ma = filter.ma.trim();
    if (filter.sdt) params.sdt = filter.sdt.trim();
    if (filter.tenKhachOrNhanVien)
      params.tenKhachOrNhanVien = filter.tenKhachOrNhanVien.trim();
    if (filter.ngayTu) params.ngayTu = filter.ngayTu;
    if (filter.ngayDen) params.ngayDen = filter.ngayDen;
    if (filter.loaiDon && filter.loaiDon.trim() !== "") {
      const inputClean = filter.loaiDon.trim().toLowerCase();
      const standardValue = LOAI_HOA_DON.find(
        (item) => item.toLowerCase() === inputClean
      );
      params.loaiDon = standardValue || filter.loaiDon.trim();
    }
    if (filter.trangThai !== "") params.trangThai = filter.trangThai;

    const res = await searchHoaDon(params);
    const data = res.data;

    hoaDonList.value = Array.isArray(data.content) ? data.content : [];
    pagination.value.page = data.number ?? 0;
    pagination.value.totalElements =
      data.totalElements ?? hoaDonList.value.length;
    pagination.value.totalPages = data.totalPages ?? 1;
  } catch (err) {
    console.error("Lỗi tải hóa đơn:", err);
    notify.error("Không tải được danh sách hóa đơn!");
  }
};

const formatDate = (d) => {
  if (!d) return "N/A";
  const [y, m, day] = d.split("-");
  return `${day}/${m}/${y}`;
};

const formatCurrency = (value) =>
  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(
    value
  );

const getTrangThai = (stt) =>
  TRANG_THAI_HOA_DON[stt] || { text: "Không rõ", class: "bg-dark" };

const handleViewDetail = (id) =>
  router.push({ name: "ChiTietHD", params: { id } });

const handleSearch = () => loadHoaDon(0);

const handleReset = () => {
  filter.ma = "";
  filter.sdt = "";
  filter.tenKhachOrNhanVien = "";
  filter.loaiDon = "";
  filter.trangThai = "";

  const todayStr = getTodayDate();
  filter.ngayTu = todayStr;
  filter.ngayDen = todayStr;

  loadHoaDon(0);
};

const handlePageChange = (newPage) => {
  if (newPage >= 0 && newPage < pagination.value.totalPages)
    loadHoaDon(newPage);
};

const handleExportExcel = () => {
  if (!hoaDonList.value.length) return notify.warning("Không có dữ liệu!");

  const header = [
    "Mã",
    "Khách hàng",
    "SĐT",
    "Ngày",
    "Tổng",
    "Loại",
    "Trạng thái",
  ];
  const rows = hoaDonList.value.map((hd) => [
    hd.ma,
    hd.tenKhachHang,
    hd.sdt,
    formatDate(hd.ngayTao),
    hd.tongTienSauGiam + " ₫",
    hd.loaiHoaDon,
    getTrangThai(hd.trangThai).text,
  ]);

  const ws = XLSX.utils.aoa_to_sheet([header, ...rows]);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Danh sách");
  XLSX.writeFile(wb, "hoa_don.xlsx");
  notify.success("Xuất Excel thành công!");
};

const itemsPerPage = ref(10);

watch(itemsPerPage, (newSize) => {
  pagination.value.size = Number(newSize);
  pagination.value.page = 0;
  loadHoaDon(0);
});
</script>

<template>
  <div class="container-fluid mt-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý Hóa đơn</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ Lọc</h4>
      </div>
      <div class="card-body">
        <form @submit.prevent="handleSearch">
          <div class="row g-3">
            <div class="col-md-3">
              <label class="form-label fw-bold">Mã hóa đơn</label>
              <input
                type="text"
                class="form-control"
                placeholder="Nhập mã hóa đơn..."
                v-model="filter.ma"
              />
            </div>
            <div class="col-md-3">
              <label class="form-label fw-bold">Loại đơn</label>
              <select class="form-select" v-model="filter.loaiDon">
                <option value="">Tất cả</option>
                <option v-for="loai in LOAI_HOA_DON" :key="loai" :value="loai">
                  {{ loai }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label fw-bold">Từ ngày</label>
              <input type="date" class="form-control" v-model="filter.ngayTu" />
            </div>
            <div class="col-md-3">
              <label class="form-label fw-bold">Đến ngày</label>
              <input
                type="date"
                class="form-control"
                v-model="filter.ngayDen"
              />
            </div>
          </div>
          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0">
              Tìm thấy
              <span class="text-warning fw-bold">{{
                pagination.totalElements
              }}</span>
              kết quả
            </p>

            <div class="d-flex align-items-center gap-2">
              <button
                type="button"
                class="btn btn-success"
                @click="handleScanQRCode"
              >
                <i class="fa fa-qrcode me-1"></i> Quét Mã
              </button>
              <button
                type="button"
                class="btn btn-primary"
                @click="handleExportExcel"
              >
                <i class="fa fa-file-excel me-1"></i> Xuất Excel
              </button>
              <button type="button" class="btn btn-dark" @click="handleReset">
                Đặt lại bộ lọc
              </button>
              <button type="submit" class="btn btn-warning text-white">
                <i class="fa fa-search me-1"></i> Tìm kiếm
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="card-header">
            <h4 class="card-title mb-0">Danh Sách Hóa Đơn</h4>
          </div>
          <div class="card-body">
            <div class="tabs">
              <button
                v-for="tab in tabs"
                :key="tab.value"
                class="tab"
                :class="{ active: activeTab === tab.value }"
                @click="handleTabClick(tab)"
              >
                {{ tab.label }}
              </button>
            </div>
            <div class="table-container">
              <div class="table-responsive">
                <table
                  id="hoa-don-table"
                  class="table table-bordered align-middle text-center custom-table"
                >
                  <thead class="table-light">
                    <tr>
                      <th>STT</th>
                      <th>Mã</th>
                      <th>Khách hàng</th>
                      <th>SDT</th>
                      <th>Nhân viên</th>
                      <th>Ngày tạo</th>
                      <th>Tổng tiền</th>
                      <th>Loại đơn</th>
                      <th>Trạng thái</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>

                  <tbody>
                    <tr v-for="(hd, index) in hoaDonList" :key="hd.id">
                      <td>
                        {{ pagination.page * pagination.size + index + 1 }}
                      </td>
                      <td class="text-warning fw-bold">{{ hd.ma }}</td>
                      <td>{{ hd.tenKhachHang }}</td>
                      <td>{{ hd.sdt }}</td>
                      <td>{{ hd.tenNhanVien }}</td>
                      <td>{{ formatDate(hd.ngayTao) }}</td>
                      <td class="text-danger fw-bold">
                        {{ formatCurrency(hd.tongTienSauGiam ?? hd.tongTien) }}
                      </td>
                      <td>{{ hd.loaiHoaDon }}</td>
                      <td>
                        <span
                          class="badge rounded-pill fs-6 px-3 py-2 text-white"
                          :class="getTrangThai(hd.trangThai).class"
                        >
                          {{ getTrangThai(hd.trangThai).text }}
                        </span>
                      </td>
                      <td>
                        <div class="d-flex justify-content-center gap-2">
                          <button
                            type="button"
                            class="btn btn-link text-primary btn-lg p-0"
                            title="Xem chi tiết"
                            @click="handleViewDetail(hd.id)"
                          >
                            <i class="fa fa-eye"></i>
                          </button>
                          <button
                            type="button"
                            class="btn btn-link text-success btn-lg p-0"
                            title="In hóa đơn"
                            @click="handlePrintPDF(hd.id)"
                          >
                            <i class="fa fa-print"></i>
                          </button>
                        </div>
                      </td>
                    </tr>

                    <tr v-if="hoaDonList.length === 0">
                      <td :colspan="10" class="text-center text-muted py-4">
                        Không tìm thấy dữ liệu
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <!-- Phân trang -->
            <div class="pagination">
              <div class="pagination-left">
                <label>Xem</label>
                <select v-model="itemsPerPage" class="select">
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="50">50</option>
                </select>
                <span>Hoá đơn</span>
              </div>

              <div class="pagination-right">
                <button
                  class="page-btn"
                  :class="{ disabled: pagination.page === 0 }"
                  @click="handlePageChange(pagination.page - 1)"
                >
                  ‹
                </button>

                <button
                  v-for="page in pagination.totalPages"
                  :key="page"
                  class="page-btn"
                  :class="{ active: pagination.page + 1 === page }"
                  @click="handlePageChange(page - 1)"
                >
                  {{ page }}
                </button>

                <button
                  class="page-btn"
                  :class="{
                    disabled: pagination.page + 1 === pagination.totalPages,
                  }"
                  @click="handlePageChange(pagination.page + 1)"
                >
                  ›
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.badge {
  min-width: 130px;
  font-weight: 500;
  text-transform: capitalize;
}

#hoa-don-table th,
#hoa-don-table td {
  vertical-align: middle;
  text-align: center;
  white-space: normal; /* Cho phép xuống dòng */
  word-wrap: break-word;
  font-size: 0.9rem;
}

#hoa-don-table td:nth-child(3),
#hoa-don-table td:nth-child(9) {
  font-size: 0.85rem;
}

#hoa-don-table .badge {
  font-size: 0.8rem;
  padding: 4px 6px;
  display: inline-block;
  white-space: normal;
}

/* Giữ chiều cao hàng đồng đều, trông đẹp hơn */
#hoa-don-table tr {
  height: 55px;
}

.page-item.active .page-link {
  background-color: #ffc107;
  border-color: #ffc107;
  color: #000;
  font-weight: 500;
}
.page-link {
  color: #ffc107;
}
.page-link:hover {
  color: #e0a800;
}
.page-item.disabled .page-link {
  color: #6c757d;
}

.tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #e5e7eb;
  margin-bottom: 10px;
  width: 100%;
  background: #fff;
  padding: 0;
}

.tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid #e5e7eb;
  margin-bottom: 10px;
  width: 100%;
  background: #fff;
  padding: 0;
}

.tab {
  position: relative;
  flex: 1;
  text-align: center;
  border: none;
  background: transparent;
  padding: 14px 0;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  color: #555;
  text-transform: uppercase;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab:hover {
  color: #007bff;
}

.tab::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #007bff;
  border-radius: 2px;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.25s ease;
}

.tab:hover::after,
.tab.active::after {
  transform: scaleX(1);
}

.tab.active {
  color: #007bff;
  font-weight: 700;
}

.tab:focus {
  outline: none;
}

.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  font-size: 14px;
}
.pagination-right {
  display: flex;
  gap: 6px;
}
.page-btn {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  border: 2px solid #d1d5db;
  background-color: transparent;
  color: #6b7280;
  font-weight: 600;
  text-align: center;
  line-height: 30px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.page-btn:hover {
  border-color: #f59e0b;
  color: #f59e0b;
  background-color: rgba(245, 158, 11, 0.1);
  transform: scale(1.05);
}

.page-btn.active {
  background-color: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
  border-color: #f59e0b;
}

.page-btn.disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
.page-btn:hover:not(.disabled):not(.active) {
  background: #f1f1f1;
}

.table-container {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.table-responsive {
  overflow-x: auto;
}

.custom-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: separate;
  border-spacing: 0;
  font-size: 0.9rem; /* cỡ mặc định toàn bảng */
}

/* Căn giữa, không xuống dòng, ẩn tràn */
.custom-table th,
.custom-table td {
  text-align: center;
  vertical-align: middle;
  padding: 10px 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Giảm cỡ chữ ở cột dễ tràn */
.custom-table td:nth-child(3), /* Khách hàng */
.custom-table td:nth-child(9) {
  /* Trạng thái */
  font-size: 0.85rem;
}

/* 👇 Giảm riêng cỡ chữ ở cột SĐT và Ngày tạo */
.custom-table td:nth-child(4), /* SĐT */
.custom-table td:nth-child(6) {
  /* Ngày tạo */
  font-size: 0.8rem; /* nhỏ hơn để vừa cột */
  white-space: nowrap;
  width: 110px; /* bạn có thể tăng thành 120px nếu thấy chật */
}

/* Badge nhỏ gọn hơn */
.custom-table .badge {
  font-size: 0.8rem;
  padding: 4px 8px;
  min-width: 70px;
}

/* Màu trạng thái giống giao diện Noble Shoes */
.badge[data-status="Hoàn Thành"],
.badge.hoan-thanh {
  background-color: #28a745 !important;
}

.badge[data-status="Đã Hủy"],
.badge.da-huy {
  background-color: #dc3545 !important;
}
</style>
