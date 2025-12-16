<template>
  <div class="container-fluid mt-4">
    <!-- Header -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Chi tiết nhân viên</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <!-- Nội dung -->
    <div class="card shadow-sm border-0">
      <div class="card-body">
        <!--Thêm điều kiện để không render khi dữ liệu chưa load -->
        <div v-if="nhanVien && nhanVien.chucVu" class="row g-3">
          <!-- Ảnh -->
          <div class="d-flex justify-content-center mb-4">
            <div
              class="position-relative d-inline-block rounded-circle border border-2 border-secondary-subtle bg-light"
              style="
                width: 140px;
                height: 140px;
                cursor: pointer;
                overflow: hidden;
              "
              @mouseenter="hover = true"
              @mouseleave="hover = false"
              @click="$refs.fileInput.click()"
            >
              <img
                v-if="previewUrl || nhanVien.urlAnh"
                :src="previewUrl || nhanVien.urlAnh"
                alt="Ảnh nhân viên"
                class="w-100 h-100 object-fit-cover"
              />

              <!-- Overlay khi hover -->
              <div
                v-if="hover"
                class="position-absolute top-0 start-0 w-100 h-100 bg-dark bg-opacity-50 d-flex justify-content-center align-items-center"
              >
                <i class="fa-solid fa-camera text-white fa-2x"></i>
              </div>

              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                hidden
                @change="handleImageChange"
              />
            </div>
          </div>

          <!-- Mã nhân viên -->
          <div class="col-md-6">
            <label class="form-label">Mã nhân viên</label>
            <input
              type="text"
              class="form-control"
              v-model="nhanVien.ma"
              disabled
            />
          </div>

          <!-- Họ tên -->
          <div class="col-md-6">
            <label class="form-label">Họ tên</label>
            <input
              v-model="nhanVien.hoTen"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': v$.hoTen.$error }"
              placeholder="Nhập họ tên nhân viên"
            />
            <small v-if="v$.hoTen.$error" class="text-danger">
              Vui lòng nhập họ tên
            </small>
          </div>

          <!-- Số điện thoại -->
          <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input
              v-model="nhanVien.sdt"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': v$.sdt.$error }"
              placeholder="Nhập số điện thoại"
            />
            <small v-if="v$.sdt.$error" class="text-danger">
              Số điện thoại không hợp lệ (bắt đầu bằng 0 và gồm 10 số)
            </small>
          </div>

          <!-- Email -->
          <div class="col-md-6">
            <label class="form-label">Email</label>
            <input
              v-model="nhanVien.email"
              type="email"
              class="form-control"
              :class="{ 'is-invalid': v$.email.$error }"
              placeholder="Nhập email"
            />
            <small v-if="v$.email.$error" class="text-danger">
              Email không hợp lệ
            </small>
          </div>

          <!-- Ngày sinh -->
          <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input
              v-model="nhanVien.ngaySinh"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': v$.ngaySinh.$error }"
            />
            <small v-if="v$.ngaySinh.$error" class="text-danger">
              Vui lòng chọn ngày sinh
            </small>
          </div>

          <!-- Giới tính -->
          <div class="col-md-6">
            <label class="form-label d-block">Giới tính</label>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                id="gioiTinhNam"
                :value="true"
                v-model="nhanVien.gioiTinh"
              />
              <label class="form-check-label" for="gioiTinhNam">Nam</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                id="gioiTinhNu"
                :value="false"
                v-model="nhanVien.gioiTinh"
              />
              <label class="form-check-label" for="gioiTinhNu">Nữ</label>
            </div>
          </div>

          <!-- CCCD -->
          <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <input
              v-model="nhanVien.cccd"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': v$.cccd.$error }"
              placeholder="Nhập CCCD (12 số)"
            />
            <small v-if="v$.cccd.$error" class="text-danger">
              CCCD phải gồm đúng 12 số
            </small>
          </div>

          <!-- Địa chỉ -->
          <div class="row g-3">
            <!-- Tỉnh/Thành phố -->
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

            <!-- Quận/Huyện -->
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

            <!-- Xã/Phường -->
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

            <!-- Địa chỉ cụ thể -->
            <div class="col-12 mt-2">
              <label class="form-label">Địa chỉ cụ thể</label>
              <input
                v-model="chiTiet"
                type="text"
                class="form-control"
                placeholder="Số nhà, thôn, đường..."
              />
            </div>
          </div>

          <!-- Ngày tạo & Ngày sửa -->
          <div class="col-md-6">
            <label class="form-label">Ngày tạo</label>
            <input
              type="date"
              class="form-control"
              v-model="nhanVien.ngayTao"
              disabled
            />
          </div>
          <div class="col-md-6">
            <label class="form-label">Ngày sửa</label>
            <input
              type="date"
              class="form-control"
              v-model="nhanVien.ngaySua"
              disabled
            />
          </div>

          <!-- Chức vụ -->
          <div class="col-md-6">
            <label class="form-label">Chức vụ</label>
            <select v-model="nhanVien.chucVu.id" class="form-select">
              <option disabled value="">-- Chọn chức vụ --</option>
              <option v-for="cv in chucVuList" :key="cv.id" :value="cv.id">
                {{ cv.ten }}
              </option>
            </select>
          </div>
        </div>

        <!-- Loading -->
        <div v-else class="text-center py-4 text-secondary">
          <div class="spinner-border text-warning" role="status"></div>
          <div class="mt-2">Đang tải dữ liệu nhân viên...</div>
        </div>
      </div>

      <!-- Nút hành động -->
      <div class="mt-4 text-end px-3 pb-3">
        <router-link :to="{ name: 'nhanVien' }" class="btn btn-secondary me-2">
          <i class="fa fa-arrow-left me-1"></i> Quay lại
        </router-link>
        <button
          type="button"
          class="btn btn-warning text-white"
          @click="confirmUpdate"
        >
          <i class="fa fa-save me-1"></i> Lưu thay đổi
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import axios from "axios"; 
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import { getProvinces, getDistricts, getWards } from "vietnam-provinces";
import useVuelidate from "@vuelidate/core";
import { required, email, helpers } from "@vuelidate/validators";
import router from "@/router";

import {
  getNhanVienById,
  updateNhanVien as updateNhanVienApi,
  getAllChucVu,
} from "@/service/NhanVienService";

const hover = ref(false);
const props = defineProps(["id"]);
const notify = useNotify();
const nhanVien = reactive({});
const chucVuList = ref([]);

const previewUrl = ref(null);
const selectedFile = ref(null);
const uploading = ref(false);

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);
const selectedProvince = ref("");
const selectedDistrict = ref("");
const selectedWard = ref("");
const chiTiet = ref("");

onMounted(async () => {
  provinces.value = getProvinces();
  await loadChucVu();
  await loadNhanVien();
});

async function loadNhanVien() {
  try {
    const res = await getNhanVienById(props.id);
    const data = res.data || {};

    if (!data.chucVu) data.chucVu = { id: "", ten: "" };
    Object.assign(nhanVien, data);

    parseDiaChi(data.diaChi);
  } catch (err) {
    console.error("❌ Lỗi load nhân viên:", err);
    notify.error("Không thể tải thông tin nhân viên.");
  }
}

// Validate
const rules = {
  hoTen: { required },
  sdt: { required, phone: helpers.regex(/^0\d{9}$/) },
  email: { required, email },
  cccd: { required, cccd: helpers.regex(/^\d{12}$/) },
  ngaySinh: { required },
  chucVu: { id: { required } },
};

const v$ = useVuelidate(rules, nhanVien);

async function loadChucVu() {
  try {
    const res = await getAllChucVu();
    chucVuList.value = res.data;
  } catch (err) {
    console.error("❌ Lỗi load chức vụ:", err);
  }
}

// cập nhật nhân viên
async function updateNhanVien() {
  v$.value.$touch();
  if (v$.value.$invalid) {
    notify.error("Vui lòng nhập đầy đủ và đúng thông tin nhân viên!");
    return;
  }

  try {
    uploading.value = true;
    let newImageUrl = nhanVien.urlAnh;

    if (selectedFile.value) {
      
      const formData = new FormData();
      formData.append("file", selectedFile.value);
      formData.append("upload_preset", "nobleshoes_preset");

      const res = await fetch(
        "https://api.cloudinary.com/v1_1/dppzg4tin/image/upload",
        {
          method: "POST",
          body: formData,
        }
      );

      if (!res.ok) throw new Error("Lỗi khi upload ảnh lên Cloudinary");

      const data = await res.json();
      newImageUrl = data.secure_url;
    }

    const provinceObj = provinces.value.find((p) => p.code === selectedProvince.value);
    const districtObj = districts.value.find((d) => d.code === selectedDistrict.value);
    const wardObj = wards.value.find((w) => w.code === selectedWard.value);

    const diaChiParts = [
      chiTiet.value,
      wardObj?.name,
      districtObj?.name,
      provinceObj?.name,
    ].filter(Boolean);

    const updatedNhanVien = {
      ...nhanVien,
      urlAnh: newImageUrl,
      diaChi: diaChiParts.join(", "),
      chucVu: { id: nhanVien.chucVu.id } 
    };

    await updateNhanVienApi(props.id, updatedNhanVien);

    notify.success("Cập nhật nhân viên thành công!");
    selectedFile.value = null; 
    router.push({ name: "nhanVien" });
    
  } catch (err) {
    console.error("❌ Lỗi cập nhật nhân viên:", err);
    notify.error("Cập nhật thất bại, vui lòng thử lại.");
  } finally {
    uploading.value = false;
  }
}

async function confirmUpdate() {
  const result = await Swal.fire({
    title: "Xác nhận cập nhật nhân viên?",
    text: "Bạn có chắc chắn muốn lưu thay đổi này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    await updateNhanVien();
  }
}

function parseDiaChi(fullAddress) {
  if (!fullAddress) return;
  const normalize = (str) =>
    str ? str.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim() : "";

  const parts = fullAddress.split(",").map((p) => p.trim()).reverse();
  const tinhName = parts[0] || "";
  const huyenName = parts[1] || "";
  const xaName = parts[2] || "";

  const province = provinces.value.find(
    (p) => normalize(tinhName).includes(normalize(p.name)) || normalize(p.name).includes(normalize(tinhName))
  ) || null;

  if (province) {
    selectedProvince.value = province.code;
    districts.value = getDistricts(province.code);
  }

  const district = districts.value.find(
    (d) => normalize(huyenName).includes(normalize(d.name)) || normalize(d.name).includes(normalize(huyenName))
  ) || null;

  if (district) {
    selectedDistrict.value = district.code;
    wards.value = getWards(district.code);
  }

  const ward = wards.value.find(
    (w) => normalize(xaName).includes(normalize(w.name)) || normalize(w.name).includes(normalize(xaName))
  ) || null;

  if (ward) {
    selectedWard.value = ward.code;
  }

  const detailParts = fullAddress.split(",").slice(0, -3).join(", ").trim();
  chiTiet.value = detailParts;
}

function onProvinceChange() {
  districts.value = getDistricts(selectedProvince.value);
  wards.value = [];
  selectedDistrict.value = "";
  selectedWard.value = "";
}

function onDistrictChange() {
  wards.value = getWards(selectedDistrict.value);
  selectedWard.value = "";
}

function handleImageChange(e) {
  const file = e.target.files[0];
  if (!file) return;
  selectedFile.value = file;
  previewUrl.value = URL.createObjectURL(file);
}
</script>

<style scoped>
input:focus,
select:focus,
textarea:focus {
  border-color: #ffc107 !important; 
  box-shadow: 0 0 0 0.2rem rgba(255, 193, 7, 0.25); 
  outline: none !important;
}
</style>
