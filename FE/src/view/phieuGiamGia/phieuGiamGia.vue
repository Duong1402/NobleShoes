<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import {
  getAllPhieuGiamGia,
  // createphieuGiamGia,
  updatePhieuGiamGia,
  // deletephieuGiamGia,
} from "@/service/phieuGiamGiaService";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { useNotify } from "@/composables/useNotify";
import Decimal from "decimal.js";

const phieuGiamGia = ref([]);
const notify = useNotify();
const selectedphieuGiamGia = ref({
  ma: "",
  ten: "",
  ngayBatDau: "",
  ngayKetThuc: "",
  hinhThucGiamGia: false,
  giaTriGiam: 0,
  giaTriGiamToiThieu: 0,
  giaTriGiamToiDa: 0,
  trangThai: true,
  moTa: "",
});

const formatDate = (dateStr) => {
  if (!dateStr) return "";
  return new Date(dateStr).toLocaleDateString("vi-VN");
};

let modalInstance = null;

// Khi component mount, load danh sách phiếu giảm giá
onMounted(async () => {
  await loadphieuGiamGia();

  // Nếu URL có ?id=... thì tự động mở modal chi tiết
  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  if (id) {
    const p = phieuGiamGia.value.find((n) => String(n.id).trim() === id.trim());
    if (p) {
      setTimeout(() => editphieuGiamGia(p), 200);
    }
  }

  // Khi modal đóng (bấm nút X hoặc ra ngoài) → xóa ?id trên URL
  const modalEl = document.getElementById("detailModal");
  if (modalEl) {
    modalEl.addEventListener("hidden.bs.modal", () => {
      window.history.pushState({}, "", "/admin/phieu-giam-gia");
    });
  }
});

// Hàm load danh sách phiếu giảm giá
const loadphieuGiamGia = async () => {
  try {
    const res = await getAllPhieuGiamGia();
    phieuGiamGia.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tải danh sách phiếu giảm giá:", err);
  }
};

// ✅ Hàm load danh sách chức vụ

// Hàm mở modal chi tiết + update
const editphieuGiamGia = (p) => {
  // Deep copy để tránh ảnh hưởng đến list chính
  selectedphieuGiamGia.value = JSON.parse(JSON.stringify(p));
  window.history.pushState({}, "", `?id=${p.id}`);

  const modalEl = document.getElementById("detailModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// Hàm lưu cập nhật phiếu giảm giá
const savephieuGiamGia = async () => {
  try {
    // Chuẩn dữ liệu gửi về BE (chỉ cần ID chức vụ)
    const payload = {
      ...selectedphieuGiamGia.value,
    };

    await updatePhieuGiamGia(payload.id, payload);
    notify.success("Cập nhật thành công!");
    modalInstance.hide();
    await loadphieuGiamGia();
  } catch (err) {
    console.error("❌ Lỗi khi cập nhật phiếu giảm giá:", err);
    notify.error("Có lỗi khi cập nhật!");
  }
};

// Tạo hàm confirm
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    text: "Bạn có chắc chắn muốn cập nhật phiếu giảm giá này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107", // màu vàng giống btn
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    savephieuGiamGia(); // gọi hàm lưu
  }
};

const toggleTrangThai = async (p) => {
  const oldValue = p.trangThai;
  p.trangThai = !p.trangThai; // Đổi 1↔0 thay vì true/false

  try {
    // Tạo payload đầy đủ, tránh làm mất các field khác
    const payload = {
      ...p,
    };

    await updatePhieuGiamGia(p.id, payload);

    notify.success(
      `Đã chuyển sang trạng thái: ${
        p.trangThai ? "Còn hoạt động" : "Ngừng hoạt động"
      }`
    );
  } catch (err) {
    p.trangThai = oldValue; // revert lại nếu lỗi
    console.error("❌ Lỗi khi cập nhật trạng thái:", err);
    notify.error("Cập nhật trạng thái thất bại!");
  }
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
            <h3 class="fw-bold text-warning mb-1">Quản lý phiếu giảm giá</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <div class="d-flex align-items-center">
          <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ Lọc</h4>
        </div>
      </div>
      <div class="card-body">
        <form>
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">Tìm kiếm</label>
              <input
                type="text"
                class="form-control"
                placeholder="Mã, tên, email..."
                required
              />
            </div>

            <div class="col-md-4">
              <label class="form-label fw-bold">Trạng thái</label>
              <div class="d-flex mt-2">
                <div class="form-check me-3 custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    checked
                  />
                  <label class="form-check-label">Tất cả</label>
                </div>
                <div class="form-check me-3 custom-radio">
                  <input class="form-check-input" type="radio" name="status" />
                  <label class="form-check-label">Còn hoạt động</label>
                </div>
                <div class="form-check custom-radio">
                  <input class="form-check-input" type="radio" name="status" />
                  <label class="form-check-label">Ngừng hoạt động</label>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0">
              Tổng số phiếu giảm giá:
              <span class="text-warning fw-bold">{{
                phieuGiamGia.length
              }}</span>
            </p>
            <div class="d-flex align-items-center gap-2">
              <button type="reset" class="btn btn-dark">Đặt lại bộ lọc</button>
              <router-link
                to="/admin/phieu-giam-gia/add"
                class="btn btn-warning text-white"
              >
                Thêm phiếu giảm giá
              </router-link>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="row">
      <div class="col-md-12">
        <div class="card">
          <div class="card-header">
            <div class="d-flex align-items-center justify-content-between">
              <h4 class="card-title mb-0">Danh Sách phiếu giảm giá</h4>
            </div>
          </div>

          <div class="card-body">
            <div class="table-responsive">
              <table id="add-row" class="display table">
                <thead>
                  <tr style="text-align: center">
                    <th>STT</th>
                    <th>Mã</th>
                    <th>Họ tên</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Giá trị giảm</th>
                    <th>Trạng thái</th>
                    <th style="width: 10%">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(p, index) in phieuGiamGia" :key="p.id">
                    <td>{{ index + 1 }}</td>
                    <td>
                      {{ p.ma }}
                    </td>
                    <td>{{ p.ten }}</td>
                    <td>{{ formatDate(p.ngayBatDau) }}</td>
                    <td>{{ formatDate(p.ngayKetThuc) }}</td>
                    <td>
                      {{ p.giaTriGiam }} {{ p.hinhThucGiamGia ? "%" : "VNĐ" }}
                    </td>

                    <td>
                      <span
                        class="badge rounded-pill fs-6 px-3 status-badge"
                        :class="{
                          'text-white bg-warning': p.trangThai,
                          'text-white bg-danger': !p.trangThai,
                        }"
                      >
                        {{ p.trangThai ? "Còn hoạt động" : "Ngừng hoạt động" }}
                      </span>
                    </td>
                    <td class="text-center align-middle">
                      <div
                        class="d-flex justify-content-center align-items-center gap-2"
                      >
                        <!-- Toggle trạng thái -->
                        <div class="form-check form-switch m-0">
                          <input
                            class="form-check-input"
                            type="checkbox"
                            role="switch"
                            :id="'switch-' + p.id"
                            :checked="p.trangThai === true"
                            @change="toggleTrangThai(p)"
                            style="
                              cursor: pointer;
                              width: 2.4rem;
                              height: 1.3rem;
                            "
                          />
                        </div>

                        <!-- Nút cập nhật -->
                        <button
                          type="button"
                          class="btn btn-link btn-warning btn-lg p-0"
                          @click="editphieuGiamGia(p)"
                          title="Cập nhật phiếu giảm giá"
                        >
                          <i class="fa fa-edit"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Modal Detail + Update -->
          <div
            class="modal fade"
            id="detailModal"
            tabindex="-1"
            aria-labelledby="detailModalLabel"
            aria-hidden="true"
          >
            <div class="modal-dialog modal-dialog-centered modal-lg">
              <div class="modal-content">
                <div class="modal-header bg-warning text-white">
                  <h5 class="modal-title" id="detailModalLabel">
                    Chi tiết phiếu giảm giá
                  </h5>
                  <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="modal"
                    aria-label="Close"
                  ></button>
                </div>

                <div class="modal-body">
                  <div class="row g-3">
                    <div class="col-md-6">
                      <label class="form-label">Mã phiếu giảm giá</label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ma"
                        disabled
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Tên phiếu giảm giá</label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ten"
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Ngày bắt đầu</label>
                      <input
                        type="date"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ngayBatDau"
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Ngày kết thúc</label>
                      <input
                        type="date"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ngayKetThuc"
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Hình thức giảm giá</label>
                      <select
                        class="form-select"
                        v-model="selectedphieuGiamGia.hinhThucGiamGia"
                      >
                        <option :value="true">Phần trăm (%)</option>
                        <option :value="false">Theo tiền (VNĐ)</option>
                      </select>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiam"
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm tối thiểu</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiamToiThieu"
                      />
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm tối đa</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiamToiDa"
                      />
                    </div>

                    <div class="col-md-12">
                      <label class="form-label">Mô tả</label>
                      <textarea
                        class="form-control"
                        rows="2"
                        v-model="selectedphieuGiamGia.moTa"
                      ></textarea>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label d-block">Trạng thái</label>
                      <div class="form-check form-check-inline">
                        <input
                          class="form-check-input"
                          type="radio"
                          :value="true"
                          v-model="selectedphieuGiamGia.trangThai"
                        />
                        <label class="form-check-label">Còn hoạt động</label>
                      </div>
                      <div class="form-check form-check-inline">
                        <input
                          class="form-check-input"
                          type="radio"
                          :value="false"
                          v-model="selectedphieuGiamGia.trangThai"
                        />
                        <label class="form-check-label">Ngừng hoạt động</label>
                      </div>
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
                    @click="confirmSave"
                  >
                    Lưu thay đổi
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style>
.badge {
  transition: all 0.2s ease;
}
.badge:hover {
  transform: scale(1.05);
  opacity: 0.9;
}
/* Radio mặc định */
.form-check-input {
  transform: scale(1.2);
  cursor: pointer;
}

/* Khi hover vào label */
.form-check-label:hover {
  color: var(--custom-orange);
  transition: color 0.2s ease;
  cursor: pointer;
}

/* ✅ Khi chọn "Còn hoạt động" */
.activeStatus .form-check-input:checked {
  background-color: #28a745 !important; /* xanh lá */
  border-color: #28a745 !important;
}
.activeStatus .form-check-label {
  color: #28a745 !important;
  font-weight: 600;
}

/* ⛔ Khi chọn "Ngừng hoạt động" */
.inactiveStatus .form-check-input:checked {
  background-color: #dc3545 !important; /* đỏ */
  border-color: #dc3545 !important;
}
.inactiveStatus .form-check-label {
  color: #dc3545 !important;
  font-weight: 600;
}
.status-badge {
  display: inline-block;
  min-width: 140px;
  text-align: center;
  border-radius: 50px;
  font-weight: 500;
  padding: 6px 12px;
  transition: all 0.2s ease;
}

#add-row td {
  max-width: 150px; /* Đặt chiều rộng tối đa chung */
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
  text-align: cen;
  text-align: center;
}
.form-check-input {
  transform: scale(1.2);
  accent-color: #198754; /* xanh Bootstrap */
}
.form-switch .form-check-input:checked {
  background-color: #ffc107 !important; /* màu cam */
  border-color: #ffc107 !important;
}
.custom-radio .form-check-input:checked {
  background-color: #ffc107 !important; /* màu cam */
  border-color: #ffc107 !important;
}
</style>
