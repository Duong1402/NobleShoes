<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, computed, onMounted, watch } from "vue";
import { useNotify } from "@/composables/useNotify";

// Service Khách hàng
import { getAllKhachHang, updateKhachHang } from "@/service/KhachHangService";
import { getDiaChiByKhachHangId } from "@/service/DiaChiService";

/* ============ STATE ============ */
const notify = useNotify();

const isLoading = ref(false); // trạng thái tải danh sách
const khachHang = ref([]); // toàn bộ KH
const q = ref(""); // từ khóa tìm kiếm
const statusFilter = ref("all"); // all | active | inactive

// Ưu tiên ID KH vừa thêm (được set bên trang Thêm KH)
const newFirstId = ref(null);

const khachHangDiaChiMap = ref(new Map());

const getDisplayAddress = (kh) => {
  return khachHangDiaChiMap.value.get(kh.id) || "— Chưa có địa chỉ —";
};

/* ============ HELPERS ============ */
const fDate = (d) => {
  if (!d) return "";
  try {
    if (/^\d{4}-\d{2}-\d{2}$/.test(d)) return d;
    const t = new Date(d);
    const yyyy = t.getFullYear();
    const mm = String(t.getMonth() + 1).padStart(2, "0");
    const dd = String(t.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  } catch {
    return d;
  }
};

// Rút số thứ tự từ mã kiểu "KH001" -> 1 (để tie-break)
function parseMaNum(ma) {
  if (!ma) return -1;
  const m = String(ma).match(/(\d+)/g);
  if (!m) return -1;
  return Number(m.at(-1)) || -1;
}

// Lấy timestamp ưu tiên để sort “mới nhất trước”
function pickTime(kh) {
  const t1 = kh?.ngayCapNhat || kh?.updatedAt;
  const t2 = kh?.ngayTao || kh?.createdAt;
  const n1 = t1 ? new Date(t1).getTime() : NaN;
  const n2 = t2 ? new Date(t2).getTime() : NaN;
  if (!Number.isNaN(n1)) return n1;
  if (!Number.isNaN(n2)) return n2;
  return 0;
}

/* ============ LOAD ============ */
const loadKhachHang = async () => {
  try {
    isLoading.value = true;
    const res = await getAllKhachHang();

    // Chuẩn hóa: array / {data:array} / {data:{content:array}}
    const raw = res?.data ?? res ?? [];
    const data = Array.isArray(raw) ? raw : raw?.content ?? [];
    khachHang.value = Array.isArray(data) ? data : [];

    await loadDiaChiForKhachHang(khachHang.value);

    const NEW_ID = sessionStorage.getItem("NEW_KH_ID");
    if (NEW_ID) {
      newFirstId.value = NEW_ID;
      sessionStorage.removeItem("NEW_KH_ID");
      currentPage.value = 1;
    }
  } catch (err) {
    console.error("Lỗi tải khách hàng:", err);
    khachHang.value = []; // fallback an toàn
    notify.error("Không tải được danh sách khách hàng");
  } finally {
    isLoading.value = false;
  }
};

const loadDiaChiForKhachHang = async (khachHangList) => {
  // Chỉ lấy địa chỉ của khách hàng mới hoặc chưa có trong Map
  const promises = khachHangList
    .filter((kh) => !khachHangDiaChiMap.value.has(kh.id))
    .map(async (kh) => {
      try {
        // Gọi API lấy danh sách địa chỉ của KH này
        // GIẢ ĐỊNH: findAllByKhachHangId(id) trả về List<DiaChi>
        const res = await getDiaChiByKhachHangId(kh.id);
        const addresses = res?.data ?? res ?? [];

        if (addresses.length > 0) {
          // Chọn địa chỉ đầu tiên (coi là mặc định)
          const dc = addresses[0]; // Tạo chuỗi địa chỉ đầy đủ
          const fullAddress = [dc.diaChiCuThe, dc.xa, dc.huyen, dc.thanhPho]
            .filter(Boolean)
            .join(", "); // Lưu vào Map
          khachHangDiaChiMap.value.set(kh.id, fullAddress);
        } else {
          khachHangDiaChiMap.value.set(kh.id, "—");
        }
      } catch (e) {
        console.warn(`Không thể lấy địa chỉ cho KH ID: ${kh.id}`, e);
        khachHangDiaChiMap.value.set(kh.id, "— Lỗi tải địa chỉ —");
      }
    }); // Chờ tất cả các promise chạy xong

  await Promise.all(promises);
};

onMounted(loadKhachHang);

/* ============ SORT + FILTER VIEW ============ */
const sortedDesc = computed(() => {
  const arr = [...khachHang.value];
  arr.sort((a, b) => {
    if (newFirstId.value) {
      if (a.id === newFirstId.value && b.id !== newFirstId.value) return -1;
      if (b.id === newFirstId.value && a.id !== newFirstId.value) return 1;
    }
    const tA = pickTime(a);
    const tB = pickTime(b);
    if (tB !== tA) return tB - tA;

    const nA = parseMaNum(a?.ma);
    const nB = parseMaNum(b?.ma);
    if (nB !== nA) return nB - nA;

    return String(b?.id ?? "").localeCompare(String(a?.id ?? ""));
  });
  return arr;
});

const filtered = computed(() => {
  const kw = q.value.trim().toLowerCase();
  return sortedDesc.value.filter((kh) => {
    const okKw =
      !kw ||
      [kh.ma, kh.hoTen, kh.email, kh.sdt]
        .map((v) => String(v ?? "").toLowerCase())
        .some((s) => s.includes(kw));
    const okStatus =
      statusFilter.value === "all" ||
      (statusFilter.value === "active" &&
        (kh.trangThai === 1 || kh.trangThai === true)) ||
      (statusFilter.value === "inactive" &&
        !(kh.trangThai === 1 || kh.trangThai === true));
    return okKw && okStatus;
  });
});

/* ============ PAGINATION (client-side) ============ */
const pageSize = ref(10);
const currentPage = ref(1);
const pageSizes = [5, 10, 20, 50];

const startIndex = computed(() => (currentPage.value - 1) * pageSize.value);
const endIndex = computed(() =>
  Math.min(startIndex.value + pageSize.value, filtered.value.length)
);
const totalPages = computed(() =>
  Math.max(1, Math.ceil(filtered.value.length / pageSize.value))
);

const paginated = computed(() =>
  filtered.value.slice(startIndex.value, startIndex.value + pageSize.value)
);

function goTo(p) {
  const target = Math.min(Math.max(1, p), totalPages.value);
  currentPage.value = target;
}

watch([q, statusFilter, pageSize], () => {
  currentPage.value = 1;
});
watch(filtered, () => {
  if (currentPage.value > totalPages.value)
    currentPage.value = totalPages.value;
});

/* ============ ACTIONS ============ */
const toggleTrangThai = async (kh) => {
  const old = kh.trangThai;
  kh.trangThai = kh.trangThai === 1 ? 0 : 1;
  try {
    const payload = {
      ...kh,
      gioiTinh: Number(kh.gioiTinh),
      trangThai: Number(kh.trangThai),
    };
    await updateKhachHang(kh.id, payload);
    notify.success(
      `Đã chuyển sang: ${
        kh.trangThai === 1 ? "Còn hoạt động" : "Ngừng hoạt động"
      }`
    );
  } catch (err) {
    kh.trangThai = old; // rollback
    console.error("Lỗi đổi trạng thái:", err);
    notify.error("Cập nhật trạng thái thất bại!");
  }
};

/* ============ EXPORT EXCEL ============ */
const exportExcel = async () => {
  try {
    if (!filtered.value.length) {
      notify.error("Không có dữ liệu để xuất!");
      return;
    }
    const XLSX = await import("xlsx");

    const rows = filtered.value.map((kh, idx) => ({
      STT: idx + 1,
      Mã: kh.ma || "",
      "Họ tên": kh.hoTen || "",
      SĐT: kh.sdt || "",
      Email: kh.email || "",
      "Giới tính": kh.gioiTinh === 1 || kh.gioiTinh === true ? "Nam" : "Nữ",
      "Ngày sinh": kh.ngaySinh ? fDate(kh.ngaySinh) : "",
      "Địa chỉ": kh.diaChi || "",
      "Trạng thái":
        kh.trangThai === 1 || kh.trangThai === true
          ? "Còn hoạt động"
          : "Ngừng hoạt động",
    }));

    const ws = XLSX.utils.json_to_sheet(rows, {
      header: [
        "STT",
        "Mã",
        "Họ tên",
        "SĐT",
        "Email",
        "Giới tính",
        "Ngày sinh",
        "Địa chỉ",
        "Trạng thái",
      ],
    });

    ws["!cols"] = [
      { wch: 6 },
      { wch: 12 },
      { wch: 24 },
      { wch: 14 },
      { wch: 28 },
      { wch: 10 },
      { wch: 12 },
      { wch: 40 },
      { wch: 18 },
    ];

    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "KhachHang");

    const now = new Date();
    const pad = (n) => String(n).padStart(2, "0");
    const ts = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(
      now.getDate()
    )}_${pad(now.getHours())}-${pad(now.getMinutes())}`;
    const filename = `khach_hang_${ts}.xlsx`;

    XLSX.writeFile(wb, filename);
    notify.success("Xuất Excel thành công!");
  } catch (err) {
    console.error("Lỗi xuất Excel:", err);
    notify.error("Xuất Excel thất bại!");
  }
};
</script>

<template>
  <div class="container-fluid mt-4 px-1">
    <!-- Header -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý khách hàng</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <!-- Bộ lọc -->
    <div class="card">
      <div class="card-header">
        <div class="d-flex align-items-center">
          <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ Lọc</h4>
        </div>
      </div>
      <div class="card-body">
        <form @submit.prevent>
          <div class="row g-3">
            <!-- Ô tìm kiếm -->
            <div class="col-md-4">
              <label class="form-label fw-bold">Tìm kiếm</label>
              <input
                v-model="q"
                type="text"
                class="form-control"
                placeholder="Mã, tên, email, SĐT..."
              />
            </div>

            <!-- Trạng thái: cùng kích thước cột với ô tìm kiếm -->
            <div class="col-md-4">
              <label class="form-label fw-bold">Trạng thái</label>
              <div class="d-flex mt-2 status-group">
                <label class="form-check custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="all"
                    v-model="statusFilter"
                  />
                  <span class="form-check-label">Tất cả</span>
                </label>

                <label class="form-check custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="active"
                    v-model="statusFilter"
                  />
                  <span class="form-check-label">Còn hoạt động</span>
                </label>

                <label class="form-check custom-radio">
                  <input
                    class="form-check-input"
                    type="radio"
                    name="status"
                    value="inactive"
                    v-model="statusFilter"
                  />
                  <span class="form-check-label">Ngừng hoạt động</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Footer của bộ lọc -->
          <div
            class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
          >
            <p class="mb-2 mb-md-0">
              Tổng số khách hàng:
              <span class="text-warning fw-bold">{{ filtered.length }}</span>
            </p>

            <div class="d-flex align-items-center gap-2 flex-wrap">
              <button
                type="button"
                class="btn btn-warning text-white"
                @click="
                  q = '';
                  statusFilter = 'all';
                "
              >
                Đặt lại bộ lọc
              </button>

              <button
                type="button"
                class="btn btn-warning text-white"
                @click="exportExcel"
                title="Xuất danh sách đang lọc ra Excel"
              >
                <i class="fa-regular fa-file-excel me-1"></i> Xuất Excel
              </button>

              <router-link
                to="/admin/khach-hang/them"
                class="btn btn-warning text-white"
              >
                <i class="fa fa-plus me-1"></i> Thêm khách hàng
              </router-link>
            </div>
          </div>
        </form>
      </div>
    </div>

    <!-- Danh sách -->
    <div class="row mt-3">
      <div class="col-md-12">
        <div class="card">
          <div class="card-header">
            <div class="d-flex align-items-center justify-content-between">
              <h4 class="card-title mb-0">Danh sách khách hàng</h4>
            </div>
          </div>

          <div class="card-body">
            <div class="table-responsive">
              <table id="kh-table" class="display table">
                <thead>
                  <tr class="text-center">
                    <th>STT</th>
                    <th>Mã</th>
                    <th>Ảnh</th>
                    <th>Họ tên</th>
                    <th>SĐT</th>
                    <th>Email</th>
                    <th>Địa chỉ</th>
                    <th>Trạng thái</th>
                    <th style="width: 10%">Thao tác</th>
                  </tr>
                </thead>

                <tbody>
                  <!-- Loading row -->
                  <tr v-if="isLoading">
                    <td colspan="9" class="text-center py-4">
                      <div
                        class="spinner-border text-warning"
                        role="status"
                      ></div>
                      <div class="mt-2 text-secondary">Đang tải dữ liệu...</div>
                    </td>
                  </tr>

                  <!-- Data rows -->
                  <tr v-for="(kh, index) in paginated" :key="kh.id" v-else>
                    <td class="text-center">{{ startIndex + index + 1 }}</td>
                    <td class="text-warning">{{ kh.ma }}</td>
                    <td class="text-center">
                      <img
                        :src="kh.urlAnh || '/src/assets/img/default-avatar.png'"
                        alt="Ảnh khách hàng"
                        class="rounded-circle shadow-sm"
                        style="
                          width: 65px;
                          height: 65px;
                          object-fit: cover;
                          border: 2px solid #ffc107;
                        "
                      />
                    </td>
                    <td>{{ kh.hoTen }}</td>
                    <td>{{ kh.sdt }}</td>
                    <td>{{ kh.email }}</td>
                    <td style="max-width: 240px; white-space: normal">
                      {{ getDisplayAddress(kh) }}
                    </td>
                    <td>
                      <span
                        class="badge rounded-pill fs-6 px-3 status-badge"
                        :class="{
                          'text-white bg-warning':
                            kh.trangThai === 1 || kh.trangThai === true,
                          'text-white bg-danger': !(
                            kh.trangThai === 1 || kh.trangThai === true
                          ),
                        }"
                      >
                        {{
                          kh.trangThai === 1 || kh.trangThai === true
                            ? "Còn hoạt động"
                            : "Ngừng hoạt động"
                        }}
                      </span>
                    </td>
                    <td class="text-center align-middle">
                      <div
                        class="d-flex justify-content-center align-items-center gap-3"
                      >
                        <div class="form-check form-switch m-0">
                          <input
                            class="form-check-input switch-yellow"
                            type="checkbox"
                            role="switch"
                            :id="'switch-' + kh.id"
                            :checked="
                              kh.trangThai === 1 || kh.trangThai === true
                            "
                            @change="toggleTrangThai(kh)"
                            style="
                              cursor: pointer;
                              width: 2.4rem;
                              height: 1.3rem;
                            "
                          />
                        </div>

                        <!-- Điều hướng sang trang sửa bằng route name -->
                        <router-link
                          :to="{
                            name: 'KhachHangDetail',
                            params: { id: kh.id },
                          }"
                          class="btn btn-link btn-warning btn-lg p-0"
                          title="Sửa khách hàng"
                        >
                          <i class="fa fa-pen"></i>
                        </router-link>
                      </div>
                    </td>
                  </tr>

                  <!-- Empty row -->
                  <tr v-if="!isLoading && paginated.length === 0">
                    <td colspan="9" class="text-center text-muted py-3">
                      Không có dữ liệu phù hợp
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- Pagination controls -->
            <div
              class="d-flex flex-column flex-md-row align-items-center justify-content-between mt-3 gap-2"
            >
              <div class="d-flex align-items-center gap-2">
                <label class="mb-0">Hiển thị</label>
                <select
                  class="form-select form-select-sm"
                  style="width: 90px"
                  v-model.number="pageSize"
                >
                  <option v-for="s in pageSizes" :key="s" :value="s">
                    {{ s }}
                  </option>
                </select>
                <span class="text-muted">
                  Dòng {{ filtered.length ? startIndex + 1 : 0 }}–{{
                    endIndex
                  }}
                  / {{ filtered.length }}
                </span>
              </div>

              <div class="btn-group">
                <button
                  class="btn btn-sm btn-outline-secondary"
                  :disabled="currentPage === 1"
                  @click="goTo(1)"
                >
                  « Đầu
                </button>
                <button
                  class="btn btn-sm btn-outline-secondary"
                  :disabled="currentPage === 1"
                  @click="goTo(currentPage - 1)"
                >
                  ‹ Trước
                </button>
                <button
                  class="btn btn-sm btn-outline-secondary"
                  :disabled="currentPage === totalPages"
                  @click="goTo(currentPage + 1)"
                >
                  Sau ›
                </button>
                <button
                  class="btn btn-sm btn-outline-secondary"
                  :disabled="currentPage === totalPages"
                  @click="goTo(totalPages)"
                >
                  Cuối »
                </button>
              </div>

              <div class="text-muted small">
                Trang {{ currentPage }} / {{ totalPages }}
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
  transition: all 0.2s ease;
}
.badge:hover {
  transform: scale(1.05);
  opacity: 0.9;
}

.form-check-input {
  cursor: pointer;
}

/* ====== Trạng thái: 3 radio bằng nhau, không viền hộp, cao = ô tìm kiếm ====== */
.status-group {
  display: flex;
  gap: 10px;
  width: 100%;
}
.status-group .custom-radio {
  flex: 1 1 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 38px; /* = .form-control mặc định */
  padding: 0 10px;
  border: none;
  background: transparent;
  box-shadow: none;
  cursor: pointer;
  user-select: none;
  margin: 0;
  white-space: nowrap;
  font-size: 0.95rem;
}
.status-group .form-check-input {
  width: 16px;
  height: 16px;
  margin: 0;
}
.status-group .custom-radio:has(.form-check-input:checked) {
  border: none;
  box-shadow: none;
  background: transparent;
}
.status-group .custom-radio .form-check-label {
  color: inherit;
  font-weight: 500;
}
.status-group .custom-radio:has(.form-check-input:checked) .form-check-label {
  color: #ffc107;
  font-weight: 600;
}

/* Radio checked màu vàng */
.custom-radio .form-check-input:checked {
  background-color: #ffc107 !important;
  border-color: #ffc107 !important;
}

/* ====== Switch màu vàng ====== */
.form-check-input.switch-yellow {
  accent-color: #ffc107;
} /* Chrome/Edge/Firefox */
.form-switch .form-check-input.switch-yellow:checked {
  background-color: #ffc107 !important;
  border-color: #ffc107 !important;
}
.form-switch .form-check-input.switch-yellow:focus {
  box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
}

/* ====== Bảng ====== */
.status-badge {
  display: inline-block;
  min-width: 140px;
  text-align: center;
  border-radius: 50px;
  font-weight: 500;
  padding: 6px 12px;
  transition: all 0.2s ease;
}

#kh-table td {
  max-width: 240px;
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
  text-align: center;
}
</style>
