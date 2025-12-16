<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed, watch } from "vue";
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import axios from "axios";
import {
  getAllNhanVien,
  updateNhanVien,
  createNhanVien,
  getAllChucVu,
} from "@/service/NhanVienService";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { useNotify } from "@/composables/useNotify";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

const nhanVien = ref([]);
const chucVuList = ref([]);
const notify = useNotify();

// Từ khóa tìm kiếm
const searchTerm = ref("");

// Trạng thái lọc
const filterStatus = ref("all");

// Đặt lại bộ lọc
const resetFilter = () => {
  searchTerm.value = "";
  filterStatus.value = "all";
  currentPage.value = 1;
};
watch([searchTerm, filterStatus], () => {
  currentPage.value = 1;
});

// Phân trang
const currentPage = ref(1);
const itemsPerPage = ref(5); 
const exportToExcel = () => {
  if (!filteredNhanVien.value.length) {
    notify.warning("Không có dữ liệu để xuất!");
    return;
  }

  const data = filteredNhanVien.value.map((nv, index) => ({
    STT: index + 1,
    "Mã nhân viên": nv.ma,
    "Họ tên": nv.hoTen,
    Email: nv.email,
    "Số điện thoại": nv.sdt,
    CCCD: nv.cccd || "",
    "Ngày sinh": nv.ngaySinh
      ? new Date(nv.ngaySinh).toLocaleDateString("vi-VN")
      : "",
    "Giới tính": nv.gioiTinh ? "Nam" : "Nữ",
    "Địa chỉ": nv.diaChi || "",
    "Chức vụ": nv.chucVu?.ten || "",
    "Trạng thái": nv.trangThai === 1 ? "Còn hoạt động" : "Ngừng hoạt động",
  }));

  const ws = XLSX.utils.json_to_sheet(data);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "DanhSachNhanVien");

  const buffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
  saveAs(new Blob([buffer]), "DanhSachNhanVien.xlsx");
  notify.success("Xuất file Excel thành công!");
};

const importing = ref(false);
const importFromExcel = async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = async (evt) => {
    const data = new Uint8Array(evt.target.result);
    const workbook = XLSX.read(data, { type: "array" });
    const sheet = workbook.Sheets[workbook.SheetNames[0]];
    const importedData = XLSX.utils.sheet_to_json(sheet);

    console.log("Dữ liệu Excel nhập vào:", importedData);

    if (!importedData.length) {
      notify.warning("File Excel không có dữ liệu!");
      return;
    }

    const mappedData = importedData.map((item) => {
      // Chuyển giới tính
      const gioiTinh = String(item["Giới tính"]).toLowerCase().includes("nam")
        ? true
        : false;
      const chucVuObj = chucVuList.value.find(
        (cv) =>
          cv.ten.toLowerCase().trim() ===
          String(item["Chức vụ"] || "")
            .toLowerCase()
            .trim()
      );

      return {
        hoTen: item["Họ tên"] || "",
        email: item["Email"] || "",
        sdt: item["Số điện thoại"] || "",
        cccd: item["CCCD"] || "",
        ngaySinh: item["Ngày sinh"]
          ? new Date(item["Ngày sinh"]).toISOString().split("T")[0]
          : null,
        gioiTinh,
        diaChi: item["Địa chỉ"] || "",
        chucVu: chucVuObj ? { id: chucVuObj.id } : null,
        trangThai: String(item["Trạng thái"]).toLowerCase().includes("ngừng")
          ? 0
          : 1,
      };
    });
    const invalidChucVu = mappedData.filter((x) => !x.chucVu);
    if (invalidChucVu.length > 0) {
      Swal.fire({
        icon: "warning",
        title: "Thiếu dữ liệu chức vụ!",
        html: `Có ${invalidChucVu.length} nhân viên không khớp chức vụ trong hệ thống.<br>Vui lòng kiểm tra lại cột <b>Chức vụ</b>.`,
      });
      return;
    }

    Swal.fire({
      title: "Nhập dữ liệu từ Excel?",
      text: `Bạn muốn thêm ${mappedData.length} nhân viên từ file này?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Có, nhập ngay",
      cancelButtonText: "Hủy",
    }).then(async (result) => {
      if (result.isConfirmed) {
        importing.value = true;
        notify.info("Đang nhập dữ liệu, vui lòng chờ...");
        try {
          for (const nv of mappedData) {
            await createNhanVien(nv);
          }

          notify.success("Nhập dữ liệu thành công!");
          await loadNhanVien();
        } catch (err) {
          console.error("❌ Lỗi khi nhập Excel:", err);
          notify.error("Có lỗi khi nhập dữ liệu!");
        } finally {
          importing.value = false;
        }
      }
    });
  };

  reader.readAsArrayBuffer(file);
};

const downloadTemplateExcel = () => {
  const templateData = [
    {
      "Họ tên": "Nguyễn Văn A",
      Email: "a@example.com",
      "Số điện thoại": "0909123456",
      CCCD: "012345678901",
      "Ngày sinh": "01/01/1990",
      "Giới tính": "Nam",
      "Địa chỉ": "123 Nguyễn Chí Thanh, Láng Hạ, Đống Đa, Hà Nội",
      "Chức vụ": "Nhân viên",
    },
  ];

  const ws = XLSX.utils.json_to_sheet(templateData);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, "Template");

  const buffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
  saveAs(new Blob([buffer]), "MauNhapNhanVien.xlsx");
  notify.success("Tải file mẫu Excel thành công!");
};

onMounted(async () => {
  await loadNhanVien();
  await loadChucVu();
});

const loadNhanVien = async () => {
  try {
    const res = await getAllNhanVien();
    nhanVien.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tải danh sách nhân viên:", err);
  }
};

const loadChucVu = async () => {
  try {
    const res = await getAllChucVu();
    console.log("Dữ liệu chức vụ tải về:", res);
    chucVuList.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tải danh sách chức vụ:", err);
    console.error("Chi tiết lỗi:", err.message || err);
  }
};

const filteredNhanVien = computed(() => {
  const keyword = searchTerm.value.toLowerCase().trim();

  return nhanVien.value.filter((nv) => {
    const matchKeyword =
      !keyword ||
      nv.ma?.toLowerCase().includes(keyword) ||
      nv.hoTen?.toLowerCase().includes(keyword) ||
      nv.sdt?.toLowerCase().includes(keyword) ||
      nv.email?.toLowerCase().includes(keyword) ||
      nv.diaChi?.toLowerCase().includes(keyword) ||
      nv.cccd?.toLowerCase().includes(keyword) ||
      nv.taiKhoan?.toLowerCase().includes(keyword) ||
      nv.chucVu?.ten?.toLowerCase().includes(keyword);

    const matchStatus =
      filterStatus.value === "all"
        ? true
        : filterStatus.value === "active"
        ? nv.trangThai === 1
        : nv.trangThai === 0;

    return matchKeyword && matchStatus;
  });
});

const paginatedNhanVien = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return filteredNhanVien.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(filteredNhanVien.value.length / itemsPerPage.value) || 1;
});

const changePage = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
};

const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    text: "Bạn có chắc chắn muốn cập nhật nhân viên này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    saveNhanVien(); 
  }
};

const toggleTrangThai = async (nv) => {
  const oldValue = nv.trangThai;
  nv.trangThai = nv.trangThai === 1 ? 0 : 1;

  try {
    const payload = {
      id: nv.id,
      ma: nv.ma,
      hoTen: nv.hoTen,
      sdt: nv.sdt,
      email: nv.email,
      urlAnh: nv.urlAnh,
      gioiTinh: nv.gioiTinh, // true = Nam, false = Nữ (hoặc null)
      ngaySinh: nv.ngaySinh, // ISO string: "YYYY-MM-DD"
      diaChi: nv.diaChi,
      cccd: nv.cccd,
      taiKhoan: nv.taiKhoan,
      matKhau: nv.matKhau,
      nguoiTao: nv.nguoiTao,
      nguoiSua: nv.nguoiSua,
      ngayTao: nv.ngayTao,
      ngaySua: nv.ngaySua,
      chucVu: nv.chucVu ? { id: nv.chucVu.id } : null,
      trangThai: nv.trangThai, // 1 hoặc 0
    };

    await updateNhanVien(nv.id, payload);

    notify.success(
      `Đã chuyển sang trạng thái: ${
        nv.trangThai === 1 ? "Còn hoạt động" : "Ngừng hoạt động"
      }`
    );
  } catch (err) {
    nv.trangThai = oldValue; 
    console.error("❌ Lỗi khi cập nhật trạng thái:", err);
    notify.error("Cập nhật trạng thái thất bại!");
  }
};
</script>
<template>
  <div class="container-fluid mt-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý nhân viên</h3>
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
                v-model="searchTerm"
                type="text"
                class="form-control border-warning"
                placeholder="Mã, tên, email..."
                style="border-width: 2px"
              />
            </div>

            <div class="col-md-4">
              <label class="form-label fw-bold">Trạng thái</label>
              <div class="d-flex mt-2">
                <div class="form-check me-3 custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    id="statusAll"
                    value="all"
                    v-model="filterStatus"
                  />
                  <label class="form-check-label" for="statusAll">Tất cả</label>
                </div>

                <div class="form-check me-3 custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    id="statusActive"
                    value="active"
                    v-model="filterStatus"
                  />
                  <label class="form-check-label" for="statusActive"
                    >Còn hoạt động</label
                  >
                </div>

                <div class="form-check custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    id="statusInactive"
                    value="inactive"
                    v-model="filterStatus"
                  />
                  <label class="form-check-label" for="statusInactive"
                    >Ngừng hoạt động</label
                  >
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0">
              Tổng số nhân viên:
              <span class="text-warning fw-bold">{{
                filteredNhanVien.length
              }}</span>
            </p>
            <div class="d-flex align-items-center gap-2">
              <button type="button" class="btn btn-dark" @click="resetFilter">
                Đặt lại bộ lọc
              </button>

              <router-link
                :to="{ name: 'nhanVienAdd' }"
                class="btn btn-warning text-white"
              >
                Thêm nhân viên
              </router-link>

              <!-- ✅ Xuất Excel -->
              <button
                type="button"
                class="btn btn-warning text-white"
                @click="exportToExcel"
              >
                Xuất Excel
              </button>

              <!-- ✅ Nhập từ Excel -->
              <label class="btn btn-warning text-white mb-0">
                Nhập từ Excel
                <input
                  type="file"
                  accept=".xlsx,.xls"
                  hidden
                  @change="importFromExcel"
                />
              </label>

              <!-- ✅ Tải mẫu Excel -->
              <button
                type="button"
                class="btn btn-warning text-white"
                @click="downloadTemplateExcel"
              >
                Tải mẫu Excel
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
            <div class="d-flex align-items-center justify-content-between">
              <h4 class="card-title mb-0">
                <i class="fa fa-table me-2"></i>Danh Sách Nhân Viên
              </h4>
            </div>
          </div>

          <div class="card-body">
            <div class="table-responsive">
              <table id="add-row" class="display table">
                <thead>
                  <tr style="text-align: center">
                    <th>STT</th>
                    <th>Mã</th>
                    <th>Ảnh</th>
                    <th>Họ tên</th>
                    <th>SĐT</th>
                    <th>Email</th>
                    <th>Địa chỉ</th>
                    <th>Chức vụ</th>
                    <th>Trạng thái</th>
                    <th style="width: 10%">Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(nv, index) in paginatedNhanVien" :key="nv.id">
                    <td class="text-center">
                      {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                    </td>
                    <td class="text-warning">{{ nv.ma }}</td>
                    <td class="text-center">
                      <img
                        :src="nv.urlAnh || '/src/assets/img/default-avatar.png'"
                        alt="Ảnh nhân viên"
                        class="rounded-circle shadow-sm"
                        style="
                          width: 65px;
                          height: 65px;
                          object-fit: cover;
                          border: 2px solid #ffc107;
                        "
                      />
                    </td>

                    <td>{{ nv.hoTen }}</td>
                    <td>{{ nv.sdt }}</td>
                    <td>{{ nv.email }}</td>
                    <td>{{ nv.diaChi }}</td>
                    <td>
                      <span v-if="nv.chucVu" class="fs-6 px-3 py-2 text-black">
                        {{ nv.chucVu.ten }}
                      </span>
                      <span v-else class="text-muted">-</span>
                    </td>

                    <td>
                      <span
                        class="badge rounded-pill fs-6 px-3 status-badge"
                        :class="{
                          'text-white bg-warning': nv.trangThai,
                          'text-white bg-danger': !nv.trangThai,
                        }"
                      >
                        {{ nv.trangThai ? "Còn hoạt động" : "Ngừng hoạt động" }}
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
                            :id="'switch-' + nv.id"
                            :checked="nv.trangThai === 1"
                            @change="toggleTrangThai(nv)"
                            style="
                              cursor: pointer;
                              width: 2.4rem;
                              height: 1.3rem;
                            "
                          />
                        </div>

                        <!-- Nút cập nhật -->
                        <router-link
                          v-if="nv.trangThai === 1"
                          :to="{
                            name: 'chiTietNhanVien',
                            params: { id: nv.id },
                          }"
                          class="btn btn-link btn-lg p-0 text-decoration-none"
                          title="Cập nhật nhân viên"
                        >
                          <i class="fa-solid fa-eye text-warning"></i>
                        </router-link>
                        <router-link
                          v-else
                          to="#"
                          class="btn btn-link btn-lg p-0 text-decoration-none disabled-link"
                          @click.prevent
                          title="Nhân viên ngừng hoạt động"
                        >
                          <i class="fa-solid fa-eye-slash text-primary"></i>
                        </router-link>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- 🔹 Phân trang & hiển thị số phần tử -->
            <div
              class="d-flex flex-wrap justify-content-between align-items-center mt-3 gap-3"
            >
              <!-- Bên trái: chọn số phần tử hiển thị -->
              <div class="d-flex align-items-center">
                <label class="me-2 mb-0 text-dark small">Hiển thị:</label>
                <select
                  v-model="itemsPerPage"
                  class="form-select form-select-sm w-auto"
                >
                  <option :value="5">5</option>
                  <option :value="10">10</option>
                  <option :value="50">50</option>
                  <option :value="filteredNhanVien.length">Tất cả</option>
                </select>
              </div>

              <!-- Giữa: hiển thị tổng số -->
              <div class="text-muted small text-center flex-grow-1">
                Hiển thị
                <span class="fw-bold">
                  {{ (currentPage - 1) * itemsPerPage + 1 }} -
                  {{
                    Math.min(
                      currentPage * itemsPerPage,
                      filteredNhanVien.length
                    )
                  }}
                </span>
                / {{ filteredNhanVien.length }} mục
              </div>

              <!-- Bên phải: pagination -->
              <nav>
                <ul class="pagination pagination-sm mb-0">
                  <li
                    class="page-item"
                    :class="{ disabled: currentPage === 1 }"
                    @click="changePage(currentPage - 1)"
                  >
                    <a class="page-link" href="#">Trước</a>
                  </li>

                  <li
                    v-for="page in totalPages"
                    :key="page"
                    class="page-item"
                    :class="{ active: page === currentPage }"
                    @click="changePage(page)"
                  >
                    <a class="page-link" href="#">{{ page }}</a>
                  </li>

                  <li
                    class="page-item"
                    :class="{ disabled: currentPage === totalPages }"
                    @click="changePage(currentPage + 1)"
                  >
                    <a class="page-link" href="#">Sau</a>
                  </li>
                </ul>
              </nav>
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
.btn:hover {
  transform: scale(1.03);
  transition: 0.15s ease-in-out;
}
.pagination .page-link {
  color: #ff7b00;
  border-radius: 6px;
}

.pagination .page-item.active .page-link {
  background-color: #ff7b00;
  border-color: #ff7b00;
  color: #fff;
}

.pagination .page-link:hover {
  color: #d66500;
}

@media (max-width: 768px) {
  /* Khi màn nhỏ thì các phần tự xuống hàng */
  .pagination {
    justify-content: center;
  }
}
.disabled-link {
  pointer-events: none; /* Ngăn click */
  opacity: 0.5; /* Làm mờ nút */
  cursor: not-allowed;
}
</style>
