<template>
  <div class="container-fluid mt-4 px-1">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý khách hàng</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <!-- Ảnh đại diện -->
        <div class="col-md-12 text-center">
          <div
            class="position-relative d-inline-block rounded-circle border border-2 border-secondary-subtle bg-light"
            style="width: 140px; height: 140px; cursor: pointer; overflow: hidden;"
            @click="$refs.fileInput.click()"
          >
            <img
              v-if="previewUrl"
              :src="previewUrl"
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
                />
                <label class="form-check-label" for="gioiTinhNu">Nữ</label>
              </div>
            </div>
          </div>

          <!-- Ngày sinh -->
          <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input v-model="form.ngaySinh" type="date" class="form-control" />
          </div>

          <!-- Địa chỉ (Select có sẵn toàn quốc) -->
          <div class="col-md-4">
            <label class="form-label">Tỉnh/Thành phố</label>
            <select
              class="form-select"
              v-model="form.tinhCode"
              @change="onProvinceChange"
            >
              <option value="">— Chọn Tỉnh/Thành —</option>
              <option v-for="p in provinces" :key="p.code" :value="p.code">
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
              :disabled="!districts.length"
            >
              <option value="">— Chọn Quận/Huyện —</option>
              <option v-for="d in districts" :key="d.code" :value="d.code">
                {{ d.name }}
              </option>
            </select>
          </div>

          <div class="col-md-4">
            <label class="form-label">Xã/Phường</label>
            <select
              class="form-select"
              v-model="form.xaCode"
              :disabled="!wards.length"
            >
              <option value="">— Chọn Xã/Phường —</option>
              <option v-for="w in wards" :key="w.code" :value="w.code">
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
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm khách hàng
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import axios from "axios";
import { createKhachHang } from "@/service/KhachHangService";

const router = useRouter();
const notify = useNotify();

/* ====== STATE FORM ====== */
const form = reactive({
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
  trangThai: 1,  // luôn hoạt động khi thêm mới
});

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
    // Sửa endpoint BE theo hệ thống của bạn
    const res = await axios.post("http://localhost:8080/api/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    // Chuẩn hóa lấy URL
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
  try {
    // API mở công khai: toàn bộ tỉnh/thành kèm quận/huyện + xã/phường
    const res = await fetch("https://provinces.open-api.vn/api/?depth=3");
    if (!res.ok) throw new Error("Fetch provinces failed");
    provincesData.value = await res.json();
  } catch (e) {
    console.error("Không tải được danh mục Tỉnh/TP. Bạn có thể nhập thủ công hoặc bundle JSON cục bộ.", e);
    // Fallback mẫu rất nhỏ (nếu cần). Khuyên bạn thay bằng JSON đầy đủ nội bộ.
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
});

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
const onProvinceChange = () => {
  form.huyenCode = "";
  form.xaCode = "";
};
const onDistrictChange = () => {
  form.xaCode = "";
};

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
    <div><b>Trạng thái</b>: Hoạt động</div>
    ${
      form.urlAnh
        ? `<div style="margin-top:8px"><img src="${form.urlAnh}" style="width:80px;height:80px;object-fit:cover;border:1px solid #eee;border-radius:8px"/></div>`
        : ""
    }
  </div>
`;

/* ====== SAVE ====== */
const addKhachHang = async () => {
  const diaChi = previewAddress();
  const payload = {
    ma: form.ma.trim(),
    hoTen: form.hoTen.trim(),
    email: form.email.trim(),
    sdt: String(form.sdt ?? "").trim(),
    gioiTinh: Number(form.gioiTinh),
    ngaySinh: toYMD(form.ngaySinh),
    diaChi,
    urlAnh: form.urlAnh || null,
    trangThai: 1, // luôn hoạt động khi thêm mới
    taiKhoan: form.email?.trim() || null,
    matKhau: null,
  };

  // GIẢ ĐỊNH service trả về entity đã tạo { data: { id, ... } }
  const res = await createKhachHang(payload);
  // Nếu service của bạn trả khác, đổi lại dòng dưới cho đúng:
  const created = res?.data || res; // tùy response
  return created;
};

const confirmSave = async () => {
  if (!form.ma || !form.hoTen || !form.email || !form.sdt) {
    notify.error("Vui lòng điền đầy đủ Mã, Họ tên, Email, SĐT!");
    return;
  }
  if (!form.tinhCode) {
    notify.error("Vui lòng chọn Tỉnh/Thành phố!");
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận thêm khách hàng?",
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
    try {
      const created = await addKhachHang();
      notify.success("Thêm khách hàng thành công!");

      // Ghim ID để trang danh sách ưu tiên lên đầu
      if (created?.id) {
        sessionStorage.setItem("NEW_KH_ID", String(created.id));
      }

      router.push("/admin/khach-hang");
    } catch (err) {
      console.error("Lỗi khi thêm khách hàng:", err);
      const msg = err?.response?.data?.message || err?.message || "Thêm thất bại!";
      notify.error(msg);
    }
  }
};
</script>

<style scoped>
.form-label { font-weight: 600; }
.card { border-radius: 12px; }
</style>