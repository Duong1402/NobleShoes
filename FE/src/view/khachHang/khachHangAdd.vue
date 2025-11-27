<template>
  <div class="container-fluid mt-4 px-1">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý khách hàng</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="col-md-12 text-center">
          <div
            class="position-relative d-inline-block rounded-circle border border-2 border-secondary-subtle bg-light"
            style="
              width: 140px;
              height: 140px;
              cursor: pointer;
              overflow: hidden;
            "
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

          <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input v-model="form.ngaySinh" type="date" class="form-control" />
          </div>

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

          <div class="card shadow p-4 mt-3 col-12">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5 class="m-0 text-primary">
                🏠 {{ isAddingNewAddress ? "Thêm Địa chỉ Mới" : "Địa chỉ" }}
                <span
                  v-if="newAddresses.length > 0 && !isAddingNewAddress"
                  class="fw-normal small text-secondary ms-2"
                >
                  ({{ currentAddressIndex + 1 }}/{{ newAddresses.length }})
                </span>
                <span
                  v-if="currentDraftAddress?.macDinh && !isAddingNewAddress"
                  class="badge bg-success ms-2"
                  >Mặc định</span
                >
              </h5>

              <button
                type="button"
                class="btn btn-outline-primary"
                @click="openAddressForm()"
                v-if="!isAddingNewAddress"
              >
                <i class="fa fa-plus me-1"></i> Thêm địa chỉ khác
              </button>
            </div>

            <hr class="mt-0 mb-3" />

            <div
              v-if="currentDraftAddress || isAddingNewAddress"
              class="p-3 border rounded bg-light"
            >
              <div class="row g-3">
                <div class="col-md-4">
                  <label class="form-label">Tỉnh/Thành phố *</label>
                  <select
                    class="form-select"
                    v-model="currentAddressForm.tinhCode"
                    @change="onNewProvinceChange"
                  >
                    <option value="">— Chọn Tỉnh/Thành —</option>
                    <option
                      v-for="p in provinces"
                      :key="p.code"
                      :value="p.code"
                    >
                      {{ p.name }}
                    </option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label">Quận/Huyện</label>
                  <select
                    class="form-select"
                    v-model="currentAddressForm.huyenCode"
                    @change="onNewDistrictChange"
                    :disabled="!districts.length"
                  >
                    <option value="">— Chọn Quận/Huyện —</option>
                    <option
                      v-for="d in districts"
                      :key="d.code"
                      :value="d.code"
                    >
                      {{ d.name }}
                    </option>
                  </select>
                </div>

                <div class="col-md-4">
                  <label class="form-label">Xã/Phường</label>
                  <select
                    class="form-select"
                    v-model="currentAddressForm.xaCode"
                    :disabled="!wards.length"
                  >
                    <option value="">— Chọn Xã/Phường —</option>
                    <option v-for="w in wards" :key="w.code" :value="w.code">
                      {{ w.name }}
                    </option>
                  </select>
                </div>

                <div class="col-12">
                  <label class="form-label">Địa chỉ cụ thể *</label>
                  <input
                    v-model.trim="currentAddressForm.chiTiet"
                    type="text"
                    class="form-control"
                    placeholder="Số nhà, thôn, đường..."
                  />
                </div>

                <div
                  class="col-12 d-flex justify-content-between align-items-center pt-2"
                >
                  <div>
                    <div class="form-check">
                      <input
                        class="form-check-input"
                        type="checkbox"
                        v-model="currentAddressForm.macDinh"
                        @change="handleDefaultChange"
                        id="defaultCheck"
                      />
                      <label class="form-check-label" for="defaultCheck">
                        Đặt làm địa chỉ mặc định
                      </label>
                    </div>
                  </div>

                  <div class="d-flex gap-2">
                    <button
                      type="button"
                      class="btn btn-secondary"
                      @click="
                        isAddingNewAddress
                          ? closeAddressForm()
                          : deleteAddress(currentDraftAddress.id)
                      "
                      v-if="newAddresses.length > 0"
                    >
                      {{ isAddingNewAddress ? "Hủy" : "Xóa Địa chỉ" }}
                    </button>
                    <button
                      type="button"
                      class="btn btn-primary"
                      @click="saveAddressToDraft()"
                    >
                      {{ isAddingNewAddress ? "Lưu Địa chỉ" : "Cập nhật" }}
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div
              class="d-flex justify-content-center align-items-center gap-3 mt-4"
            >
              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="goToPreviousAddress"
                :disabled="currentAddressIndex === 0 || isAddingNewAddress"
              >
                <i class="fa fa-arrow-left"></i> Trước
              </button>

              <button
                type="button"
                class="btn btn-outline-secondary"
                @click="goToNextAddress"
                :disabled="
                  currentAddressIndex === newAddresses.length - 1 ||
                  isAddingNewAddress
                "
              >
                Sau <i class="fa fa-arrow-right"></i>
              </button>
            </div>

            <div
              v-if="newAddresses.length === 0"
              class="text-center p-3 text-muted"
            >
              <p>Vui lòng thêm địa chỉ đầu tiên.</p>
            </div>
          </div>
        </div>

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
import { reactive, ref, computed, onMounted, watch } from "vue"; // Thêm 'watch'
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import axios from "axios";
import { createKhachHang } from "@/service/KhachHangService";
import {
  createDiaChi,
  // ...
} from "@/service/DiaChiService";

const router = useRouter();
const notify = useNotify();

/* ====== STATE FORM KHÁCH HÀNG ====== */
const form = reactive({
  ma: "",
  hoTen: "",
  email: "",
  sdt: "",
  gioiTinh: 1, // 1=Nam, 0=Nữ
  ngaySinh: "", // yyyy-MM-dd
  urlAnh: "", // URL ảnh sau upload
  trangThai: 1, // luôn hoạt động khi thêm mới
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
    const res = await axios.post(
      "http://localhost:8080/admin/upload",
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );

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

/* ====== QUẢN LÝ DANH SÁCH ĐỊA CHỈ TRƯỚC KHI LƯU KHÁCH HÀNG (MỚI) ====== */

const provincesData = ref([]);
const newAddresses = ref([]);

// 🟢 MỚI: Theo dõi index của địa chỉ đang được hiển thị trên form
const currentAddressIndex = ref(0);

// 🟢 MỚI: Dùng để xác định form đang ở chế độ 'Thêm mới' hay 'Sửa'
const isAddingNewAddress = ref(false);

const currentAddressForm = reactive({
  tinhCode: "",
  huyenCode: "",
  xaCode: "",
  chiTiet: "",
  macDinh: false,
  id: null,
});

// 🟢 MỚI: Lấy địa chỉ hiện tại từ danh sách nháp
const currentDraftAddress = computed(() => {
  if (isAddingNewAddress.value) return currentAddressForm;
  if (newAddresses.value.length === 0) return null;
  // Đảm bảo index nằm trong phạm vi
  const index = Math.min(
    currentAddressIndex.value,
    newAddresses.value.length - 1
  );
  return newAddresses.value[index];
});

/* ------------------------------------------------ */
/* --- Logic Đồng bộ Dữ liệu Địa chỉ ra Form --- */
/* ------------------------------------------------ */

// 🟢 MỚI: Hàm tìm Code từ Tên (Dùng cho logic sửa địa chỉ)
// LƯU Ý: Đây là logic tạm thời vì API của bạn chỉ trả về Tên cho draft,
// không phải CODE. Bạn cần sửa lại nếu BE của bạn trả về CODE
const findCodeByName = (name, type) => {
  if (!name) return "";
  let target = null;
  if (type === "tinh") {
    target = provincesData.value.find((p) => p.name === name);
  } else if (type === "huyen" && currentProvince.value) {
    target = currentProvince.value.districts.find((d) => d.name === name);
  } else if (type === "xa" && currentDistrict.value) {
    target = currentDistrict.value.wards.find((w) => w.name === name);
  }
  return target?.code || "";
};

// 🟢 MỚI: Đồng bộ địa chỉ đang được chọn (currentDraftAddress) vào currentAddressForm
const resetAddressForm = (makeDefault = true) => {
  currentAddressForm.tinhCode = "";
  currentAddressForm.huyenCode = "";
  currentAddressForm.xaCode = "";
  currentAddressForm.chiTiet = "";
  currentAddressForm.macDinh = makeDefault && newAddresses.value.length === 0;
  currentAddressForm.id = null;
};

const syncAddressToForm = (address) => {
  if (!address) {
    resetAddressForm();
    return;
  }
  // Nếu đang ở chế độ sửa, phải tìm lại CODE dựa trên TÊN đã lưu
  currentAddressForm.tinhCode = findCodeByName(address.thanhPho, "tinh");
  currentAddressForm.huyenCode = findCodeByName(address.huyen, "huyen");
  currentAddressForm.xaCode = findCodeByName(address.xa, "xa");

  // Nếu không tìm thấy code (vì data chưa load kịp hoặc logic findCodeByName lỗi),
  // sẽ cần phải xử lý thêm ở đây. Tạm thời cứ gán Tên
  if (!currentAddressForm.tinhCode) {
    // Nếu không tìm thấy code, set form về rỗng để người dùng nhập lại hoặc đợi load
    currentAddressForm.tinhCode = "";
  }

  currentAddressForm.chiTiet = address.diaChiCuThe;
  currentAddressForm.macDinh = address.macDinh;
  currentAddressForm.id = address.id;
};

// 🟢 MỚI: Watch để đồng bộ form khi index thay đổi hoặc list được cập nhật
watch(
  [currentDraftAddress, isAddingNewAddress, provincesData], // Theo dõi cả provincesData để xử lý khi dữ liệu tỉnh load xong
  () => {
    if (isAddingNewAddress.value) {
      // Khi đang thêm mới, không đồng bộ, giữ nguyên form reset
      return;
    }
    // Nếu chuyển sang xem/sửa (và không phải thêm mới), thì đồng bộ địa chỉ
    syncAddressToForm(currentDraftAddress.value);
  },
  { deep: true, immediate: true }
);

/* ------------------------------------------------ */
/* --- Logic Chuyển đổi và Thao tác --- */
/* ------------------------------------------------ */

// Hàm hiển thị form nhập địa chỉ mới
const openAddressForm = () => {
  isAddingNewAddress.value = true;
  currentAddressIndex.value = newAddresses.value.length; // Chuyển index tới cuối
  resetAddressForm();
};

// Hàm đóng form nhập địa chỉ mới (chuyển về xem địa chỉ mặc định/đầu tiên)
const closeAddressForm = () => {
  isAddingNewAddress.value = false;
  currentAddressIndex.value = 0; // Luôn quay về địa chỉ đầu tiên
  // Đồng bộ lại form với địa chỉ ở index 0
  syncAddressToForm(newAddresses.value[0]);
};

// Hàm Lưu Địa chỉ vào danh sách nháp (Dùng cho cả Thêm và Sửa)
const saveAddressToDraft = () => {
  if (!currentAddressForm.tinhCode || !currentAddressForm.chiTiet) {
    notify.error("Vui lòng nhập đủ Tỉnh/Thành phố và Địa chỉ cụ thể.");
    return;
  }

  const tinh = currentProvince.value?.name || "";
  const huyen = currentDistrict.value?.name || "";
  const xa = currentWard.value?.name || "";

  // Tạo đối tượng địa chỉ nháp mới/cập nhật
  const updatedDraftAddress = {
    // Nếu đang sửa (có ID), dùng ID đó, không thì tạo UUID tạm thời
    id: currentAddressForm.id || crypto.randomUUID(),
    thanhPho: tinh,
    huyen: huyen,
    xa: xa,
    diaChiCuThe: currentAddressForm.chiTiet.trim(),
    macDinh: currentAddressForm.macDinh,
  };

  if (updatedDraftAddress.macDinh) {
    // Nếu địa chỉ này được chọn làm mặc định, gỡ mặc định của tất cả các địa chỉ khác
    newAddresses.value.forEach((addr) => (addr.macDinh = false));
  }

  if (isAddingNewAddress.value) {
    // Trường hợp THÊM MỚI
    newAddresses.value.push(updatedDraftAddress);
    // Chuyển sang chế độ xem/sửa địa chỉ vừa thêm
    isAddingNewAddress.value = false;
    currentAddressIndex.value = newAddresses.value.length - 1;
  } else {
    // Trường hợp SỬA: Tìm và thay thế theo index đang xem
    const index = currentAddressIndex.value;
    if (index >= 0 && index < newAddresses.value.length) {
      newAddresses.value[index] = updatedDraftAddress;
    }
  }

  // Đảm bảo có ít nhất một địa chỉ mặc định sau khi lưu/cập nhật
  if (
    !newAddresses.value.some((a) => a.macDinh) &&
    newAddresses.value.length > 0
  ) {
    newAddresses.value[0].macDinh = true;
  }

  notify.success(
    currentAddressForm.id
      ? "Cập nhật địa chỉ thành công."
      : "Thêm địa chỉ thành công."
  );

  // Form sẽ tự đồng bộ nhờ watch, không cần gọi syncAddressToForm()
};

// Hàm Xóa Địa chỉ
const deleteAddress = (addressId) => {
  if (!confirm("Bạn có chắc chắn muốn xóa địa chỉ này khỏi danh sách?")) return;

  const indexToDelete = newAddresses.value.findIndex((a) => a.id === addressId);

  if (indexToDelete !== -1) {
    const isDefault = newAddresses.value[indexToDelete].macDinh;
    newAddresses.value.splice(indexToDelete, 1);

    // Nếu list rỗng, chuyển sang chế độ thêm mới
    if (newAddresses.value.length === 0) {
      openAddressForm();
      return;
    }

    // Nếu xóa địa chỉ mặc định, phải chọn địa chỉ đầu tiên còn lại làm mặc định
    if (isDefault) {
      newAddresses.value[0].macDinh = true;
    }

    // Điều chỉnh index: Nếu index bị xóa là index cuối, lùi lại 1
    if (
      currentAddressIndex.value > 0 &&
      currentAddressIndex.value >= newAddresses.value.length
    ) {
      currentAddressIndex.value = newAddresses.value.length - 1;
    }

    // Đồng bộ lại form với địa chỉ mới (hoặc địa chỉ mới ở index cũ)
    syncAddressToForm(newAddresses.value[currentAddressIndex.value]);
  }
};

// Hàm Đặt Mặc Định
// const setDefaultAddress = () => {
//   if (currentDraftAddress.value && !isAddingNewAddress.value) {
//     newAddresses.value.forEach((addr) => {
//       addr.macDinh = addr.id === currentDraftAddress.value.id;
//     });
//     // Đồng bộ lại trạng thái checkbox trong form
//     currentAddressForm.macDinh = true;
//     notify.success("Đặt địa chỉ mặc định thành công.");
//   }
// };
// Xử lý Checkbox đặt mặc định
const handleDefaultChange = () => {
  // Nếu đang ở chế độ THÊM MỚI
  if (isAddingNewAddress.value) {
    if (currentAddressForm.macDinh) {
      // Khi thêm mới và chọn mặc định, gỡ mặc định của các địa chỉ khác trong danh sách nháp
      newAddresses.value.forEach((addr) => (addr.macDinh = false));
    }
  }
  // Nếu đang ở chế độ SỬA/XEM địa chỉ đã có (và người dùng vừa tích vào checkbox)
  else if (currentDraftAddress.value && currentAddressForm.macDinh) {
    // Áp dụng ngay lập tức: Gỡ mặc định của các địa chỉ khác
    newAddresses.value.forEach((addr) => (addr.macDinh = false));

    // Đặt địa chỉ hiện tại làm mặc định
    currentDraftAddress.value.macDinh = true;
    notify.success("Địa chỉ đã được đặt làm mặc định.");
  }
  // Trường hợp HẠNG CHẾ: Nếu bỏ tích và list có nhiều hơn 1 địa chỉ, KHÔNG CHO BỎ
  else if (currentDraftAddress.value && !currentAddressForm.macDinh) {
    // Nếu địa chỉ hiện tại đang là mặc định và người dùng bỏ chọn
    if (currentDraftAddress.value.macDinh && newAddresses.value.length > 1) {
      // Ngăn không cho bỏ chọn nếu đây là địa chỉ đang là mặc định
      currentAddressForm.macDinh = true;
      notify.warning(
        "Cần có ít nhất một địa chỉ mặc định. Vui lòng đặt mặc định cho địa chỉ khác trước khi bỏ chọn cái này."
      );
    } else if (newAddresses.value.length === 1) {
      // Nếu chỉ có 1 địa chỉ, không bao giờ cho phép bỏ mặc định
      currentAddressForm.macDinh = true;
    }
  }
};

// 🟢 MỚI: Chuyển đổi địa chỉ
const goToNextAddress = () => {
  if (currentAddressIndex.value < newAddresses.value.length - 1) {
    currentAddressIndex.value++;
    isAddingNewAddress.value = false;
  }
};

const goToPreviousAddress = () => {
  if (currentAddressIndex.value > 0) {
    currentAddressIndex.value--;
    isAddingNewAddress.value = false;
  }
};

/* ------------------------------------------------ */
/* --- Logic Địa chỉ Toàn quốc --- */
/* ------------------------------------------------ */

const currentProvince = computed(
  () =>
    provincesData.value.find(
      (p) => String(p.code) === String(currentAddressForm.tinhCode)
    ) || null
);
const currentDistrict = computed(() => {
  if (!currentProvince.value) return null;
  return (
    currentProvince.value.districts?.find(
      (d) => String(d.code) === String(currentAddressForm.huyenCode)
    ) || null
  );
});
const currentWard = computed(() => {
  if (!currentDistrict.value) return null;
  return (
    currentDistrict.value.wards?.find(
      (w) => String(w.code) === String(currentAddressForm.xaCode)
    ) || null
  );
});

// Danh sách cho 3 select
const provinces = computed(() => provincesData.value);
const districts = computed(() => currentProvince.value?.districts ?? []);
const wards = computed(() => currentDistrict.value?.wards ?? []);

/* Reset liên kết khi đổi cấp */
const onNewProvinceChange = () => {
  currentAddressForm.huyenCode = "";
  currentAddressForm.xaCode = "";
};
const onNewDistrictChange = () => {
  currentAddressForm.xaCode = "";
};

/* ------------------------------------------------ */
/* --- Logic Lưu & Preview --- */
/* ------------------------------------------------ */

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
  // Lấy địa chỉ mặc định từ danh sách nháp
  const defaultAddress = newAddresses.value.find((a) => a.macDinh);
  if (!defaultAddress) return "— (Chưa có địa chỉ mặc định)";

  return [
    defaultAddress.diaChiCuThe,
    defaultAddress.xa,
    defaultAddress.huyen,
    defaultAddress.thanhPho,
  ]
    .filter(Boolean)
    .join(", ");
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

/* ====== SAVE KHÁCH HÀNG VÀ TẤT CẢ ĐỊA CHỈ ====== */
const addKhachHang = async () => {
  if (newAddresses.value.length === 0) {
    throw new Error("Vui lòng thêm ít nhất một địa chỉ cho khách hàng.");
  }

  // Đảm bảo có ít nhất một địa chỉ mặc định trước khi lưu
  if (!newAddresses.value.some((a) => a.macDinh)) {
    newAddresses.value[0].macDinh = true; // Set địa chỉ đầu tiên làm mặc định
  }

  // 1. Lưu Khách Hàng
  const payload = {
    ma: form.ma.trim(),
    hoTen: form.hoTen.trim(),
    email: form.email.trim(),
    sdt: String(form.sdt ?? "").trim(),
    gioiTinh: Number(form.gioiTinh),
    ngaySinh: toYMD(form.ngaySinh),
    urlAnh: form.urlAnh || null,
    trangThai: 1,
    taiKhoan: form.email?.trim() || null,
    matKhau: null,
  };

  const resKh = await createKhachHang(payload);
  const createdKhachHang = resKh?.data || resKh;
  if (!createdKhachHang?.id) {
    throw new Error("Không nhận được ID Khách Hàng sau khi tạo.");
  }
  const khachHangId = createdKhachHang.id;

  // 2. Lặp và Lưu TẤT CẢ Địa Chỉ
  const savePromises = newAddresses.value.map((draftAddr) => {
    // 🟢 LƯU Ý: Không gửi các trường tạm thời (id)
    const diaChiPayload = {
      thanhPho: draftAddr.thanhPho,
      huyen: draftAddr.huyen,
      xa: draftAddr.xa,
      diaChiCuThe: draftAddr.diaChiCuThe,
      macDinh: draftAddr.macDinh,
      khachHang: { id: khachHangId },
    };
    // Gọi API tạo địa chỉ
    return createDiaChi(diaChiPayload);
  });

  // Chờ tất cả địa chỉ được lưu
  await Promise.all(savePromises);

  return createdKhachHang;
};

const confirmSave = async () => {
  if (!form.hoTen || !form.email || !form.sdt) {
    notify.error("Vui lòng điền đầy đủ Mã, Họ tên, Email, SĐT!");
    return;
  }
  if (newAddresses.value.length === 0) {
    notify.error("Vui lòng thêm ít nhất một địa chỉ cho khách hàng!");
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
      notify.success("Thêm khách hàng và địa chỉ thành công!");

      // Ghim ID để trang danh sách ưu tiên lên đầu
      if (created?.id) {
        sessionStorage.setItem("NEW_KH_ID", String(created.id));
      }

      router.push("/admin/khach-hang");
    } catch (err) {
      console.error("Lỗi khi thêm khách hàng:", err);
      const msg =
        err?.response?.data?.message || err?.message || "Thêm thất bại!";
      notify.error(msg);
    }
  }
};

/* ====== ĐỊA CHỈ TOÀN QUỐC (LOAD TỪ OPEN API) ====== */

onMounted(async () => {
  try {
    const res = await fetch("https://provinces.open-api.vn/api/?depth=3");
    if (!res.ok) throw new Error("Fetch provinces failed");
    provincesData.value = await res.json();

    // Khởi tạo địa chỉ nháp đầu tiên để form hiển thị
    if (newAddresses.value.length === 0) {
      openAddressForm();
      isAddingNewAddress.value = false; // Bắt đầu ở trạng thái sẵn sàng nhập (như sửa/thêm)
    }
  } catch (e) {
    console.error(
      "Không tải được danh mục Tỉnh/TP. Bạn có thể nhập thủ công hoặc bundle JSON cục bộ.",
      e
    );
    // Fallback mẫu
    provincesData.value = [
      {
        code: "01",
        name: "Hà Nội",
        districts: [
          {
            code: "001",
            name: "Quận Ba Đình",
            wards: [
              { code: "00001", name: "Phường Phúc Xá" },
              { code: "00004", name: "Phường Trúc Bạch" },
            ],
          },
        ],
      },
    ];
    // Khởi tạo địa chỉ nháp đầu tiên
    if (newAddresses.value.length === 0) {
      openAddressForm();
      isAddingNewAddress.value = false;
    }
  }
});
</script>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
</style>
