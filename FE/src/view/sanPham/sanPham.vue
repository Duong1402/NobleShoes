<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed, watch } from "vue"; // Thêm watch
import { Modal } from "bootstrap";
import Swal from "sweetalert2";
import { getAllSanPham, updateSanPham } from "@/service/SanPhamService";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import { useNotify } from "@/composables/useNotify";

// Dữ liệu sản phẩm
const sanPham = ref([]);
const notify = useNotify();
const selectedSanPham = ref({
  id: "",
  ma: "",
  ten: "",
  ngayTao: "",
  soLuong: 0,
  trangThai: 1,
});

// Bộ lọc
const filterText = ref("");
const filterStatus = ref("all");

// Pagination
const currentPage = ref(1);
const pageSize = ref(5); // số sản phẩm trên 1 trang

// Load danh sách sản phẩm
const loadSanPham = async () => {
  try {
    const res = await getAllSanPham();
    // ⚠️ CHỈ TẢI DỮ LIỆU: Không sắp xếp ở đây để giữ mảng gốc
    sanPham.value = Array.isArray(res) ? res : res.data || [];
  } catch (err) {
    console.error("❌ Lỗi khi tải danh sách sản phẩm:", err);
  }
};

// Computed danh sách đã lọc (chỉ lọc, chưa sắp xếp)
const preFilteredSanPham = computed(() => {
  return (sanPham.value || []).filter((sp) => {
    const matchesText =
      (sp.ma || "").toLowerCase().includes(filterText.value.toLowerCase()) ||
      (sp.ten || "").toLowerCase().includes(filterText.value.toLowerCase());

    const matchesStatus =
      filterStatus.value === "all" || Number(sp.trangThai) === Number(filterStatus.value);

    return matchesText && matchesStatus;
  });
});

// 💡 ĐIỀU CHỈNH CHỦ YẾU: Sắp xếp danh sách đã lọc
const sortedAndFilteredSanPham = computed(() => {
    // Sắp xếp giảm dần theo ID để sản phẩm mới nhất lên đầu
    // Sử dụng .slice() để tạo bản sao và sắp xếp, tránh lỗi side-effect
    return preFilteredSanPham.value.slice().sort((a, b) => b.id - a.id);
    
    // Nếu ID không phải là số, bạn có thể dùng:
    // return preFilteredSanPham.value.slice().sort((a, b) => new Date(b.ngayTao) - new Date(a.ngayTao));
});

// Tính tổng số trang (Sử dụng sortedAndFilteredSanPham)
const totalPages = computed(() => Math.ceil(sortedAndFilteredSanPham.value.length / pageSize.value));

// Danh sách sản phẩm hiển thị theo trang (Sử dụng sortedAndFilteredSanPham)
const pagedSanPham = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return sortedAndFilteredSanPham.value.slice(start, start + pageSize.value);
});

// Theo dõi sự thay đổi của bộ lọc để đặt lại trang về 1
watch([filterText, filterStatus], () => {
    currentPage.value = 1;
});

// Modal instance
let modalInstance = null;
const getModalInstance = () => {
  const modalEl = document.getElementById("detailModal");
  if (!modalEl) return null;
  return modalInstance || (modalInstance = new Modal(modalEl));
};

// Mở modal chi tiết
const editSanPham = (sp) => {
  selectedSanPham.value = {
    ...JSON.parse(JSON.stringify(sp)),
    ngayTao: sp.ngayTao ? new Date(sp.ngayTao).toISOString().substr(0, 10) : "",
  };
  window.history.pushState({}, "", `?id=${sp.id}`);
  const modal = getModalInstance();
  modal?.show();
};

// Lưu sản phẩm
const saveSanPham = async () => {
  try {
    const payload = { ...selectedSanPham.value };
    await updateSanPham(payload.id, payload);
    notify.success("Cập nhật thành công!");
    getModalInstance()?.hide();
    
    // ⚠️ CẬP NHẬT: Tải lại dữ liệu. Computed property sẽ tự sắp xếp.
    await loadSanPham(); 
  } catch (err) {
    console.error("❌ Lỗi khi cập nhật sản phẩm:", err);
    notify.error("Có lỗi khi cập nhật!");
  }
};

// Xác nhận lưu
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    text: "Bạn có chắc chắn muốn cập nhật sản phẩm này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) saveSanPham();
};

// Toggle trạng thái
const toggleTrangThai = async (sp, newValue) => {
  const oldValue = sp.trangThai;
  sp.trangThai = newValue;

  try {
    await updateSanPham(sp.id, { ...sp, trangThai: newValue });
    notify.success(`Đã chuyển sang: ${newValue ? "Còn hoạt động" : "Ngừng hoạt động"}`);
  } catch (err) {
    sp.trangThai = oldValue;
    notify.error("Cập nhật trạng thái thất bại!");
  }
};

// Chuyển trang
const goToPage = (page) => {
  if (totalPages.value === 0) {
    currentPage.value = 1;
    return;
  }
  if (page < 1) page = 1;
  if (page > totalPages.value) page = totalPages.value;
  currentPage.value = page;
};

// Khi mounted
onMounted(async () => {
  await loadSanPham();

  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  if (id) {
    const sp = sanPham.value.find((s) => String(s.id).trim() === id.trim());
    if (sp) editSanPham(sp);
  }

  const modalEl = document.getElementById("detailModal");
  if (modalEl) {
    modalEl.addEventListener("hidden.bs.modal", () => {
      window.history.pushState({}, "", "/admin/san-pham");
    });
  }
});
</script>

<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý sản phẩm</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="card-header">
        <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ lọc</h4>
      </div>
      <div class="card-body">
        <form @reset.prevent="filterText = ''; filterStatus = 'all'">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label fw-bold">Tìm kiếm</label>
              <input type="text" class="form-control" placeholder="Mã, tên sản phẩm..." v-model="filterText" />
            </div>
            <div class="col-md-4">
              <label class="form-label fw-bold">Trạng thái</label>
              <div class="d-flex mt-2 gap-3">
                <div class="form-check custom-radio">
                  <input type="radio" name="status" value="all" id="filterAll" v-model="filterStatus" />
                  <label for="filterAll" class="form-check-label">Tất cả</label>
                </div>
                <div class="form-check custom-radio">
                  <input type="radio" name="status" value="1" id="filterActive" v-model="filterStatus" />
                  <label for="filterActive" class="form-check-label">Còn hoạt động</label>
                </div>
                <div class="form-check custom-radio">
                  <input type="radio" name="status" value="0" id="filterInactive" v-model="filterStatus" />
                  <label for="filterInactive" class="form-check-label">Ngừng hoạt động</label>
                </div>
              </div>
            </div>
          </div>

          <div class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4">
            <p class="mb-2 mb-md-0">
              Tổng số sản phẩm:
              <span class="text-warning fw-bold">{{ sortedAndFilteredSanPham.length }}</span>
            </p>
            <div class="d-flex align-items-center gap-2">
              <button type="reset" class="btn btn-dark">Đặt lại bộ lọc</button>
              <router-link to="/admin/san-pham/them" class="btn btn-warning text-white">
                Thêm sản phẩm
              </router-link>
            </div>
          </div>
        </form>
      </div>
    </div>

    <div class="row mt-3">
      <div class="col-md-12">
        <div class="card">
          <div class="card-header">
            <h4 class="card-title mb-0">Danh sách sản phẩm</h4>
          </div>

          <div class="card-body">
            <div class="table-responsive">
              <table class="display table">
                <thead>
                  <tr class="text-center">
                    <th>STT</th>
                    <th>Mã sản phẩm</th>
                    <th>Tên sản phẩm</th>
                    <th>Ngày tạo</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(sp, index) in pagedSanPham" :key="sp.id">
                    <td>{{ (currentPage-1)*pageSize + index + 1 }}</td>
                    <td class="text-warning">{{ sp.ma }}</td>
                    <td>{{ sp.ten }}</td>
                    <td>{{ new Date(sp.ngayTao).toLocaleDateString("vi-VN") }}</td>
                    <td>
                      <span class="badge rounded-pill fs-6 px-3" 
                        :class="sp.trangThai ? 'bg-warning text-white' : 'bg-danger text-white'">
                        {{ sp.trangThai ? "Còn hoạt động" : "Ngừng hoạt động" }}
                      </span>
                    </td>
                    <td class="text-center">
                      <div class="d-flex justify-content-center gap-2">
                        <div class="form-check form-switch m-0">
                          <input class="form-check-input" type="checkbox" role="switch" :checked="sp.trangThai"
                            @change="(e) => toggleTrangThai(sp, e.target.checked)" />
                        </div>
                        <router-link v-if="sp.trangThai" :to="`/admin/san-pham/chi-tiet/${sp.id}`"
                          class="btn btn-link text-info p-0"><i class="fa fa-eye"></i></router-link>
                        <span v-else class="text-muted" title="Ngừng hoạt động"><i class="fa fa-eye-slash"></i></span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <nav aria-label="Page navigation">
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
    </div>
  </div>
</template>

<style>
.custom-radio {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.custom-radio input[type="radio"] {
  opacity: 0;
  position: relative;
  width: 20px;
  height: 20px;
}

.custom-radio .form-check-label::before {
  content: "";
  display: block;
  width: 20px;
  height: 20px;
  border: 2px solid #ffc107;
  border-radius: 50%;
  background-color: #fff;
  transition: 0.2s all;
}

.custom-radio input[type="radio"]:checked + .form-check-label::before {
  background-color: #ffc107;
  border-color: #ffc107;
}

.custom-radio .form-check-label {
  text-align: center;
  cursor: pointer;
}
</style>