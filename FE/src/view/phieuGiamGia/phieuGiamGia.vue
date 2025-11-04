<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, reactive, computed, watch } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import {
  getAllPhieuGiamGia,
  // createphieuGiamGia,
  updatePhieuGiamGia,
  updateTrangThaiPhieuGiamGia,
  updateTrangThaiPhieuGiamGiaCaNhan,
  updatePhieuGiamGiaCaNhan,
  // deletephieuGiamGia,
} from "@/service/phieuGiamGiaService";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { useNotify } from "@/composables/useNotify";
import Decimal from "decimal.js";
import { get } from "jquery";
import { all } from "axios";

const phieuGiamGia = ref([]);
const notify = useNotify();
const selectedphieuGiamGia = ref({
});
const errors = reactive({});
const totalPages = ref(0);
const page = ref(0);
const size = ref(10);
const filterTrangThai = ref("all");
const searchQuery = ref("");
const filterNgayBatDau = ref("");
const filterNgayKetThuc = ref("");

// Hàm định dạng ngày tháng
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
    const res = await getAllPhieuGiamGia(page.value, size.value);
    phieuGiamGia.value = res.content;
    totalPages.value = res.totalPages;
  } catch (err) {
    console.error("Lỗi khi tải danh sách phiếu giảm giá:", err);
  }
};

//Chuyển trang
const gotoPage = (p) => {
  if (p >= 0 && p < totalPages.value) {
    page.value = p;
    loadphieuGiamGia();
  }
};

// Hàm mở modal chi tiết + update
const editphieuGiamGia = (p) => {
  // Chuyển định dạng ngày ISO sang yyyy-MM-dd để input date hiểu được
  const toDateInputFormat = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  let normalized = {
    ...p,
    loaiPhieu: p.loaiPhieu || "Thông thường",
    ngayBatDau: toDateInputFormat(p.ngayBatDau),
    ngayKetThuc: toDateInputFormat(p.ngayKetThuc),
  };

  // ✅ Deep copy tránh làm thay đổi list chính
  selectedphieuGiamGia.value = JSON.parse(JSON.stringify(normalized));
console.log(selectedphieuGiamGia.value);
  // ✅ Mở modal sửa
  window.history.pushState({}, "", `?id=${p.id}`);
  const modalEl = document.getElementById("detailModal");
  if (!modalInstance) modalInstance = new Modal(modalEl);
  modalInstance.show();
};

// Hàm lưu cập nhật phiếu giảm giá
const savephieuGiamGia = async () => {
  try {
    let payload = { ...selectedphieuGiamGia.value };

    if (selectedphieuGiamGia.value.loaiPhieu === "Cá nhân") {
      payload = {
        ...payload,
        ngayNhan: payload.ngayBatDau, // map từ ngày bắt đầu trong form
        ngayHetHan: payload.ngayKetThuc, // map sang ngày hết hạn
      };
      await updatePhieuGiamGiaCaNhan(payload.id, payload);
    } else {
      await updatePhieuGiamGia(payload.id, payload);
    }

    notify.success("Cập nhật thành công!");
    modalInstance.hide();
    await loadphieuGiamGia();
  } catch (err) {
    if (err.response && err.response.data) {
      Object.assign(errors, err.response.data);
      console.error("❌ Lỗi xác thực từ BE:", err.response.data);
    } else {
      console.error("❌ Lỗi không xác định:", err);
    }
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
  p.trangThai = !p.trangThai;

  try {
    if (p.loaiPhieu === "Thông thường") {
      await updateTrangThaiPhieuGiamGia(p.id, p.trangThai);
    } else {
      await updateTrangThaiPhieuGiamGiaCaNhan(p.id, p.trangThai);
    }

    notify.success(
      `Đã chuyển sang trạng thái: ${
        p.trangThai ? "Còn hoạt động" : "Ngừng hoạt động"
      }`
    );
  } catch (err) {
    p.trangThai = oldValue;
    console.error("❌ Lỗi khi cập nhật trạng thái:", err);
    notify.error("Cập nhật trạng thái thất bại!");
  }
};

const filterPhieuGiamGia = computed(() => {
  // Hàm lọc phiếu giảm giá (chưa triển khai)
  return phieuGiamGia.value.filter((p) => {
    const matchesStatus =
      filterTrangThai.value === "all" ||
      (filterTrangThai.value === "active" && p.trangThai) ||
      (filterTrangThai.value === "inactive" && !p.trangThai);

    const matchesSearch =
      !searchQuery.value ||
      p.ma.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      p.ten.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      (p.email &&
        p.email.toLowerCase().includes(searchQuery.value.toLowerCase()));

    const matchesDate =
      (!filterNgayBatDau.value ||
        new Date(p.ngayBatDau) >= new Date(filterNgayBatDau.value)) &&
      (!filterNgayKetThuc.value ||
        new Date(p.ngayKetThuc) <= new Date(filterNgayKetThuc.value));

    return matchesStatus && matchesSearch && matchesDate;
  });
});

watch(
  [filterTrangThai, searchQuery, filterNgayBatDau, filterNgayKetThuc],
  () => {
    page.value = 0;
  }
);

const resetFilters = () => {
  filterTrangThai.value = "all";
  searchQuery.value = "";
  filterNgayBatDau.value = "";
  filterNgayKetThuc.value = "";
};
</script>
<template>
  <div class="container-fluid mt-4 px-1">
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
          <div class="row g-3 mb-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">Tìm kiếm</label>
              <input
                type="text"
                class="form-control"
                placeholder="Mã, tên, email..."
                v-model="searchQuery"
                required
              />
            </div>

            <div class="col-md-4">
              <label class="form-label fw-bold">Trạng thái</label>
              <div class="d-flex mt-2">
                <div class="form-check me-3 custom-radio d-flex">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="all"
                    v-model="filterTrangThai"
                    checked
                  />
                  <label class="form-check-label">Tất cả</label>
                </div>
                <div class="form-check me-3 custom-radio d-flex">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="active"
                    v-model="filterTrangThai"
                  />
                  <label class="form-check-label">Còn hoạt động</label>
                </div>
                <div class="form-check custom-radio d-flex">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="inactive"
                    v-model="filterTrangThai"
                  />
                  <label class="form-check-label">Ngừng hoạt động</label>
                </div>
              </div>
            </div>
          </div>
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">Ngày bắt đầu</label>
              <input
                type="date"
                class="form-control"
                v-model="filterNgayBatDau"
              />
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">Ngày kết thúc</label>
              <input
                type="date"
                class="form-control"
                v-model="filterNgayKetThuc"
              />
            </div>
          </div>

          <!-- Footer -->
          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0"></p>
            <div class="d-flex align-items-center gap-2">
              <button type="reset" class="btn btn-dark" @click="resetFilters">
                Đặt lại bộ lọc
              </button>
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
              <h4 class="card-title mb-0">Danh sách Phiếu giảm giá</h4>
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
                    <th>Loại phiếu</th>
                    <th>Giá trị giảm</th>
                    <th>Giá trị giảm tối đa</th>
                    <th>Trạng thái</th>
                    <th style="width: 10%">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(p, index) in filterPhieuGiamGia" :key="p.id">
                    <td>{{ index + 1 + page * size }}</td>
                    <td>
                      {{ p.ma }}
                    </td>
                    <td>{{ p.ten }}</td>
                    <td>{{ formatDate(p.ngayBatDau) }}</td>
                    <td>{{ formatDate(p.ngayKetThuc) }}</td>
                    <td>{{ p.loaiPhieu }}</td>
                    <td>
                      {{ p.giaTriGiam }} {{ p.hinhThucGiamGia ? "%" : "VNĐ" }}
                    </td>
                    <td>{{ p.giaTriGiamToiDa }}VNĐ</td>
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

            <nav aria-label="Page navigation">
              <ul class="pagination justify-content-end mt-3">
                <li class="page-item" :class="{ disabled: page === 0 }">
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(page - 1)"
                    >Trước</a
                  >
                </li>
                <li
                  class="page-item"
                  v-for="n in totalPages"
                  :key="n"
                  :class="{ active: n - 1 === page }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(n - 1)"
                    >{{ n }}</a
                  >
                </li>
                <li
                  class="page-item"
                  :class="{ disabled: page === totalPages - 1 }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(page + 1)"
                    >Sau</a
                  >
                </li>
              </ul>
            </nav>
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
                    Sửa phiếu giảm giá
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
                        :class="{ 'is-invalid': errors.ten }"
                      />
                      <div class="invalid-feedback">
                        {{ errors.ten }}
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Ngày bắt đầu</label>
                      <input
                        type="date"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ngayBatDau"
                        :class="{ 'is-invalid': errors.ngayBatDau }"
                      />
                      <div class="invalid-feedback">
                        {{ errors.ngayBatDau }}
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Ngày kết thúc</label>
                      <input
                        type="date"
                        class="form-control"
                        v-model="selectedphieuGiamGia.ngayKetThuc"
                        :class="{ 'is-invalid': errors.ngayKetThuc }"
                      />
                      <div class="invalid-feedback">
                        {{ errors.ngayKetThuc }}
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label d-block"
                        >Hình thức giảm giá</label
                      >
                      <div class="d-flex gap-3">
                        <div class="form-check custom-radio">
                          <input
                            class="form-check-input"
                            type="radio"
                            id="%"
                            :value="true"
                            v-model="selectedphieuGiamGia.hinhThucGiamGia"
                            checked
                          />
                          <label class="form-check-label">%</label>
                        </div>
                        <div class="form-check custom-radio">
                          <input
                            class="form-check-input"
                            type="radio"
                            id="tienMat"
                            :value="false"
                            v-model="selectedphieuGiamGia.hinhThucGiamGia"
                          />
                          <label class="form-check-label">Tiền mặt</label>
                        </div>
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiam"
                        :class="{ 'is-invalid': errors.giaTriGiam }"
                      />
                      <div class="invalid-feedback">
                        {{ errors.giaTriGiam }}
                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm tối thiểu</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiamToiThieu"
                      />
                      <div class="invalid-feedback">                      </div>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label">Giá trị giảm tối đa</label>
                      <input
                        type="number"
                        class="form-control"
                        v-model="selectedphieuGiamGia.giaTriGiamToiDa"
                        :class="{ 'is-invalid': errors.giaTriGiamToiDa }"
                      />
                      <div class="invalid-feedback">
                        {{ errors.giaTriGiamToiDa }}
                      </div>
                    </div>

                    <div
                      v-if="selectedphieuGiamGia.loaiPhieu === 'Cá nhân'"
                      class="col-md-6"
                    >
                      <label class="form-label">Khách hàng</label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="selectedphieuGiamGia.tenKhachHang"
                        disabled
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