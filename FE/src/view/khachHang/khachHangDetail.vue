<template>
  <div class="container-fluid mt-4 px-1">
    <!-- Header -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Sửa khách hàng</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <div v-if="ready">
        <!-- Banner khóa -->
        <div v-if="isReadOnly" class="alert alert-warning d-flex align-items-center mb-3" role="alert">
          <i class="fa-solid fa-lock me-2"></i>
          Khách hàng này đã   ngừng hoạt động. Bạn không thể chỉnh sửa thông tin.
        </div>

        <form @submit.prevent="confirmSave" :style="isReadOnly ? 'opacity:.9' : ''">
          <!-- Ảnh đại diện -->
          <div class="col-md-12 text-center">
            <div
              class="position-relative d-inline-block rounded-circle border border-2 border-secondary-subtle bg-light"
              :style="isReadOnly ? 'width:140px;height:140px;pointer-events:none;opacity:.7;overflow:hidden' : 'width:140px;height:140px;cursor:pointer;overflow:hidden'"
              @click="!isReadOnly && $refs.fileInput.click()"
            >
              <img
                v-if="previewUrl || form.urlAnh"
                :src="previewUrl || form.urlAnh"
                alt="Ảnh xem trước"
                class="w-100 h-100 object-fit-cover"
              />
              <div
                v-else
                class="d-flex flex-column justify-content-center align-items-center h-100 text-secondary"
              >
                <i class="fa-regular fa-camera fa-3x"></i>
                <span class="fw-medium small">Chọn ảnh</span>
              </div>

              <input
                type="file"
                class="d-none"
                ref="fileInput"
                accept="image/*"
                @change="handleFileUpload"
                :disabled="isReadOnly"
              />
            </div>

            <small v-if="uploading" class="text-secondary d-block mt-2">
              Đang tải ảnh lên...
            </small>
          </div>

          <div class="row g-3 mt-1">
            <!-- Mã (Code) -->
            <div class="col-md-6">
              <label class="form-label">Mã (Code) *</label>
              <input
                v-model.trim="form.ma"
                type="text"
                class="form-control"
                placeholder="VD: KH001"
                required
                :readonly="isReadOnly"
              />
            </div>

            <!-- Họ tên -->
            <div class="col-md-6">
              <label class="form-label">Họ tên *</label>
              <input
                v-model.trim="form.hoTen"
                type="text"
                class="form-control"
                placeholder="Nhập họ tên khách hàng"
                required
                :readonly="isReadOnly"
              />
            </div>

            <!-- Số điện thoại -->
            <div class="col-md-6">
              <label class="form-label">Số điện thoại *</label>
              <input
                v-model.trim="form.sdt"
                type="text"
                class="form-control"
                placeholder="Nhập số điện thoại"
                required
                :readonly="isReadOnly"
              />
            </div>

            <!-- Email -->
            <div class="col-md-6">
              <label class="form-label">Email *</label>
              <input
                v-model.trim="form.email"
                type="email"
                class="form-control"
                placeholder="Nhập email khách hàng"
                required
                :readonly="isReadOnly"
              />
            </div>

            <!-- Giới tính -->
            <div class="col-md-6">
              <label class="form-label d-block">Giới tính</label>
              <div class="d-flex gap-3">
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="radio"
                    id="gioiTinhNam"
                    :value="1"
                    v-model.number="form.gioiTinh"
                    :disabled="isReadOnly"
                  />
                  <label class="form-check-label" for="gioiTinhNam">Nam</label>
                </div>
                <div class="form-check">
                  <input
                    class="form-check-input"
                    type="radio"
                    id="gioiTinhNu"
                    :value="0"
                    v-model.number="form.gioiTinh"
                    :disabled="isReadOnly"
                  />
                  <label class="form-check-label" for="gioiTinhNu">Nữ</label>
                </div>
              </div>
            </div>

            <!-- Ngày sinh -->
            <div class="col-md-6">
              <label class="form-label">Ngày sinh</label>
              <input v-model="form.ngaySinh" type="date" class="form-control" :disabled="isReadOnly" />
            </div>

            <!-- Địa chỉ (Select có sẵn toàn quốc) -->
            <div class="col-md-4">
              <label class="form-label">Tỉnh/Thành phố</label>
              <select
                class="form-select"
                v-model="form.tinhCode"
                @change="onProvinceChange"
                :disabled="isReadOnly"
              >
                <option value="">— Chọn Tỉnh/Thành —</option>
                <option v-for="p in provinces" :key="p.code" :value="String(p.code)">
                  {{ p.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Quận/Huyện</label>
              <select
                class="form-select"
                v-model="form.huyenCode"
                @change="onDistrictChange"
                :disabled="isReadOnly || !districts.length"
              >
                <option value="">— Chọn Quận/Huyện —</option>
                <option v-for="d in districts" :key="d.code" :value="String(d.code)">
                  {{ d.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Xã/Phường</label>
              <select
                class="form-select"
                v-model="form.xaCode"
                :disabled="isReadOnly || !wards.length"
              >
                <option value="">— Chọn Xã/Phường —</option>
                <option v-for="w in wards" :key="w.code" :value="String(w.code)">
                  {{ w.name }}
                </option>
              </select>
            </div>

            <div class="col-12">
              <label class="form-label">Địa chỉ cụ thể</label>
              <input
                v-model.trim="form.chiTiet"
                type="text"
                class="form-control"
                placeholder="Số nhà, thôn, đường..."
                :readonly="isReadOnly"
              />
              <small class="text-muted">
                Xem trước: {{ previewAddress() }}
              </small>
            </div>
          </div>

          <!-- Nút -->
          <div class="mt-4 text-end">
            <router-link to="/admin/khach-hang" class="btn btn-secondary me-2">
              <i class="fa fa-arrow-left me-1"></i> Quay lại
            </router-link>

            <!-- Ẩn hẳn nút nếu readOnly (hoặc để disabled nếu muốn) -->
            <button
              v-if="!isReadOnly"
              type="submit"
              class="btn btn-warning text-white"
            >
              <i class="fa fa-save me-1"></i> Lưu thay đổi
            </button>

            <button
              v-else
              type="button"
              class="btn btn-warning text-white"
              disabled
              title="Khách hàng đã ngừng hoạt động, không thể sửa"
            >
              <i class="fa fa-lock me-1"></i> Lưu thay đổi
            </button>
          </div>
        </form>
      </div>

      <div v-else class="text-center py-4 text-secondary">
        <div class="spinner-border text-warning" role="status"></div>
        <div class="mt-2">Đang tải dữ liệu khách hàng...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import axios from "axios";
import { getKhachHangById, updateKhachHang } from "@/service/KhachHangService";

const route = useRoute();
const router = useRouter();
const notify = useNotify();
const id = route.params.id || route.query.id;

/* ====== RAW SERVER (để merge, tránh UNIQUE NULL) ====== */
const rawServer = ref({});
const j = (o) => { try { return JSON.stringify(o, null, 2); } catch { return String(o); } };

/* ====== STATE FORM ====== */
const form = reactive({
  id: null,
  ma: "",
  hoTen: "",
  email: "",
  sdt: "",
  gioiTinh: 1,   // 1=Nam, 0=Nữ
  ngaySinh: "",  // yyyy-MM-dd
  urlAnh: "",    // URL ảnh sau upload

  // địa chỉ dạng code
  tinhCode: "",
  huyenCode: "",
  xaCode: "",
  chiTiet: "",

  trangThai: 1,  // 1 hoạt động, 0 ngừng
});

const ready = ref(false);

/* ====== READONLY FLAG ====== */
const isReadOnly = computed(() => Number(form.trangThai) === 0);

/* ====== ẢNH ====== */
const uploading = ref(false);
const previewUrl = ref("");

const handleFileUpload = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return;

  // Preview tạm
  previewUrl.value = URL.createObjectURL(file);

  const formData = new FormData();
  formData.append("file", file);

  uploading.value = true;
  try {
    // Endpoint upload giống form thêm của bạn
    const res = await axios.post("http://localhost:8080/api/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    form.urlAnh = res?.data?.url ?? res?.data?.secure_url ?? res?.data ?? "";
    if (form.urlAnh) previewUrl.value = form.urlAnh;
  } catch (error) {
    notify.error("Upload ảnh thất bại!");
    console.error(error);
  } finally {
    uploading.value = false;
  }
};

/* ====== ĐỊA CHỈ TOÀN QUỐC (LOAD TỪ OPEN API) ====== */
const provincesData = ref([]); // [{ code, name, districts:[{code,name,wards:[{code,name}]}] }]

onMounted(async () => {
  if (!id) {
    notify.error("Thiếu ID khách hàng trên URL!");
    return;
  }
  await Promise.all([loadProvinces(), loadKhachHang()]);
  ready.value = true;
});

async function loadProvinces() {
  try {
    const res = await fetch("https://provinces.open-api.vn/api/?depth=3");
    if (!res.ok) throw new Error("Fetch provinces failed");
    provincesData.value = await res.json();
  } catch (e) {
    console.error("Không tải được danh mục Tỉnh/TP. Dùng fallback.", e);
    // Fallback mẫu nhỏ
    provincesData.value = [
      {
        code: "01",
        name: "Hà Nội",
        districts: [
          {
            code: "0101",
            name: "Quận Cầu Giấy",
            wards: [
              { code: "010101", name: "Phường Dịch Vọng" },
              { code: "010102", name: "Phường Dịch Vọng Hậu" },
              { code: "010103", name: "Phường Nghĩa Tân" },
            ],
          },
        ],
      },
    ];
  }
}

/* Lấy object theo code để build tên đầy đủ */
const currentProvince = computed(() =>
  provincesData.value.find(p => String(p.code) === String(form.tinhCode)) || null
);
const currentDistrict = computed(() => {
  if (!currentProvince.value) return null;
  return currentProvince.value.districts?.find(d => String(d.code) === String(form.huyenCode)) || null;
});
const currentWard = computed(() => {
  if (!currentDistrict.value) return null;
  return currentDistrict.value.wards?.find(w => String(w.code) === String(form.xaCode)) || null;
});

/* Danh sách cho 3 select */
const provinces = computed(() => provincesData.value);
const districts = computed(() => currentProvince.value?.districts ?? []);
const wards     = computed(() => currentDistrict.value?.wards ?? []);

/* Reset liên kết khi đổi cấp */
const onProvinceChange = () => { form.huyenCode = ""; form.xaCode = ""; };
const onDistrictChange = () => { form.xaCode = ""; };

/* Helpers */
const toYMD = (d) => {
  if (!d) return null;
  const t = new Date(d);
  if (Number.isNaN(t.getTime())) return null;
  const yyyy = t.getFullYear();
  const mm = String(t.getMonth() + 1).padStart(2, "0");
  const dd = String(t.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
};
const fDate = (d) => {
  if (!d) return "";
  try {
    if (/^\d{4}-\d{2}-\d{2}$/.test(d)) return d;
    const t = new Date(d);
    const yyyy = t.getFullYear();
    const mm = String(t.getMonth() + 1).padStart(2, "0");
    const dd = String(t.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
  } catch { return d; }
};
const previewAddress = () => {
  const tinh = currentProvince.value?.name || "";
  const huyen = currentDistrict.value?.name || "";
  const xa = currentWard.value?.name || "";
  return [form.chiTiet, xa, huyen, tinh].filter(Boolean).join(", ");
};
const buildPreviewHtml = () => `
  <div style="text-align:left;font-size:14px;line-height:1.5">
    <div><b>Mã</b>: ${form.ma || "—"}</div>
    <div><b>Họ tên</b>: ${form.hoTen || "—"}</div>
    <div><b>SĐT</b>: ${form.sdt || "—"}</div>
    <div><b>Email</b>: ${form.email || "—"}</div>
    <div><b>Giới tính</b>: ${Number(form.gioiTinh) === 1 ? "Nam" : "Nữ"}</div>
    <div><b>Ngày sinh</b>: ${form.ngaySinh || "—"}</div>
    <div><b>Địa chỉ</b>: ${previewAddress() || "—"}</div>
    <div><b>Trạng thái</b>: ${Number(form.trangThai) === 1 ? "Còn hoạt động" : "Ngừng hoạt động"}</div>
    ${form.urlAnh ? `<div style="margin-top:8px"><img src="${form.urlAnh}" style="width:80px;height:80px;object-fit:cover;border:1px solid #eee;border-radius:8px"/></div>` : ""}
  </div>
`;

/* ====== LOAD KH ====== */
async function loadKhachHang() {
  try {
    const res = await getKhachHangById(id);
    const data = res?.data?.data ?? res?.data ?? {};

    rawServer.value = { ...data }; // giữ toàn bộ field gốc từ server (để merge)

    // Set form
    form.id = data.id ?? null;
    form.ma = data.ma ?? "";
    form.hoTen = data.hoTen ?? "";
    form.email = data.email ?? "";
    form.sdt = data.sdt ?? "";
    form.gioiTinh = (data.gioiTinh === true || data.gioiTinh === 1 || data.gioiTinh === "1") ? 1 : 0;
    form.ngaySinh = fDate(data.ngaySinh ?? data.dateOfBirth ?? "");
    form.urlAnh = data.urlAnh ?? data.avatar ?? "";
    form.trangThai = (data.trangThai === true || data.trangThai === 1 || data.trangThai === "1") ? 1 : 0;

    // Parse địa chỉ text -> codes
    parseAddressToCodes(data.diaChi ?? data.address ?? "");
  } catch (err) {
    console.error("❌ Lỗi load khách hàng:", err);
    notify.error("Không thể tải thông tin khách hàng.");
  }
}

/* ===== Parse địa chỉ text -> codes ===== */
function parseAddressToCodes(fullAddress) {
  try {
    if (!fullAddress) return;
    const norm = s => (s || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    const parts = fullAddress.split(",").map(p => p.trim());
    const tinhName  = parts.at(-1) || "";
    const huyenName = parts.at(-2) || "";
    const xaName    = parts.at(-3) || "";

    const p = provincesData.value.find(x =>
      norm(x.name) === norm(tinhName) || norm(tinhName).includes(norm(x.name)) || norm(x.name).includes(norm(tinhName))
    );
    if (p) {
      form.tinhCode = String(p.code);
      const d = (p.districts || []).find(x =>
        norm(x.name) === norm(huyenName) || norm(huyenName).includes(norm(x.name)) || norm(x.name).includes(norm(huyenName))
      );
      if (d) {
        form.huyenCode = String(d.code);
        const w = (d.wards || []).find(x =>
          norm(x.name) === norm(xaName) || norm(xaName).includes(norm(x.name)) || norm(x.name).includes(norm(xaName))
        );
        if (w) form.xaCode = String(w.code);
      }
    }
    form.chiTiet = parts.slice(0, Math.max(0, parts.length - 3)).join(", ");
  } catch (e) {
    console.warn("Parse địa chỉ lỗi:", e);
  }
}

/* ====== SAVE (PUT) ====== */
async function saveKhachHang() {
  // 0) Chặn sửa nếu ngừng hoạt động
  if (isReadOnly.value) {
    throw new Error("Khách hàng ngừng hoạt động — không cho phép cập nhật.");
  }

  // 1) Validate tối thiểu
  if (!form.ma || !form.hoTen || !form.email || !form.sdt) {
    notify.error("Vui lòng điền đầy đủ Mã, Họ tên, Email, SĐT!");
    return;
  }
  if (!form.tinhCode) {
    notify.error("Vui lòng chọn Tỉnh/Thành phố!");
    return;
  }

  try {
    // 2) Ghép địa chỉ text
    const tinh = currentProvince.value?.name;
    const huyen = currentDistrict.value?.name;
    const xa = currentWard.value?.name;
    const diaChi = [form.chiTiet, xa, huyen, tinh].filter(Boolean).join(", ");

    // 3) Base từ server để không mất các field không có trên form (tránh UNIQUE bị null)
    const base = { ...(rawServer.value || {}) };

    // 4) Các field được phép sửa
    const safe = {
      ma:        (form.ma ?? "").trim(),
      hoTen:     (form.hoTen ?? "").trim(),
      sdt:       String(form.sdt ?? "").trim(),
      email:     (form.email ?? "").trim(),
      gioiTinh:  form.gioiTinh ? 1 : 0,
      trangThai: form.trangThai ? 1 : 0,
      ngaySinh:  toYMD(form.ngaySinh),
      urlAnh:    form.urlAnh || base.urlAnh || "",
      diaChi:    diaChi || base.diaChi || "",
    };

    // 5) Nếu người dùng xoá trống nhưng server đang có giá trị unique, GIỮ nguyên base
    ["ma","email","sdt"].forEach(k => { if (!safe[k] && base[k]) safe[k] = base[k]; });

    // 6) Payload cuối: merge (PATCH-giả)
    const payload = { ...base, ...safe };

    // Xoá field chỉ-đọc / không nên gửi
    delete payload.id;
    delete payload.ngayTao;
    delete payload.ngaySua;

    // Log tham khảo
    console.groupCollapsed("🛰️ PUT /khach-hang merged payload");
    console.log("PATH id:", String(id));
    console.log("payload:", j(payload));
    console.groupEnd();

    // 7) Call service PUT
    await updateKhachHang(String(id), payload);

    notify.success("Cập nhật khách hàng thành công!");
    router.push("/admin/khach-hang");
  } catch (err) {
    const status = err?.response?.status;
    const data = err?.response?.data;
    const arrErrors =
      Array.isArray(data?.errors)
        ? data.errors.map((e) => e?.defaultMessage || e?.message || j(e))
        : Array.isArray(data)
        ? data.map((e) => e?.message || j(e))
        : [];

    const msg =
      data?.message ||
      data?.error ||
      (arrErrors.length ? arrErrors.join("; ") : "") ||
      err?.message ||
      "Cập nhật thất bại!";

    console.groupCollapsed("❌ Lỗi cập nhật khách hàng");
    console.error("Status:", status);
    console.error("Response data:", j(data));
    console.error("Axios error:", err);
    console.groupEnd();

    notify.error(`${status ? `Lỗi ${status}: ` : ""}${msg}`);
  }
}

async function confirmSave() {
  // Chặn submit nếu đã ngừng hoạt động
  if (isReadOnly.value) {
    notify.error("Khách hàng đã ngừng hoạt động, không thể sửa!");
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    html: buildPreviewHtml(),
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
    width: 600,
  });

  if (result.isConfirmed) {
    await saveKhachHang();
  }
}
</script>

<style scoped>
.form-label { font-weight: 600; }
.card { border-radius: 12px; }

/* Focus viền vàng cho input/select */
input:focus, select:focus, textarea:focus {
  border-color: #ffc107 !important;
  box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
  outline: none !important;
}
</style>