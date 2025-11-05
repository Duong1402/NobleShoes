<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, reactive, onMounted } from "vue";
import {
  searchHoaDon,
  getHoaDonById,
  updateHoaDon,
} from "@/service/HoaDonService";
import { useNotify } from "@/composables/useNotify";
// import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import { useRouter } from "vue-router";
const router = useRouter();
const hoaDonList = ref([]);
const filter = reactive({
  ma: "",
  tenKhachOrNhanVien: "",
  ngayTu: "",
  ngayDen: "",
  loaiDon: "",
  trangThai: "",
});
const tabs = ref([
  { label: "Tất cả", value: "" },
  { label: "Đã hủy", value: 0 },
  { label: "Chờ xác nhận", value: 1 },
  { label: "Đã xác nhận", value: 2 },
  { label: "Đang giao", value: 3 },
  { label: "Hoàn thành", value: 4 },
  { label: "Chờ thanh toán", value: 5 },
]);
const activeTab = ref("");
const handleTabClick = (tab) => {
  activeTab.value = tab.value; // đổi tab đang chọn
  filter.trangThai = tab.value; // gán vào bộ lọc
  loadHoaDon(0); // gọi lại API
};
const pagination = ref({
  page: 0,
  size: 10,
  totalPages: 0,
  totalElements: 0,
});
const notify = useNotify();

const currentPage = ref(1);
const itemsPerPage = ref(5);
const totalPages = ref(3);

let detailModalInstance = null;
const selectedHoaDon = ref(null);
const modalMode = ref("view");

const TRANG_THAI_HOA_DON = {
  0: { text: "Đã hủy", class: "bg-danger" },
  1: { text: "Chờ xác nhận", class: "bg-warning text-dark" },
  2: { text: "Đã xác nhận", class: "bg-info" },
  3: { text: "Đang giao", class: "bg-primary" },
  4: { text: "Hoàn thành", class: "bg-success" },
  5: { text: "Chờ thanh toán", class: "bg-secondary" },
};
const LOAI_HOA_DON = ["Online", "Tại cửa hàng"];

onMounted(() => {
  loadHoaDon();

  const modalEl = document.getElementById("detailModal");
  if (modalEl) {
    detailModalInstance = new Modal(modalEl);
  }
});

const loadHoaDon = async (page = 0) => {
  try {
    const params = {
      page: page,
      size: pagination.value.size,
    };
    if (filter.ma) params.ma = filter.ma.trim();
    if (filter.tenKhachOrNhanVien)
      params.tenKhachOrNhanVien = filter.tenKhachOrNhanVien.trim();
    if (filter.ngayTu) params.ngayTu = filter.ngayTu;
    if (filter.ngayDen) params.ngayDen = filter.ngayDen;
    if (filter.loaiDon) params.loaiDon = filter.loaiDon;
    if (filter.trangThai !== "") params.trangThai = filter.trangThai;

    const res = await searchHoaDon(params);
    const data = res.data;
    hoaDonList.value = data.content;
    pagination.value.page = data.number;
    pagination.value.totalPages = data.totalPages;
    pagination.value.totalElements = data.totalElements;
  } catch (err) {
    console.error("Lỗi khi tải danh sách hóa đơn:", err);
    notify.error("Tải dữ liệu hóa đơn thất bại!");
  }
};
const handleViewDetail = (id) => {
  router.push({ name: "ChiTietHD", params: { id } });
};

const handleEditDetail = (id) => {
  router.push({ name: "ChiTietHD", params: { id } });
};
// const handleViewDetail = async (id) => {
//   modalMode.value = "view";
//   await fetchAndShowModal(id);
// };

// const handleEditDetail = async (id) => {
//   modalMode.value = "edit";
//   await fetchAndShowModal(id);
// };

// const fetchAndShowModal = async (id) => {
//   try {
//     const res = await getHoaDonById(id);
//     selectedHoaDon.value = res.data;
//     detailModalInstance.show();
//   } catch (err) {
//     console.error("Lỗi khi lấy chi tiết hóa đơn:", err);
//     notify.error("Không thể tải chi tiết hóa đơn!");
//   }
// };

const handleUpdate = async () => {
  if (!selectedHoaDon.value) return;

  const result = await Swal.fire({
    title: "Xác nhận cập nhật?",
    text: "Bạn có chắc muốn lưu các thay đổi cho hóa đơn này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    try {
      const id = selectedHoaDon.value.id;

      const updateData = {
        trangThai: selectedHoaDon.value.trangThai,
        sdt: selectedHoaDon.value.sdt,
        diaChiGiaoHang: selectedHoaDon.value.diaChiGiaoHang,
      };

      await updateHoaDon(id, updateData);

      notify.success("Cập nhật hóa đơn thành công!");
      detailModalInstance.hide();
      await loadHoaDon(pagination.value.page);
    } catch (err) {
      console.error("Lỗi khi cập nhật hóa đơn:", err);
      notify.error("Cập nhật thất bại!");
    }
  }
};

const handleSearch = () => {
  loadHoaDon(0);
};
const handleReset = () => {
  filter.ma = "";
  filter.tenKhachOrNhanVien = "";
  filter.ngayTu = "";
  filter.ngayDen = "";
  filter.loaiDon = "";
  filter.trangThai = "";
  loadHoaDon(0);
};
const handlePageChange = (newPage) => {
  if (newPage >= 0 && newPage < pagination.value.totalPages) {
    loadHoaDon(newPage);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "N/A";
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
};
const formatCurrency = (value) => {
  if (value === null || value === undefined) return "0 ₫";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};
const getTrangThai = (status) => {
  return TRANG_THAI_HOA_DON[status] || { text: "Không rõ", class: "bg-dark" };
};
</script>

<template>
  <div class="container-fluid mt-4 px-5">
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
              <button type="button" class="btn btn-primary">📷 Quét mã</button>
              <button type="button" class="btn btn-primary">⬇️ Xuất file</button>
            </div>
            <div class="d-flex align-items-center gap-2">
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
            <div class="table-responsive">
              <table id="hoa-don-table" class="display table">
                <thead>
                  <tr class="text-center">
                    <th>STT</th>
                    <th>Mã hóa đơn</th>
                    <th>Tên khách hàng</th>
                    <th>Tên nhân viên</th>
                    <th>Ngày tạo</th>
                    <th>Tổng tiền</th>
                    <th>Loại đơn</th>
                    <th>Trạng thái</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="(hd, index) in hoaDonList"
                    :key="hd.id"
                    class="text-center"
                  >
                    <td>{{ pagination.page * pagination.size + index + 1 }}</td>
                    <td class="text-warning fw-bold">{{ hd.ma }}</td>
                    <td>{{ hd.tenKhachHang }}</td>
                    <td>{{ hd.tenNhanVien }}</td>
                    <td>{{ formatDate(hd.ngayTao) }}</td>
                    <td class="text-danger fw-bold">
                      {{ formatCurrency(hd.tongTien) }}
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

                    <td class="text-center">
                      <div class="d-flex justify-content-center gap-2">
                        <button
                          type="button"
                          class="btn btn-link btn-primary btn-lg p-0"
                          title="Xem chi tiết"
                          @click="handleViewDetail(hd.id)"
                        >
                          <i class="fa fa-eye"></i>
                        </button>
                        <button
                          type="button"
                          class="btn btn-link btn-warning btn-lg p-0"
                          title="Chỉnh sửa"
                          @click="handleEditDetail(hd.id)"
                        >
                          <i class="fa fa-edit"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="hoaDonList.length === 0">
                    <td colspan="9" class="text-center text-muted py-4">
                      Không tìm thấy dữ liệu
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Phân trang -->
    <div class="pagination">
      <div class="pagination-left">
        <label>Xem</label>
        <select v-model="itemsPerPage" class="select">
          <option value="5">5</option>
          <option value="10">10</option>
        </select>
        <span>Hoá đơn</span>
      </div>

      <div class="pagination-right">
        <button class="page-btn disabled">‹</button>
        <button class="page-btn active">1</button>
        <button class="page-btn">2</button>
        <button class="page-btn">3</button>
        <button class="page-btn">›</button>
      </div>
    </div>
          </div>
        </div>
      </div>
    </div>
    <!--<div
      class="modal fade"
      id="detailModal"
      tabindex="-1"
      aria-labelledby="detailModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content" v-if="selectedHoaDon">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title" id="detailModalLabel">
              <span v-if="modalMode === 'view'"
                >Chi tiết Hóa đơn: {{ selectedHoaDon.ma }}</span
              >
              <span v-if="modalMode === 'edit'"
                >Cập nhật Hóa đơn: {{ selectedHoaDon.ma }}</span
              >
            </h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
           Chi tiết 
          <div class="modal-body">
            <div class="row g-3">
              <h5 class="mb-0 mt-3">Thông tin chung</h5>
              <div class="col-md-6">
                <label class="form-label">Tên khách hàng</label>
                <input
                  type="text"
                  class="form-control"
                  :value="selectedHoaDon.tenKhachHang"
                  disabled
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Tên nhân viên</label>
                <input
                  type="text"
                  class="form-control"
                  :value="selectedHoaDon.tenNhanVien"
                  disabled
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Ngày tạo</label>
                <input
                  type="text"
                  class="form-control"
                  :value="formatDate(selectedHoaDon.ngayTao)"
                  disabled
                />
              </div>
              <div class="col-md-6">
                <label class="form-label">Tổng tiền</label>
                <input
                  type="text"
                  class="form-control"
                  :value="formatCurrency(selectedHoaDon.tongTien)"
                  disabled
                />
              </div>

              <h5 class="mb-0 mt-4">Thông tin vận hành (Có thể sửa)</h5>
              <div class="col-md-6">
                <label class="form-label fw-bold">Trạng thái đơn hàng</label>
                <select
                  class="form-select"
                  v-model="selectedHoaDon.trangThai"
                  :disabled="modalMode === 'view'"
                >
                  <option
                    v-for="(value, key) in TRANG_THAI_HOA_DON"
                    :key="key"
                    :value="key"
                  >
                    {{ value.text }}
                  </option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label">Số điện thoại giao hàng</label>
                <input
                  type="text"
                  class="form-control"
                  v-model="selectedHoaDon.sdt"
                  :disabled="modalMode === 'view'"
                />
              </div>
              <div class="col-12">
                <label class="form-label">Địa chỉ giao hàng</label>
                <textarea
                  class="form-control"
                  rows="2"
                  v-model="selectedHoaDon.diaChiGiaoHang"
                  :disabled="modalMode === 'view'"
                ></textarea>
              </div>

              <h5 class="mb-1 mt-4">Danh sách sản phẩm</h5>
              <div class="col-12">
                <table class="table table-sm">
                  <thead class="table-light">
                    <tr>
                      <th scope="col">Tên sản phẩm</th>
                      <th scope="col" class="text-center">Số lượng</th>
                      <th scope="col" class="text-end">Đơn giá</th>
                      <th scope="col" class="text-end">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(item, index) in selectedHoaDon.chiTietSanPham"
                      :key="index"
                    >
                      <td>{{ item.tenSanPham }}</td>
                      <td class="text-center">{{ item.soLuong }}</td>
                      <td class="text-end">
                        {{ formatCurrency(item.donGia) }}
                      </td>
                      <td class="text-end fw-bold">
                        {{ formatCurrency(item.thanhTien) }}
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-secondary"
              data-bs-dismiss="modal"
            >
              Đóng
            </button>
            <button
              type="button"
              class="btn btn-warning text-white"
              @click="handleUpdate"
              v-if="modalMode === 'edit'"
            >
              Lưu thay đổi
            </button>
          </div>
        </div>
      </div> 
    </div> -->
    
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
  white-space: nowrap;
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
</style>
