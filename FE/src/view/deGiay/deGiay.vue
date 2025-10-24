<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import {
  getAllDeGiay,
  createDeGiay,
  updateDeGiay,
  deleteDeGiay,
} from "@/service/DeGiayService";

const deGiay = ref([]);
const notify = useNotify();
const selectedDeGiay = ref({ id: "", ma: "", ten: "" });
let modalInstance = null;

// --- Bộ lọc ---
const search = ref("");

// --- Load danh sách ---
const loadDeGiay = async () => {
  try {
    const res = await getAllDeGiay();
    deGiay.value = res.data || [];
  } catch (err) {
    console.error("❌ Lỗi khi tải đế giày:", err);
    notify.error("Không thể tải dữ liệu!");
  }
};

onMounted(loadDeGiay);

// --- Bộ lọc tìm kiếm ---
const filteredDeGiay = computed(() => {
  const q = search.value.trim().toLowerCase();
  return !q
    ? deGiay.value
    : deGiay.value.filter(
        (i) =>
          i.ma?.toLowerCase().includes(q) ||
          i.ten?.toLowerCase().includes(q)
      );
});

const resetFilter = () => {
  search.value = "";
};

// --- Modal thêm ---
const openModalAdd = () => {
  selectedDeGiay.value = { id: "", ma: "", ten: "" };
  const modalEl = document.getElementById("detailModal");
  modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// --- Modal sửa ---
const editItem = (item) => {
  selectedDeGiay.value = { ...item };
  const modalEl = document.getElementById("detailModal");
  modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// --- Validate form ---
const validateForm = () => {
  if (!selectedDeGiay.value.ma?.trim()) {
    notify.warning("Mã đế giày không được để trống!");
    return false;
  }
  if (!selectedDeGiay.value.ten?.trim()) {
    notify.warning("Tên đế giày không được để trống!");
    return false;
  }
  return true;
};

// --- Lưu thêm / sửa ---
const saveItem = async () => {
  if (!validateForm()) return;
  try {
    if (selectedDeGiay.value.id) {
      await updateDeGiay(selectedDeGiay.value.id, selectedDeGiay.value);
      notify.success("Cập nhật thành công!");
    } else {
      await createDeGiay(selectedDeGiay.value);
      notify.success("Thêm mới thành công!");
    }
    modalInstance.hide();
    await loadDeGiay();
  } catch (err) {
    console.error("❌ Lỗi khi lưu đế giày:", err);
    notify.error("Có lỗi xảy ra khi lưu dữ liệu!");
  }
};

// --- Xác nhận lưu ---
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu?",
    text: "Bạn có chắc muốn lưu thông tin này?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Hủy",
    confirmButtonColor: "#ffc107",
  });
  if (result.isConfirmed) saveItem();
};

// --- Xác nhận xóa ---
const confirmDelete = async (id) => {
  const result = await Swal.fire({
    title: "Xác nhận xóa?",
    text: "Bạn có chắc chắn muốn xóa đế giày này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Xóa",
    cancelButtonText: "Hủy",
    confirmButtonColor: "#dc3545",
  });
  if (result.isConfirmed) {
    try {
      await deleteDeGiay(id);
      notify.success("Đã xóa thành công!");
      await loadDeGiay();
    } catch (err) {
      console.error("❌ Lỗi khi xóa:", err);
      notify.error("Không thể xóa đế giày!");
    }
  }
};
</script>

<template>
  <div class="container-fluid mt-4 px-5">
    <!-- Header -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3 d-flex justify-content-between align-items-center">
        <div>
          <h3 class="fw-bold text-warning mb-1">Quản lý Đế Giày</h3>
          <Breadcrumb class="mt-2 mb-0" />
        </div>
        <button class="btn btn-warning text-white" @click="openModalAdd">
          <i class="fa fa-plus me-2"></i>Thêm mới
        </button>
      </div>
    </div>

    <!-- Bộ lọc -->
    <div class="card mb-4">
      <div class="card-header">
        <h5 class="card-title"><i class="fa fa-filter me-2"></i>Bộ lọc</h5>
      </div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label fw-bold">Tìm kiếm</label>
            <input
              v-model="search"
              type="text"
              class="form-control"
              placeholder="Nhập mã hoặc tên đế giày..."
            />
          </div>
        </div>
        <div class="d-flex justify-content-between align-items-center mt-4">
          <p class="mb-0">
            Tổng số:
            <span class="text-warning fw-bold">{{ filteredDeGiay.length }}</span>
          </p>
          <div class="d-flex gap-2">
            <button class="btn btn-dark" @click="resetFilter">Đặt lại</button>
            <button class="btn btn-warning text-white" @click="openModalAdd">
              Thêm đế giày
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Danh sách -->
    <div class="card">
      <div class="card-header">
        <h5 class="card-title mb-0"><i class="fa fa-list me-2"></i>Danh sách Đế Giày</h5>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-bordered align-middle text-center">
            <thead class="table-light">
              <tr>
                <th>STT</th>
                <th>Mã</th>
                <th>Tên</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in filteredDeGiay" :key="item.id">
                <td>{{ index + 1 }}</td>
                <td class="fw-bold text-warning">{{ item.ma }}</td>
                <td>{{ item.ten }}</td>
                <td class="text-center">
                  <div class="d-flex justify-content-center align-items-center gap-2">
                    <!-- Show detail icon -->
                    <button class="btn btn-link text-info btn-lg p-0" @click="editItem(item)" title="Xem chi tiết">
                      <i class="fa fa-eye"></i>
                    </button>

                    <!-- Xóa ẩn đi: không hiển thị nút xóa -->
                  </div>
                </td>
              </tr>
              <tr v-if="filteredDeGiay.length === 0">
                <td colspan="4" class="text-muted py-3">Không có dữ liệu</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="detailModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">
              {{ selectedDeGiay.id ? "Cập nhật Đế Giày" : "Thêm mới Đế Giày" }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="confirmSave">
              <div class="mb-3">
                <label class="form-label fw-bold">Mã</label>
                <input v-model="selectedDeGiay.ma" type="text" class="form-control" placeholder="Nhập mã đế giày" />
              </div>
              <div class="mb-3">
                <label class="form-label fw-bold">Tên</label>
                <input v-model="selectedDeGiay.ten" type="text" class="form-control" placeholder="Nhập tên đế giày" />
              </div>
              <div class="text-end">
                <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">
                  Hủy
                </button>
                <button type="submit" class="btn btn-warning text-white">
                  {{ selectedDeGiay.id ? "Lưu thay đổi" : "Thêm mới" }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.table td,
.table th {
  vertical-align: middle;
}
</style>




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