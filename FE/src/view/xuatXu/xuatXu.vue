<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, computed, onMounted } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import {
  getAllXuatXu,
  createXuatXu,
  updateXuatXu,
  deleteXuatXu,
} from "@/service/xuatXuService";

const xuatXuList = ref([]);
const selectedItem = ref({ id: "", ma: "", ten: "" });
const search = ref("");
const notify = useNotify();
let modalInstance = null;

// ✅ Load danh sách
const loadXuatXu = async () => {
  try {
    const res = await getAllXuatXu();
    xuatXuList.value = res.data;
  } catch (err) {
    console.error("❌ Lỗi khi tải xuất xứ:", err);
    notify.error("Không thể tải danh sách xuất xứ!");
  }
};

onMounted(loadXuatXu);

// ✅ computed lọc danh sách
const filteredXuatXu = computed(() => {
  const q = search.value.trim().toLowerCase();
  if (!q) return xuatXuList.value;
  return xuatXuList.value.filter(
    (i) =>
      (i.ma || "").toLowerCase().includes(q) ||
      (i.ten || "").toLowerCase().includes(q)
  );
});

const resetFilter = () => (search.value = "");

// ✅ Mở modal thêm mới
const openModalAdd = () => {
  selectedItem.value = { id: "", ma: "", ten: "" };
  const modalEl = document.getElementById("xuatXuModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// ✅ Mở modal sửa
const openModalEdit = (item) => {
  selectedItem.value = { ...item };
  const modalEl = document.getElementById("xuatXuModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// ✅ Kiểm tra dữ liệu
const validateItem = () => {
  if (!selectedItem.value.ma.trim()) {
    notify.error("Vui lòng nhập mã xuất xứ!");
    return false;
  }
  if (!selectedItem.value.ten.trim()) {
    notify.error("Vui lòng nhập tên xuất xứ!");
    return false;
  }
  return true;
};

// ✅ Lưu thêm / sửa
const saveItem = async () => {
  if (!validateItem()) return;
  try {
    if (selectedItem.value.id) {
      await updateXuatXu(selectedItem.value.id, selectedItem.value);
      notify.success("Cập nhật thành công!");
    } else {
      await createXuatXu(selectedItem.value);
      notify.success("Thêm mới thành công!");
    }
    modalInstance.hide();
    await loadXuatXu();
  } catch (err) {
    console.error("❌ Lỗi khi lưu xuất xứ:", err);
    notify.error("Có lỗi xảy ra khi lưu!");
  }
};

// ✅ Xác nhận lưu
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu?",
    text: "Bạn có chắc chắn muốn lưu thông tin này?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Hủy",
    confirmButtonColor: "#ffc107",
  });
  if (result.isConfirmed) await saveItem();
};

// ✅ Xóa xuất xứ
const confirmDelete = async (id) => {
  const result = await Swal.fire({
    title: "Xác nhận xóa?",
    text: "Bạn có chắc chắn muốn xóa xuất xứ này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Xóa",
    cancelButtonText: "Hủy",
    confirmButtonColor: "#dc3545",
  });
  if (result.isConfirmed) {
    try {
      await deleteXuatXu(id);
      notify.success("Xóa thành công!");
      await loadXuatXu();
    } catch (err) {
      console.error("❌ Lỗi khi xóa:", err);
      notify.error("Không thể xóa!");
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
          <h3 class="fw-bold text-warning mb-1">Quản lý Xuất Xứ</h3>
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
        <h4 class="card-title mb-0"><i class="fa fa-filter me-2"></i> Bộ lọc</h4>
      </div>
      <div class="card-body">
        <div class="row g-3 align-items-end">
          <div class="col-md-4">
            <label class="form-label fw-bold">Tìm kiếm</label>
            <input
              type="text"
              v-model="search"
              class="form-control"
              placeholder="Nhập mã hoặc tên xuất xứ..."
            />
          </div>
          <div class="col-md-4 d-flex gap-2 mt-3 mt-md-0">
            <button type="button" class="btn btn-dark flex-fill" @click="resetFilter">
              Đặt lại bộ lọc
            </button>
            <button
              type="button"
              class="btn btn-warning text-white flex-fill"
              @click="openModalAdd"
            >
              <i class="fa fa-plus me-1"></i>Thêm mới
            </button>
          </div>
        </div>
        <p class="mt-3 mb-0">
          Tổng số:
          <span class="text-warning fw-bold">{{ filteredXuatXu.length }}</span>
        </p>
      </div>
    </div>

    <!-- Danh sách -->
    <div class="card">
      <div class="card-header">
        <h4 class="card-title mb-0"><i class="fa fa-list me-2"></i>Danh sách Xuất Xứ</h4>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover text-center align-middle">
            <thead class="table-light">
              <tr>
                <th>STT</th>
                <th>Mã</th>
                <th>Tên</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in filteredXuatXu" :key="item.id">
                <td>{{ index + 1 }}</td>
                <td class="text-warning fw-bold">{{ item.ma }}</td>
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
              <tr v-if="filteredXuatXu.length === 0">
                <td colspan="4" class="text-muted py-3">Không có dữ liệu</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div class="modal fade" id="xuatXuModal" tabindex="-1" aria-hidden="true">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title">
              {{ selectedItem.id ? "Cập nhật Xuất Xứ" : "Thêm mới Xuất Xứ" }}
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>

          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label fw-bold">Mã</label>
              <input
                type="text"
                class="form-control"
                v-model="selectedItem.ma"
                placeholder="Nhập mã xuất xứ"
              />
            </div>
            <div>
              <label class="form-label fw-bold">Tên</label>
              <input
                type="text"
                class="form-control"
                v-model="selectedItem.ten"
                placeholder="Nhập tên xuất xứ"
              />
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
              Đóng
            </button>
            <button
              type="button"
              class="btn btn-warning text-white"
              @click="confirmSave"
            >
              Lưu
            </button>
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
