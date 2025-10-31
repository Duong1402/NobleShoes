<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed, watch } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import {
  getAllDanhMuc,
  createDanhMuc,
  updateDanhMuc,
  deleteDanhMuc,
} from "@/service/DanhMucService";

const danhMucs = ref([]);
const notify = useNotify();
const selectedDanhMuc = ref({
  id: "",
  ma: "", // Mã sẽ được tự động tạo khi thêm mới
  ten: "",
});
let modalInstance = null;

// --- Bộ lọc ---
const search = ref("");

// PHÂN TRANG: Khởi tạo biến
const currentPage = ref(1);
const pageSize = ref(10); // Số lượng mục trên 1 trang

// Load danh mục
const loadDanhMucs = async () => {
    try {
        const res = await getAllDanhMuc();
        
        // 💡 BƯỚC CẬP NHẬT: Sắp xếp theo Mã (ma) giảm dần (Z -> A)
        danhMucs.value = res.data.sort((a, b) => {
            // Dùng localeCompare để so sánh chuỗi
            // b.ma.localeCompare(a.ma) sẽ sắp xếp GIẢM DẦN (DM10 lên trước DM09)
            // LƯU Ý: Sắp xếp chuỗi DM10 VÀ DM09:
            // Sắp xếp Alphabet: DM10 (chuỗi '1') đứng trước DM09 (chuỗi '0').
            // => Đây là cách sắp xếp TỰ NHIÊN (DM10 sẽ lên đầu danh sách)
            
            // Trường hợp 1: Sắp xếp Tăng Dần (A -> Z):
            // return a.ma.localeCompare(b.ma); // DM02, DM03, ..., DM09, DM10 (Nếu bạn muốn DM10 ở cuối)

            // Trường hợp 2: Sắp xếp GIẢM DẦN (Z -> A, để DM10 lên đầu):
            return b.ma.localeCompare(a.ma); // DM10, DM09, ..., DM02 (Nếu bạn muốn DM10 ở đầu)
        }); 

        // PHÂN TRANG: Đảm bảo trang hiện tại hợp lệ sau khi tải dữ liệu
        if (currentPage.value > totalPages.value) currentPage.value = 1;
    } catch (err) {
        console.error("❌ Lỗi khi tải danh mục:", err);
    }
};

onMounted(async () => {
  await loadDanhMucs();
});

// computed danh sách lọc (GIỮ NGUYÊN)
const filteredDanhMucs = computed(() => {
  const q = (search.value || "").trim().toLowerCase();
  if (!q) return danhMucs.value;
  return danhMucs.value.filter(
    (i) =>
      (i.ma || "").toLowerCase().includes(q) ||
      (i.ten || "").toLowerCase().includes(q)
  );
});

// PHÂN TRANG: Tính tổng số trang (Dựa trên filteredDanhMucs)
const totalPages = computed(() => Math.ceil(filteredDanhMucs.value.length / pageSize.value));

// PHÂN TRANG: Danh sách hiển thị theo trang
const pagedDanhMucs = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    return filteredDanhMucs.value.slice(start, start + pageSize.value);
});

// WATCH: Theo dõi bộ lọc để đặt lại trang về 1
watch(search, () => {
    currentPage.value = 1;
});

const resetFilter = () => {
  search.value = "";
};

// Mở modal thêm (Mã danh mục sẽ để trống, Backend sẽ tự tạo)
const openModalAdd = () => {
  selectedDanhMuc.value = { id: "", ma: "", ten: "" };
  const modalEl = document.getElementById("detailModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// Mở modal sửa
const editItem = (item) => {
  selectedDanhMuc.value = JSON.parse(JSON.stringify(item));
  const modalEl = document.getElementById("detailModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// ✅ Validate form trước khi lưu
const validateForm = () => {
  // 💡 CHỈ kiểm tra Mã danh mục khi đang CẬP NHẬT
  if (selectedDanhMuc.value.id) {
    if (!selectedDanhMuc.value.ma?.trim()) {
      notify.warning("Vui lòng nhập mã danh mục!");
      return false;
    }
    if (selectedDanhMuc.value.ma.length < 2) {
      notify.warning("Mã danh mục phải có ít nhất 2 ký tự!");
      return false;
    }
  }

  // Luôn kiểm tra Tên danh mục
  if (!selectedDanhMuc.value.ten?.trim()) {
    notify.warning("Vui lòng nhập tên danh mục!");
    return false;
  }
  if (selectedDanhMuc.value.ten.length < 3) {
    notify.warning("Tên danh mục phải có ít nhất 3 ký tự!");
    return false;
  }
  return true;
};

// Lưu thêm/sửa
const saveItem = async () => {
  try {
    if (!validateForm()) return; // ✅ kiểm tra form trước khi lưu

    if (selectedDanhMuc.value.id) {
      // Cập nhật: Gửi cả mã (nếu cần thiết, hoặc backend tự xử lý)
      await updateDanhMuc(selectedDanhMuc.value.id, selectedDanhMuc.value);
      notify.success("Cập nhật thành công!");
    } else {
      // Thêm mới: Mã rỗng, chờ Backend tự tạo mã
      await createDanhMuc(selectedDanhMuc.value);
      notify.success("Thêm mới thành công!");
    }
    modalInstance.hide();
    await loadDanhMucs();
  } catch (err) {
    console.error("❌ Lỗi khi lưu danh mục:", err);
    notify.error("Có lỗi xảy ra!");
  }
};

// Xác nhận lưu
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
  if (result.isConfirmed) saveItem();
};

// Xác nhận xóa
const confirmDelete = async (id) => {
  const result = await Swal.fire({
    title: "Xác nhận xóa?",
    text: "Bạn có chắc chắn muốn xóa danh mục này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Xóa",
    cancelButtonText: "Hủy",
    confirmButtonColor: "#dc3545",
  });
  if (result.isConfirmed) {
    try {
      await deleteDanhMuc(id);
      notify.success("Xóa thành công!");
      await loadDanhMucs();
    } catch (err) {
      console.error("❌ Lỗi khi xóa:", err);
      notify.error("Không thể xóa!");
    }
  }
};

// PHÂN TRANG: Hàm chuyển trang
const goToPage = (page) => {
    if (totalPages.value === 0) {
        currentPage.value = 1;
        return;
    }
    if (page < 1) page = 1;
    if (page > totalPages.value) page = totalPages.value;
    currentPage.value = page;
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
            <h3 class="fw-bold text-warning mb-1">Quản lý Danh Mục</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card mb-4">
      <div class="card-header">
        <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ Lọc</h4>
      </div>
      <div class="card-body">
        <form @submit.prevent>
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">Tìm kiếm</label>
              <input
                type="text"
                v-model="search"
                class="form-control"
                placeholder="Nhập mã hoặc tên danh mục..."
              />
            </div>
          </div>

          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0">
              Tổng số:
              <span class="text-warning fw-bold">{{
                filteredDanhMucs.length
              }}</span>
            </p>
            <div class="d-flex align-items-center gap-2">
              <button type="button" class="btn btn-dark" @click="resetFilter">
                Đặt lại bộ lọc
              </button>
              <button
                type="button"
                class="btn btn-warning text-white"
                @click="openModalAdd"
              >
                Thêm danh mục
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <h4 class="card-title mb-0">
          <i class="fa fa-list me-2"></i>Danh sách Danh Mục
        </h4>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="display table text-center align-middle">
            <thead>
              <tr>
                <th>STT</th>
                <th>Mã</th>
                <th>Tên</th>
                <th style="width: 10%">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in pagedDanhMucs" :key="item.id">
                <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
                <td class="text-warning fw-bold">{{ item.ma }}</td>
                <td>{{ item.ten }}</td>
                <td class="text-center">
                  <div class="d-flex justify-content-center align-items-center gap-2">
                    <button class="btn btn-link text-info btn-lg p-0" @click="editItem(item)" title="Xem chi tiết/Sửa">
                      <i class="fa fa-eye"></i>
                    </button>
                    <!-- <button class="btn btn-link text-danger btn-lg p-0" @click="confirmDelete(item.id)" title="Xóa">
                      <i class="fa fa-trash"></i>
                    </button> -->
                  </div>
                </td>
              </tr>
              <tr v-if="filteredDanhMucs.length === 0">
                <td colspan="4" class="text-muted py-3">Không có dữ liệu</td>
              </tr>
            </tbody>
          </table>

          <nav v-if="totalPages > 1" aria-label="Page navigation">
              <ul class="pagination justify-content-end mt-3">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a class="page-link" href="#" @click.prevent="goToPage(currentPage - 1)">Trước</a>
                </li>
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                  <a class="page-link" href="#" @click.prevent="goToPage(page)">{{ page }}</a>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                  <a class="page-link" href="#" @click.prevent="goToPage(currentPage + 1)">Sau</a>
                </li>
              </ul>
          </nav>
        </div>
      </div>
    </div>

    <div
      class="modal fade"
      id="detailModal"
      tabindex="-1"
      aria-labelledby="detailModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-warning text-white">
            <h5 class="modal-title" id="detailModalLabel">
              {{ selectedDanhMuc.id ? "Cập nhật Danh Mục" : "Thêm mới Danh Mục" }}
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
              <div class="col-12" v-if="selectedDanhMuc.id">
                <label class="form-label">Mã <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  v-model="selectedDanhMuc.ma"
                  placeholder="Mã danh mục"
                  readonly
                />
                 <small class="text-muted">Mã được tạo tự động và không thể thay đổi.</small>
              </div>

              <div class="col-12">
                <label class="form-label">Tên <span class="text-danger">*</span></label>
                <input
                  type="text"
                  class="form-control"
                  v-model="selectedDanhMuc.ten"
                  placeholder="Nhập tên danh mục"
                />
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