<template>
  <div class="container-fluid mt-4 px-1">
    <!-- Header -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
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
        <div
          v-if="isReadOnly"
          class="alert alert-warning d-flex align-items-center mb-3"
          role="alert"
        >
          <i class="fa-solid fa-lock me-2"></i>
          Khách hàng này đã ngừng hoạt động. Bạn không thể chỉnh sửa thông tin.
        </div>

        <form
          @submit.prevent="confirmSave"
          :style="isReadOnly ? 'opacity:.9' : ''"
        >
          <!-- Ảnh đại diện -->
          <div class="col-md-12 text-center">
            <div
              class="position-relative d-inline-block rounded-circle border border-2 border-secondary-subtle bg-light"
              :style="
                isReadOnly
                  ? 'width:140px;height:140px;pointer-events:none;opacity:.7;overflow:hidden'
                  : 'width:140px;height:140px;cursor:pointer;overflow:hidden'
              "
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
                disabled
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
              <input
                v-model="form.ngaySinh"
                type="date"
                class="form-control"
                :disabled="isReadOnly"
              />
            </div>

            <!-- Địa chỉ (Select có sẵn toàn quốc) -->
            <div class="col-12 mt-4">
              <h5 class="fw-bold mb-3">
                <i class="fa-solid fa-location-dot me-1 text-warning"></i> Quản
                lý Địa chỉ
                <button
                  v-if="!isAddingNewAddress && !isReadOnly"
                  type="button"
                  class="btn btn-sm btn-outline-warning ms-2"
                  @click="openAddressForm(null, true)"
                >
                  <i class="fa fa-plus me-1"></i> Thêm địa chỉ mới
                </button>
              </h5>

              <div v-if="!isAddingNewAddress">
                <div
                  v-if="newAddresses.length === 0"
                  class="alert alert-info text-center"
                >
                  Chưa có địa chỉ nào được thêm. Vui lòng thêm địa chỉ.
                </div>

                <div
                  v-else
                  class="card shadow-sm p-3"
                  :class="{
                    'border-warning border-3':
                      newAddresses[currentAddressIndex]?.macDinh,
                  }"
                >
                  <div
                    class="d-flex justify-content-between align-items-start mb-3"
                  >
                    <h6 class="fw-bold m-0">
                      Địa chỉ đang xem
                      <span class="fw-normal small text-secondary ms-2">
                        ({{ currentAddressIndex + 1 }}/{{
                          newAddresses.length
                        }})
                      </span>
                      <span
                        v-if="newAddresses[currentAddressIndex]?.macDinh"
                        class="badge bg-warning text-white ms-2"
                        >Mặc Định</span
                      >
                    </h6>
                    <div class="d-flex gap-2">
                      <button
                        type="button"
                        class="btn btn-sm btn-outline-info"
                        @click="openAddressForm(currentAddressIndex, false)"
                        :disabled="isReadOnly"
                      >
                        <i class="fa fa-edit"></i> Sửa
                      </button>
                      <button
                        type="button"
                        class="btn btn-sm btn-outline-danger"
                        @click="confirmDeleteAddress(currentAddressIndex)"
                        :disabled="isReadOnly || newAddresses.length === 1"
                      >
                        <i class="fa fa-trash"></i> Xóa
                      </button>
                    </div>
                  </div>

                  <hr class="mt-0 mb-3" />

                  <div class="row g-3">
                    <div class="col-md-4">
                      <label class="form-label">Tỉnh/Thành phố</label>
                      <select
                        class="form-select"
                        :value="currentAddressForm.tinhCode"
                        disabled
                      >
                        <option value="">— Chọn Tỉnh/Thành —</option>
                        <option
                          v-for="p in provinces"
                          :key="p.code"
                          :value="String(p.code)"
                        >
                          {{ p.name }}
                        </option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Quận/Huyện</label>
                      <select
                        class="form-select"
                        :value="currentAddressForm.huyenCode"
                        disabled
                      >
                        <option value="">— Chọn Quận/Huyện —</option>
                        <option
                          v-for="d in draftDistricts"
                          :key="d.code"
                          :value="String(d.code)"
                        >
                          {{ d.name }}
                        </option>
                      </select>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Xã/Phường</label>
                      <select
                        class="form-select"
                        :value="currentAddressForm.xaCode"
                        disabled
                      >
                        <option value="">— Chọn Xã/Phường —</option>
                        <option
                          v-for="w in draftWards"
                          :key="w.code"
                          :value="String(w.code)"
                        >
                          {{ w.name }}
                        </option>
                      </select>
                    </div>

                    <div class="col-12">
                      <label class="form-label">Địa chỉ cụ thể</label>
                      <input
                        :value="currentAddressForm.chiTiet"
                        type="text"
                        class="form-control"
                        readonly
                      />
                    </div>

                    <div class="col-12">
                      <div class="form-check">
                        <input
                          class="form-check-input"
                          type="checkbox"
                          :checked="currentAddressForm.macDinh"
                          disabled
                        />
                        <label class="form-check-label">
                          Địa chỉ mặc định
                        </label>
                      </div>
                    </div>
                  </div>

                  <div
                    class="d-flex justify-content-center align-items-center gap-3 mt-4"
                    v-if="newAddresses.length > 1"
                  >
                    <button
                      type="button"
                      class="btn btn-outline-secondary btn-sm"
                      @click="goToPreviousAddress"
                      :disabled="currentAddressIndex === 0"
                    >
                      <i class="fa fa-arrow-left"></i> Trước
                    </button>

                    <button
                      type="button"
                      class="btn btn-outline-secondary btn-sm"
                      @click="goToNextAddress"
                      :disabled="
                        currentAddressIndex === newAddresses.length - 1
                      "
                    >
                      Sau <i class="fa fa-arrow-right"></i>
                    </button>
                  </div>
                </div>
              </div>

              <div v-else class="card shadow-sm p-3 border-info">
                <div
                  class="d-flex justify-content-between align-items-center mb-3"
                >
                  <h6 class="fw-bold text-info m-0">
                    {{
                      currentAddressIndex !== null
                        ? "Cập nhật địa chỉ"
                        : "Thêm địa chỉ mới"
                    }}
                  </h6>
                  <button
                    class="btn btn-close"
                    @click="closeAddressForm"
                  ></button>
                </div>

                <div class="row g-3">
                  <div class="col-md-4">
                    <label class="form-label"
                      >Tỉnh/Thành phố <span class="text-danger">*</span></label
                    >
                    <select
                      class="form-select"
                      v-model="currentAddressForm.tinhCode"
                      @change="onDraftProvinceChange"
                    >
                      <option value="">— Chọn —</option>
                      <option
                        v-for="p in provinces"
                        :key="p.code"
                        :value="String(p.code)"
                      >
                        {{ p.name }}
                      </option>
                    </select>
                  </div>

                  <div class="col-md-4">
                    <label class="form-label"
                      >Quận/Huyện <span class="text-danger">*</span></label
                    >
                    <select
                      class="form-select"
                      v-model="currentAddressForm.huyenCode"
                      @change="onDraftDistrictChange"
                      :disabled="!currentAddressForm.tinhCode"
                    >
                      <option value="">— Chọn —</option>
                      <option
                        v-for="d in draftDistricts"
                        :key="d.code"
                        :value="String(d.code)"
                      >
                        {{ d.name }}
                      </option>
                    </select>
                  </div>

                  <div class="col-md-4">
                    <label class="form-label"
                      >Xã/Phường <span class="text-danger">*</span></label
                    >
                    <select
                      class="form-select"
                      v-model="currentAddressForm.xaCode"
                      :disabled="!currentAddressForm.huyenCode"
                    >
                      <option value="">— Chọn —</option>
                      <option
                        v-for="w in draftWards"
                        :key="w.code"
                        :value="String(w.code)"
                      >
                        {{ w.name }}
                      </option>
                    </select>
                  </div>

                  <div class="col-12">
                    <label class="form-label"
                      >Địa chỉ cụ thể <span class="text-danger">*</span></label
                    >
                    <input
                      v-model="currentAddressForm.chiTiet"
                      type="text"
                      class="form-control"
                      placeholder="Số nhà, đường..."
                    />
                  </div>

                  <div class="col-12">
                    <div class="form-check">
                      <input
                        class="form-check-input"
                        type="checkbox"
                        id="chkMacDinhEdit"
                        v-model="currentAddressForm.macDinh"
                        @change="handleDefaultChange"
                      />
                      <label class="form-check-label" for="chkMacDinhEdit">
                        Đặt làm địa chỉ mặc định
                      </label>
                    </div>
                  </div>

                  <div class="col-12 text-end mt-3">
                    <button
                      class="btn btn-secondary me-2"
                      @click="closeAddressForm"
                    >
                      Hủy bỏ
                    </button>
                    <button
                      class="btn btn-warning text-white"
                      @click="saveAddressToDraft"
                    >
                      <i class="fa fa-save"></i>
                      {{
                        currentAddressIndex !== null
                          ? "Cập nhật"
                          : "Thêm vào danh sách"
                      }}
                    </button>
                  </div>
                </div>
              </div>
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

import {
  getDiaChiByKhachHangId,
  updateDiaChi,
  createDiaChi,
  deleteDiaChi,
} from "@/service/DiaChiService";

const route = useRoute();
const router = useRouter();
const notify = useNotify();
const id = route.params.id || route.query.id;
const currentDiaChiId = ref(null);

/* ====== RAW SERVER (để merge, tránh UNIQUE NULL) ====== */
const rawServer = ref({});
const j = (o) => {
  try {
    return JSON.stringify(o, null, 2);
  } catch {
    return String(o);
  }
};

/* ====== STATE FORM ====== */
const form = reactive({
  id: null,
  ma: "",
  hoTen: "",
  email: "",
  sdt: "",
  gioiTinh: 1, // 1=Nam, 0=Nữ
  ngaySinh: "", // yyyy-MM-dd
  urlAnh: "", // URL ảnh sau upload // địa chỉ dạng code
  trangThai: 1, // 1 hoạt động, 0 ngừng
});

const newAddresses = ref([]); // Danh sách nháp
const currentAddressIndex = ref(null); // index đang sửa (null = thêm mới)
const isAddingNewAddress = ref(false); // Flag hiển thị form chi tiết
const currentAddressForm = reactive({
  id: null, // Dùng để lưu ID nếu đang sửa địa chỉ đã có trên DB
  tinhCode: "",
  huyenCode: "",
  xaCode: "",
  chiTiet: "",
  macDinh: false,
});

const ready = ref(false);

const isReadOnly = computed(() => Number(form.trangThai) === 0);

/* ====== ẢNH ====== */
const uploading = ref(false);
const previewUrl = ref("");

const handleFileUpload = async (event) => {
  const file = event.target.files?.[0];
  if (!file) return; // Preview tạm

  previewUrl.value = URL.createObjectURL(file);

  const formData = new FormData();
  formData.append("file", file);

  uploading.value = true;
  try {
    // Endpoint upload giống form thêm của bạn
    const res = await axios.post(
      "http://localhost:8080/admin/upload",
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );

    form.urlAnh = res?.data?.url ?? res?.data?.secure_url ?? res?.data ?? "";
    if (form.urlAnh) previewUrl.value = form.urlAnh;
  } catch (error) {
    notify.error("Upload ảnh thất bại!");
    console.error(error);
  } finally {
    uploading.value = false;
  }
};

const provincesData = ref([]);

onMounted(async () => {
  if (!id) {
    notify.error("Thiếu ID khách hàng trên URL!");
    return;
  }
  await loadProvinces();

  await loadKhachHang();

  if (newAddresses.value.length > 0) {
    syncDetailToForm();
  }

  ready.value = true;
});

async function loadProvinces() {
  try {
    const res = await fetch("https://provinces.open-api.vn/api/?depth=3");
    if (!res.ok) throw new Error("Fetch provinces failed");
    provincesData.value = await res.json();
  } catch (e) {
    console.error("Không tải được danh mục Tỉnh/TP. Dùng fallback.", e); // Fallback mẫu nhỏ
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
const currentProvince = computed(
  () =>
    provincesData.value.find((p) => String(p.code) === String(form.tinhCode)) ||
    null
);
const currentDistrict = computed(() => {
  if (!currentProvince.value) return null;
  return (
    currentProvince.value.districts?.find(
      (d) => String(d.code) === String(form.huyenCode)
    ) || null
  );
});
const currentWard = computed(() => {
  if (!currentDistrict.value) return null;
  return (
    currentDistrict.value.wards?.find(
      (w) => String(w.code) === String(form.xaCode)
    ) || null
  );
});

/* Danh sách cho 3 select */
const provinces = computed(() => provincesData.value);
const districts = computed(() => currentProvince.value?.districts ?? []);
const wards = computed(() => currentDistrict.value?.wards ?? []);

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
  } catch {
    return d;
  }
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
    <div><b>Trạng thái</b>: ${
  Number(form.trangThai) === 1 ? "Còn hoạt động" : "Ngừng hoạt động"
}</div>
    ${
  form.urlAnh
    ? `<div style="margin-top:8px"><img src="${form.urlAnh}" style="width:80px;height:80px;object-fit:cover;border:1px solid #eee;border-radius:8px"/></div>`
    : ""
}
  </div>
`;

// 1. COMPUTED cho Form Địa chỉ chi tiết
const draftCurrentProvince = computed(
  () =>
    provincesData.value.find(
      (p) => String(p.code) === String(currentAddressForm.tinhCode)
    ) || null
);
const draftCurrentDistrict = computed(() => {
  if (!draftCurrentProvince.value) return null;
  return (
    draftCurrentProvince.value.districts?.find(
      (d) => String(d.code) === String(currentAddressForm.huyenCode)
    ) || null
  );
});
const draftCurrentWard = computed(() => {
  if (!draftCurrentDistrict.value) return null;
  return (
    draftCurrentDistrict.value.wards?.find(
      (w) => String(w.code) === String(currentAddressForm.xaCode)
    ) || null
  );
});

const draftDistricts = computed(
  () => draftCurrentProvince.value?.districts ?? []
);
const draftWards = computed(() => draftCurrentDistrict.value?.wards ?? []);

// 2. FORM RESET & VALIDATION
const resetAddressForm = (makeDefault = false) => {
  currentAddressForm.id = null;
  currentAddressForm.tinhCode = "";
  currentAddressForm.huyenCode = "";
  currentAddressForm.xaCode = "";
  currentAddressForm.chiTiet = "";
  currentAddressForm.macDinh = makeDefault && newAddresses.value.length === 0; // Nếu là địa chỉ đầu tiên
};

const isAddressFormValid = computed(() => {
  return (
    currentAddressForm.tinhCode &&
    currentAddressForm.huyenCode &&
    currentAddressForm.xaCode &&
    currentAddressForm.chiTiet.trim()
  );
});

// 3. HANDLERS
const onDraftProvinceChange = () => {
  currentAddressForm.huyenCode = "";
  currentAddressForm.xaCode = "";
};
const onDraftDistrictChange = () => {
  currentAddressForm.xaCode = "";
};

const openAddressForm = (index = null, isNew = false) => {
  isAddingNewAddress.value = true;
  currentAddressIndex.value = index;

  if (isNew || index === null) {
    // THÊM MỚI
    resetAddressForm(true);
  } else {
    // SỬA
    const addressToEdit = newAddresses.value[index];
    Object.assign(currentAddressForm, {
      id: addressToEdit.id,
      tinhCode: addressToEdit.tinhCode,
      huyenCode: addressToEdit.huyenCode,
      xaCode: addressToEdit.xaCode,
      chiTiet: addressToEdit.diaChiCuThe,
      macDinh: addressToEdit.macDinh,
    });
  }
};

const closeAddressForm = () => {
  isAddingNewAddress.value = false;

  if (newAddresses.value.length > 0) {
    if (currentAddressIndex.value === null) {
      currentAddressIndex.value = 0;
    }
    syncDetailToForm();
  } else {
    currentAddressIndex.value = null;
    resetAddressForm(false);
  }
};

/* ====== LOGIC LƯU ĐỊA CHỈ TẠM (DRAFT) ====== */
const saveAddressToDraft = () => {
  // 1. Validate cơ bản
  if (!isAddressFormValid.value) {
    notify.error("Vui lòng điền đầy đủ thông tin địa chỉ!");
    return;
  }

  // 2. TÌM TÊN TỪ MÃ CODE (QUAN TRỌNG: Để gửi lên server dạng chữ)
  // Lấy object tỉnh/huyện/xã từ dữ liệu Open API dựa trên code đang chọn
  const tinhObj = provincesData.value.find(
    (p) => String(p.code) === String(currentAddressForm.tinhCode)
  );
  const huyenObj = tinhObj?.districts?.find(
    (d) => String(d.code) === String(currentAddressForm.huyenCode)
  );
  const xaObj = huyenObj?.wards?.find(
    (w) => String(w.code) === String(currentAddressForm.xaCode)
  );

  const tinhName = tinhObj?.name || "";
  const huyenName = huyenObj?.name || "";
  const xaName = xaObj?.name || "";

  // 3. Tạo object địa chỉ chuẩn hóa
  const addressPayload = {
    id: currentAddressForm.id, // Giữ ID nếu là sửa địa chỉ cũ

    // CẶP DỮ LIỆU ĐỂ HIỂN THỊ UI (DROPDOWN)
    tinhCode: currentAddressForm.tinhCode,
    huyenCode: currentAddressForm.huyenCode,
    xaCode: currentAddressForm.xaCode,

    // CẶP DỮ LIỆU ĐỂ GỬI SERVER (LƯU DB)
    thanhPho: tinhName,
    huyen: huyenName,
    xa: xaName,
    diaChiCuThe: currentAddressForm.chiTiet.trim(),

    macDinh: currentAddressForm.macDinh,
  };

  // 4. Xử lý Logic Mặc định (Chỉ 1 cái được true)
  if (addressPayload.macDinh) {
    newAddresses.value.forEach((addr) => (addr.macDinh = false));
  } else if (newAddresses.value.length === 0) {
    // Nếu đây là địa chỉ đầu tiên, bắt buộc là mặc định
    addressPayload.macDinh = true;
  }

  // 5. Cập nhật vào mảng
  if (currentAddressIndex.value !== null) {
    // Đang sửa
    newAddresses.value[currentAddressIndex.value] = addressPayload;
    notify.success("Cập nhật địa chỉ thành công!");
  } else {
    // Thêm mới
    newAddresses.value.push(addressPayload);
    notify.success("Thêm địa chỉ vào danh sách thành công!");
  }

  // Đảm bảo luôn có 1 cái mặc định nếu danh sách > 0
  if (
    newAddresses.value.length > 0 &&
    !newAddresses.value.some((a) => a.macDinh)
  ) {
    newAddresses.value[0].macDinh = true;
  }

  closeAddressForm();
};

const handleDefaultChange = () => {
  if (currentAddressForm.macDinh) {
  } else {
    if (newAddresses.value.length > 0 || currentAddressIndex.value !== null) {
      const isOnlyDefault =
        newAddresses.value.filter((a) => a.macDinh).length === 1 &&
        newAddresses.value[currentAddressIndex.value]?.macDinh;

      if (newAddresses.value.length === 1 || isOnlyDefault) {
        currentAddressForm.macDinh = true;
        notify.warning("Phải có ít nhất một địa chỉ mặc định.");
      }
    }
  }
};

const confirmDeleteAddress = async (index) => {
  if (newAddresses.value.length === 1) {
    notify.error("Khách hàng phải có ít nhất một địa chỉ.");
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận xóa?",
    text: "Bạn có chắc chắn muốn xóa địa chỉ này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, xóa!",
    cancelButtonText: "Hủy",
    reverseButtons: true,
  });

  if (result.isConfirmed) {
    const addr = newAddresses.value[index];

    try {
      // Nếu địa chỉ đã có id thì gọi API delete
      if (addr.id) {
        await deleteDiaChi(addr.id);
        notify.success("Đã xóa địa chỉ khỏi cơ sở dữ liệu.");
      } else {
        notify.success("Đã xóa địa chỉ khỏi danh sách nháp.");
      }

      // Xóa khỏi array local
      newAddresses.value.splice(index, 1);

      // Đảm bảo luôn có 1 địa chỉ mặc định
      if (
        !newAddresses.value.some((a) => a.macDinh) &&
        newAddresses.value.length > 0
      ) {
        newAddresses.value[0].macDinh = true;
      }
    } catch (err) {
      console.error("❌ Lỗi xóa địa chỉ:", err);
      notify.error("Xóa địa chỉ thất bại!");
    }
  }
};

const syncDetailToForm = () => {
  const index = currentAddressIndex.value;
  if (index === null || !newAddresses.value[index]) {
    // Nếu không có địa chỉ hoặc index không hợp lệ, reset form
    resetAddressForm(false);
    return;
  }

  const addressToView = newAddresses.value[index];

  console.log("Địa chỉ gốc:", addressToView); // Kiểm tra xem các Code đã tồn tại chưa

  Object.assign(currentAddressForm, {
    id: addressToView.id,
    tinhCode: addressToView.tinhCode,
    huyenCode: addressToView.huyenCode,
    xaCode: addressToView.xaCode,
    chiTiet: addressToView.diaChiCuThe,
    macDinh: addressToView.macDinh,
  });

  console.log(
    "Form sau khi gán:",
    currentAddressForm.tinhCode,
    currentAddressForm.huyenCode
  ); // Kiểm tra dữ liệu trong Form

  isAddingNewAddress.value = false;
};

const formatAddressForDisplay = (address) => {
  return [address.diaChiCuThe, address.xa, address.huyen, address.thanhPho]
    .filter(Boolean)
    .join(", ");
};

const goToPreviousAddress = () => {
  if (currentAddressIndex.value > 0) {
    currentAddressIndex.value--;
    syncDetailToForm();
  }
};

const goToNextAddress = () => {
  if (currentAddressIndex.value < newAddresses.value.length - 1) {
    currentAddressIndex.value++;
    syncDetailToForm();
  }
};

/* ====== LOAD DỮ LIỆU TỪ SERVER ====== */
async function loadKhachHang() {
  try {
    // 1. Lấy thông tin KH
    const res = await getKhachHangById(id);
    const data = res?.data?.data ?? res?.data ?? {};
    rawServer.value = { ...data };

    // Fill Form Khách hàng
    form.id = data.id;
    form.ma = data.ma;
    form.hoTen = data.hoTen;
    form.email = data.email;
    form.sdt = data.sdt;
    form.gioiTinh = data.gioiTinh === true || data.gioiTinh == 1 ? 1 : 0;
    form.ngaySinh = fDate(data.ngaySinh);
    form.urlAnh = data.urlAnh;
    form.trangThai = data.trangThai === true || data.trangThai == 1 ? 1 : 0;

    // 2. Lấy danh sách Địa chỉ
    const resDiaChi = await getDiaChiByKhachHangId(id);
    const serverAddressList = Array.isArray(resDiaChi?.data)
      ? resDiaChi.data
      : [];

    if (provincesData.value.length > 0) {
      newAddresses.value = serverAddressList.map((addr) => {
        let tCode = "",
          hCode = "",
          xCode = "";

        // Tìm Tỉnh
        const p = provincesData.value.find(
          (p) => p.name === addr.thanhPho || p.name.includes(addr.thanhPho)
        );
        if (p) {
          tCode = String(p.code);
          // Tìm Huyện
          const d = p.districts?.find(
            (d) => d.name === addr.huyen || d.name.includes(addr.huyen)
          );
          if (d) {
            hCode = String(d.code);
            // Tìm Xã
            const w = d.wards?.find(
              (w) => w.name === addr.xa || w.name.includes(addr.xa)
            );
            if (w) xCode = String(w.code);
          }
        }

        return {
          id: addr.id,
          macDinh: addr.macDinh,
          diaChiCuThe: addr.diaChiCuThe,

          // Dữ liệu Tên (để gửi lại server)
          thanhPho: addr.thanhPho,
          huyen: addr.huyen,
          xa: addr.xa,

          // Dữ liệu Code (để hiển thị Dropdown)
          tinhCode: tCode,
          huyenCode: hCode,
          xaCode: xCode,
        };
      });
    } else {
      // Fallback nếu chưa load được API tỉnh thành
      newAddresses.value = serverAddressList;
    }

    // Nếu có địa chỉ, chọn cái mặc định để hiển thị
    if (newAddresses.value.length > 0) {
      const idx = newAddresses.value.findIndex((a) => a.macDinh);
      currentAddressIndex.value = idx >= 0 ? idx : 0;
    }
  } catch (err) {
    console.error("Lỗi load KH:", err);
    notify.error("Lỗi tải dữ liệu!");
  }
}

// Hàm mới để tìm Code cho danh sách địa chỉ (TƯƠNG TỰ parseAddressDataToCodes cũ)
function parseAddressNamesToCodes(addressList) {
  if (!provincesData.value.length) return addressList; // Chưa load data

  return addressList.map((addressData) => {
    let tinhCode = "";
    let huyenCode = "";
    let xaCode = "";

    const p = provincesData.value.find((p) => p.name === addressData.thanhPho);
    if (p) {
      tinhCode = String(p.code);
      const d = (p.districts || []).find((d) => d.name === addressData.huyen);
      if (d) {
        huyenCode = String(d.code);
        const w = (d.wards || []).find((w) => w.name === addressData.xa);
        if (w) xaCode = String(w.code);
      }
    }

    // Trả về địa chỉ đã bổ sung các trường code
    return {
      ...addressData,
      tinhCode: tinhCode,
      huyenCode: huyenCode,
      xaCode: xaCode,
    };
  });
}

/* ===== ✅ SỬA: Đổi tên hàm thành parseAddressDataToCodes (vì nhận object data) ===== */
function parseAddressDataToCodes(addressData) {
  // ✅ SỬA: Kiểm tra object rỗng
  if (!addressData || Object.keys(addressData).length === 0) return; // Tìm Tỉnh/Thành

  const p = provincesData.value.find((p) => p.name === addressData.thanhPho);
  if (p) {
    form.tinhCode = String(p.code); // Tìm Huyện/Quận
    const d = (p.districts || []).find((d) => d.name === addressData.huyen);
    if (d) {
      form.huyenCode = String(d.code); // Tìm Xã/Phường
      const w = (d.wards || []).find((w) => w.name === addressData.xa);
      if (w) form.xaCode = String(w.code);
    }
  } // Đặt địa chỉ chi tiết

  form.chiTiet = addressData.diaChiCuThe ?? "";
}

/* ====== SAVE (PUT) KHÁCH HÀNG & ĐỊA CHỈ ====== */
async function saveKhachHang() {
  // 1. Check ReadOnly
  if (isReadOnly.value) {
    notify.error("Khách hàng ngừng hoạt động — không cho phép cập nhật.");
    return;
  }

  // 2. Validate Form Khách Hàng
  if (!form.ma || !form.hoTen || !form.email || !form.sdt) {
    notify.error("Vui lòng điền đầy đủ Mã, Họ tên, Email, SĐT!");
    return;
  }

  // 3. Validate Danh sách địa chỉ
  if (newAddresses.value.length === 0) {
    notify.error("Khách hàng phải có ít nhất một địa chỉ!");
    return;
  }
  // Đảm bảo logic mặc định lần cuối
  if (!newAddresses.value.some((addr) => addr.macDinh)) {
    newAddresses.value[0].macDinh = true;
  }

  try {
    const khachHangId = route.params.id || route.query.id;

    // BƯỚC A: XỬ LÝ LƯU ĐỊA CHỈ (QUAN TRỌNG)
    const addressPromises = newAddresses.value.map((addr) => {
      // Payload gửi đi chỉ cần tên (Server lưu tên), ID và trạng thái
      const apiPayload = {
        diaChiCuThe: addr.diaChiCuThe,
        xa: addr.xa, // Tên xã
        huyen: addr.huyen, // Tên huyện
        thanhPho: addr.thanhPho, // Tên thành phố
        macDinh: addr.macDinh,
      };

      if (addr.id) {
        // TRƯỜNG HỢP 1: ĐỊA CHỈ CŨ (Có ID) -> GỌI PUT
        return updateDiaChi(addr.id, apiPayload);
      } else {
        const createPayload = {
          ...apiPayload,
          khachHang: { id: khachHangId },
        };
        return createDiaChi(createPayload);
      }
    });

    // Chờ tất cả địa chỉ được lưu xong
    await Promise.all(addressPromises);

    // BƯỚC B: CẬP NHẬT THÔNG TIN KHÁCH HÀNG
    const defaultAddress =
      newAddresses.value.find((addr) => addr.macDinh) || newAddresses.value[0];
    const diaChiFullText = [
      defaultAddress.diaChiCuThe,
      defaultAddress.xa,
      defaultAddress.huyen,
      defaultAddress.thanhPho,
    ]
      .filter(Boolean)
      .join(", ");

    const payloadKH = {
      ...(rawServer.value || {}), // Merge dữ liệu gốc
      ma: form.ma,
      hoTen: form.hoTen,
      sdt: form.sdt,
      email: form.email,
      gioiTinh: form.gioiTinh,
      trangThai: form.trangThai,
      ngaySinh: toYMD(form.ngaySinh),
      urlAnh: form.urlAnh,
      diaChi: diaChiFullText, // Cập nhật field địa chỉ hiển thị nhanh
    };

    // Loại bỏ các trường thừa không nên gửi lên PUT
    delete payloadKH.id;
    delete payloadKH.ngayTao;
    delete payloadKH.ngaySua;
    delete payloadKH.danhSachDiaChi;

    // Gọi API update khách hàng
    await updateKhachHang(String(id), payloadKH);

    notify.success("Lưu thay đổi thành công!");

    // Load lại dữ liệu để đồng bộ ID mới của các địa chỉ vừa thêm
    await loadKhachHang();
    router.push({ name: "khachHang" });
  } catch (err) {
    console.error("❌ Lỗi Save:", err);
    const msg = err?.response?.data?.message || "Cập nhật thất bại!";
    notify.error(msg);
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
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}

/* Focus viền vàng cho input/select */
input:focus,
select:focus,
textarea:focus {
  border-color: #ffc107 !important;
  box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
  outline: none !important;
}
</style>
