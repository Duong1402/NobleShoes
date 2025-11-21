<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Thêm nhân viên</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="row g-3">
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

          <div class="col-md-6">
            <label class="form-label">Họ tên</label>
            <input
              v-model="form.hoTen"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': v$.hoTen.$error }"
              placeholder="Nhập họ tên nhân viên"
            />
            <small v-if="v$.hoTen.$error" class="text-danger">
              Vui lòng nhập họ tên
            </small>
          </div>

          <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input
              v-model="form.sdt"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': v$.sdt.$error }"
              placeholder="Nhập số điện thoại"
            />
            <small v-if="v$.sdt.required.$invalid" class="text-danger">
              Vui lòng nhập số điện thoại
            </small>
            <small v-else-if="v$.sdt.phone.$invalid" class="text-danger">
              Số điện thoại không hợp lệ (phải có 10 số và bắt đầu bằng 0)
            </small>
          </div>

          <div class="col-md-6">
            <label class="form-label">Email</label>
            <input
              v-model="form.email"
              type="email"
              class="form-control"
              :class="{ 'is-invalid': v$.email.$error }"
              placeholder="Nhập email nhân viên"
              @blur="v$.email.$touch()"
            />
            <small v-if="v$.email.required.$invalid" class="text-danger">
              Vui lòng nhập email
            </small>
            <small v-else-if="v$.email.email.$invalid" class="text-danger">
              Email không đúng định dạng
            </small>
            <small v-else-if="v$.email.duplicate.$invalid" class="text-danger">
              Email đã tồn tại, vui lòng nhập email khác
            </small>
            <small v-else-if="isCheckingEmail" class="text-muted">
              Đang kiểm tra email...
            </small>
          </div>

          <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <div class="input-group">
              <input
                v-model="form.cccd"
                type="text"
                class="form-control"
                :class="{ 'is-invalid': v$.cccd.$error }"
                placeholder="Nhập CCCD nhân viên"
              />
              <button
                type="button"
                class="btn btn-outline-warning"
                @click="openScanner"
                title="Quét mã CCCD (PDF417)"
              >
                <i class="fa-solid fa-qrcode"></i>
              </button>
            </div>
            <small v-if="v$.cccd.required.$invalid" class="text-danger">
              Vui lòng nhập CCCD
            </small>
            <small v-else-if="v$.cccd.cccd.$invalid" class="text-danger">
              CCCD phải có 12 số
            </small>

            <div
              v-if="scanning"
              class="modal fade show"
              style="display: block; background: rgba(0, 0, 0, 0.6)"
            >
              <div class="modal-dialog modal-dialog-centered" style="max-width: 420px">
                <div class="modal-content border-0">
                  <div class="modal-header bg-dark text-white py-2">
                    <h6 class="mb-0">
                      <i class="fa-solid fa-id-card me-2 text-warning"></i>Quét CCCD
                    </h6>
                    <button
                      type="button"
                      class="btn-close btn-close-white"
                      @click="stopScan"
                    ></button>
                  </div>
                  <div class="modal-body text-center p-0">
                    <video
                      id="video"
                      autoplay
                      muted
                      playsinline
                      style="width: 100%; height: 320px; background: #000"
                    ></video>
                  </div>
                  <div class="modal-footer py-2 justify-content-end">
                    <button class="btn btn-warning btn-sm" @click="stopScan">
                      Đóng
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <label class="form-label d-block">Giới tính</label>
            <div class="d-flex gap-3">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="radio"
                  id="gioiTinhNam"
                  :value="true"
                  v-model="form.gioiTinh"
                />
                <label class="form-check-label" for="gioiTinhNam">Nam</label>
              </div>
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="radio"
                  id="gioiTinhNu"
                  :value="false"
                  v-model="form.gioiTinh"
                />
                <label class="form-check-label" for="gioiTinhNu">Nữ</label>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input
              v-model="form.ngaySinh"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': v$.ngaySinh.$error }"
            />
            <small v-if="v$.ngaySinh.$error" class="text-danger">
              Vui lòng chọn ngày sinh
            </small>
          </div>

          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Tỉnh/Thành phố</label>
              <select
                v-model="selectedProvince"
                class="form-select"
                @change="onProvinceChange"
              >
                <option value="">-- Chọn tỉnh/thành phố --</option>
                <option v-for="p in provinces" :key="p.code" :value="p.code">
                  {{ p.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Quận/Huyện</label>
              <select
                v-model="selectedDistrict"
                class="form-select"
                @change="onDistrictChange"
                :disabled="!districts.length"
              >
                <option value="">-- Chọn quận/huyện --</option>
                <option v-for="d in districts" :key="d.code" :value="d.code">
                  {{ d.name }}
                </option>
              </select>
            </div>

            <div class="col-md-4">
              <label class="form-label">Xã/Phường</label>
              <select
                v-model="selectedWard"
                class="form-select"
                :disabled="!wards.length"
              >
                <option value="">-- Chọn xã/phường --</option>
                <option v-for="w in wards" :key="w.code" :value="w.code">
                  {{ w.name }}
                </option>
              </select>
            </div>

            <div class="col-12 mt-2">
              <label class="form-label">Địa chỉ cụ thể</label>
              <input
                v-model="form.chiTiet"
                type="text"
                class="form-control"
                placeholder="Số nhà, thôn, đường..."
              />
            </div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Chức vụ</label>
            <select
              v-model="form.chucVu.id"
              class="form-select"
              :class="{ 'is-invalid': v$.chucVu.id.$error }"
            >
              <option disabled value="">-- Chọn chức vụ --</option>
              <option v-for="cv in chucVuList" :key="cv.id" :value="cv.id">
                {{ cv.ten }}
              </option>
            </select>
            <small v-if="v$.chucVu.id.$error" class="text-danger">
              Vui lòng chọn chức vụ
            </small>
          </div>
        </div>

        <div class="mt-4 text-end">
          <router-link
            :to="{ name: 'nhanVien' }"
            class="btn btn-secondary me-2"
          >
            <i class="fa fa-arrow-left me-1"></i> Quay lại
          </router-link>
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm nhân viên
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, nextTick, onBeforeUnmount, shallowRef } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import useVuelidate from "@vuelidate/core";
import { required, email, helpers } from "@vuelidate/validators";
import { getProvinces, getDistricts, getWards } from "vietnam-provinces";
import axios from "axios";
import { debounce } from "lodash";

const router = useRouter();
const chucVuList = ref([]);
const notify = useNotify();
const uploading = ref(false);
const previewUrl = ref("");
const isCheckingEmail = ref(false);

// Form thêm nhân viên
const form = reactive({
  hoTen: "",
  sdt: "",
  email: "",
  gioiTinh: true,
  ngaySinh: "",
  tinh: "",
  huyen: "",
  xa: "",
  chiTiet: "",
  cccd: "",
  chucVu: { id: "" },
  urlAnh: "",
});

// 1. ✅ Check trùng email (Dùng Axios để gửi Token)
const checkDuplicateEmail = async (value) => {
  if (!value) return true;
  isCheckingEmail.value = true;
  try {
    const res = await axios.get('http://localhost:8080/admin/nhan-vien/check-email', {
        params: { email: value }
    });
    const data = res.data;
    return !data.exists;
  } catch (e) {
    console.error("Lỗi kiểm tra email:", e);
    return true; // Không chặn nếu lỗi mạng/server
  } finally {
    isCheckingEmail.value = false;
  }
};

// Validate
const rules = {
  hoTen: { required },
  sdt: {
    required,
    phone: helpers.regex(/^0\d{9}$/),
  },
  email: {
    required,
    email,
    duplicate: helpers.withAsync(
      async (value) => await checkDuplicateEmail(value)
    ),
  },
  cccd: {
    required,
    cccd: helpers.regex(/^\d{12}$/),
  },
  ngaySinh: { required },
  chucVu: { id: { required } },
};
const v$ = useVuelidate(rules, form);

// Quét CCCD
const scanning = ref(false);
const decodeError = ref(null);
const decoded = ref(false);
let codeReader = null;
let videoElem = null;

const openScanner = async () => {
  scanning.value = true;
  decodeError.value = null;
  decoded.value = false;
  await nextTick();

  try {
    const { BrowserMultiFormatReader } = await import("@zxing/browser");
    codeReader = new BrowserMultiFormatReader();
    const devices = await BrowserMultiFormatReader.listVideoInputDevices();
    if (!devices.length) {
      decodeError.value = "Không tìm thấy camera.";
      return;
    }
    const selectedDeviceId =
      devices.find((d) => d.label.toLowerCase().includes("back"))?.deviceId ||
      devices[0].deviceId;

    videoElem = document.getElementById("video");

    await codeReader.decodeFromVideoDevice(
      selectedDeviceId,
      videoElem,
      (result, err) => {
        if (result && !decoded.value) {
          decoded.value = true;
          console.log("✅ ĐÃ QUÉT:", result.getText());
          handleDecodedCCCD(result.getText());
          stopScan();
        } 
        // ✅ Fix: Chặn lỗi rác console
        else if (err && !err.message.includes("No MultiFormat Readers")) {
          // console.warn("⚠️ Lỗi đọc:", err);
        }
      }
    );
  } catch (e) {
    console.error("❌ Lỗi khởi tạo camera:", e);
    decodeError.value = "Không thể mở camera: " + e.message;
  }
};

const stopScan = () => {
  scanning.value = false;
  decodeError.value = null;

  // 1. Dừng thư viện decode
  if (codeReader) {
    try {
      codeReader.reset(); // Quan trọng: Reset reader để nó nhả resource
    } catch (e) {
      console.error(e);
    }
    codeReader = null;
  }

  // 2. Dừng Camera vật lý (Hardware)
  const vid = document.getElementById("video");
  if (vid && vid.srcObject) {
    const tracks = vid.srcObject.getTracks();
    tracks.forEach((track) => {
      track.stop(); // Bắt buộc phải stop từng track
      vid.srcObject.removeTrack(track);
    });
    vid.srcObject = null;
  }
};

const handleDecodedCCCD = async (text) => {
  try {
    const parts = text.split("|").filter((x) => x.trim() !== "");
    form.cccd = parts[0]?.trim() || "";
    form.hoTen = parts[1]?.trim() || "";

    const ngaySinhRaw = parts[2]?.trim() || "";
    if (/^\d{8}$/.test(ngaySinhRaw)) {
      const d = ngaySinhRaw.substring(0, 2);
      const m = ngaySinhRaw.substring(2, 4);
      const y = ngaySinhRaw.substring(4, 8);
      form.ngaySinh = `${y}-${m}-${d}`;
    } else {
      form.ngaySinh = "";
    }

    const gioiTinhRaw = (parts[3] || "").trim().toLowerCase();
    form.gioiTinh = gioiTinhRaw === "nam" || gioiTinhRaw === "male";

    const fullAddress = parts.slice(4).join(", ").trim();
    form.chiTiet = fullAddress.split(",")[0]?.trim() || "";

    notify.success("Quét QR thành công");
    scanning.value = false;
  } catch (e) {
    console.error("❌ Lỗi phân tích CCCD:", e);
    notify.error("Không thể đọc được dữ liệu CCCD!");
  }
};

onBeforeUnmount(stopScan);

// Upload ảnh
const handleFileUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  previewUrl.value = URL.createObjectURL(file);
  const formData = new FormData();
  formData.append("file", file);
  uploading.value = true;

  try {
    const res = await axios.post("http://localhost:8080/api/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    form.urlAnh = res.data;
  } catch (err) {
    notify.error("Upload ảnh thất bại!");
    console.error(err);
  } finally {
    uploading.value = false;
  }
};

// Địa chỉ
const provinces = shallowRef([]);
const districts = shallowRef([]);
const wards = shallowRef([]);
const selectedProvince = ref("");
const selectedDistrict = ref("");
const selectedWard = ref("");

const onProvinceChange = () => {
  districts.value = getDistricts(selectedProvince.value);
  wards.value = [];
  selectedDistrict.value = "";
  selectedWard.value = "";
};
const onDistrictChange = () => {
  wards.value = getWards(selectedDistrict.value);
  selectedWard.value = "";
};

// 2. ✅ Load chức vụ (Dùng Axios để gửi Token)
const loadChucVu = async () => {
  try {
    const res = await axios.get("http://localhost:8080/admin/chuc-vu");
    chucVuList.value = res.data;
  } catch (err) {
    console.error("Lỗi khi tải chức vụ:", err);
  }
};

// 3. ✅ Thêm nhân viên (Dùng Axios để gửi Token)
const addNhanVien = async () => {
  try {
    form.tinh = provinces.value.find((p) => p.code === selectedProvince.value)?.name || "";
    form.huyen = districts.value.find((d) => d.code === selectedDistrict.value)?.name || "";
    form.xa = wards.value.find((w) => w.code === selectedWard.value)?.name || "";

    form.diaChi = `${form.chiTiet || ""}, ${form.xa || ""}, ${
      form.huyen || ""
    }, ${form.tinh || ""}`.replace(/(^[,\s]+)|([,\s]+$)/g, "");

    const res = await axios.post("http://localhost:8080/admin/nhan-vien", form);

    notify.success("Thêm nhân viên thành công!");
    router.push({ name: "nhanVien" });
  } catch (err) {
    console.error("Lỗi khi thêm nhân viên:", err);
    const errorMsg = err.response?.data?.message || "Thêm thất bại, vui lòng thử lại!";
    notify.error(errorMsg);
  }
};

const confirmSave = async () => {
  v$.value.$touch();
  if (v$.value.$invalid) {
    notify.error("Vui lòng kiểm tra lại thông tin!");
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận thêm nhân viên?",
    text: "Bạn có chắc chắn muốn thêm nhân viên này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    addNhanVien();
  }
};

const debouncedCheckEmail = debounce(async (value, resolve) => {
    try {
        const res = await axios.get('http://localhost:8080/admin/nhan-vien/check-email', {
            params: { email: value }
        });
        resolve(!res.data.exists); // Trả về true (hợp lệ) hoặc false (trùng)
    } catch (e) {
        resolve(true); // Lỗi mạng thì cho qua
    }
}, 500);



onMounted(() => {
  provinces.value = getProvinces();
  loadChucVu();
});
</script>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
.is-invalid {
  border-color: #dc3545 !important;
}
.text-danger {
  font-size: 0.875rem;
}
input:focus,
select:focus,
textarea:focus {
  border-color: #ffc107 !important;
  box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25);
  outline: none !important;
}
</style>