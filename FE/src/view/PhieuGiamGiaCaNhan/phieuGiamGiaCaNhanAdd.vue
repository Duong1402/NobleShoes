<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý nhân viên</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="row g-3">
          <!-- Họ tên -->
          <div class="col-md-6">
            <label class="form-label">Họ tên</label>
            <input
              v-model="form.hoTen"
              type="text"
              class="form-control"
              placeholder="Nhập họ tên nhân viên"
              required
            />
          </div>

          <!-- Số điện thoại -->
          <div class="col-md-6">
            <label class="form-label">Số điện thoại</label>
            <input
              v-model="form.sdt"
              type="text"
              class="form-control"
              placeholder="Nhập số điện thoại"
              required
            />
          </div>

          <!-- Email -->
          <div class="col-md-6">
            <label class="form-label">Email</label>
            <input
              v-model="form.email"
              type="email"
              class="form-control"
              placeholder="Nhập email nhân viên"
              required
            />
          </div>

          <!-- CCCD -->
          <div class="col-md-6">
            <label class="form-label">CCCD</label>
            <input
              v-model="form.cccd"
              type="text"
              class="form-control"
              placeholder="Nhập CCCD nhân viên"
              required
            />
          </div>

          <!-- Tài khoản -->
          <div class="col-md-6">
            <label class="form-label">Tài khoản</label>
            <input
              v-model="form.taiKhoan"
              type="text"
              class="form-control"
              placeholder="Nhập tài khoản nhân viên"
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

          <!-- Ngày sinh -->
          <div class="col-md-6">
            <label class="form-label">Ngày sinh</label>
            <input
              v-model="form.ngaySinh"
              type="date"
              class="form-control"
              required
            />
          </div>

          <div class="row g-3">
            <!-- Địa chỉ: Tỉnh/Thành phố, Quận/Huyện, Xã/Phường -->
            <div class="col-md-4">
              <label class="form-label">Tỉnh/Thành phố</label>
              <input
                v-model="form.tinh"
                type="text"
                class="form-control"
                placeholder="Chọn tỉnh/thành phố"
              />
            </div>
            <div class="col-md-4">
              <label class="form-label">Quận/Huyện</label>
              <input
                v-model="form.huyen"
                type="text"
                class="form-control"
                placeholder="Chọn quận/huyện"
              />
            </div>
            <div class="col-md-4">
              <label class="form-label">Xã/Phường</label>
              <input
                v-model="form.xa"
                type="text"
                class="form-control"
                placeholder="Chọn xã/phường"
              />
            </div>

            <!-- Địa chỉ cụ thể -->
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

          <!-- Chức vụ -->
          <div class="col-md-6">
            <label class="form-label">Chức vụ</label>
            <select v-model="form.chucVu.id" class="form-select" required>
              <option disabled value="">-- Chọn chức vụ --</option>
              <option v-for="cv in chucVuList" :key="cv.id" :value="cv.id">
                {{ cv.ten }}
              </option>
            </select>
          </div>
        </div>

        <!-- Nút hành động -->
        <div class="mt-4 text-end">
          <router-link to="/admin/nhan-vien" class="btn btn-secondary me-2">
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
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";

const router = useRouter();
const chucVuList = ref([]);
const notify = useNotify();

// Form thêm nhân viên
const form = reactive({
  hoTen: "",
  sdt: "",
  email: "",
  gioiTinh: true,
  ngaySinh: "",
  diaChi: "",
  cccd: "",
  chucVu: {
    id: "",
  },
  taiKhoan:""
});

// Load danh sách chức vụ
const loadChucVu = async () => {
  try {
    const res = await fetch("http://localhost:8080/admin/chuc-vu");
    const data = await res.json();
    chucVuList.value = data;
  } catch (err) {
    console.error("Lỗi khi tải chức vụ:", err);
  }
};

// Gọi API thêm nhân viên
const addNhanVien = async () => {
  try {
    // Ghép đầy đủ địa chỉ trước khi gửi lên server
    form.diaChi = `${form.chiTiet || ""}, ${form.xa || ""}, ${
      form.huyen || ""
    }, ${form.tinh || ""}`.replace(/(^[,\s]+)|([,\s]+$)/g, ""); // loại bỏ dấu phẩy thừa

    const res = await fetch("http://localhost:8080/admin/nhan-vien", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(form),
    });

    if (!res.ok) throw new Error("Lỗi khi thêm nhân viên");

    notify.success("Thêm nhân viên thành công!");
    router.push("/admin/nhan-vien");
  } catch (error) {
    console.error("Lỗi khi thêm nhân viên:", error);
    notify.error("Thêm thất bại, vui lòng thử lại!");
  }
};

// Tạo hàm confirm
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận thêm nhân viên ?",
    text: "Bạn có chắc chắn muốn thêm nhân viên này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107", // màu vàng giống btn
    cancelButtonColor: "#6c757d"
  });

  if (result.isConfirmed) {
    addNhanVien(); // gọi hàm lưu
  }
};


onMounted(() => loadChucVu());
</script>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
</style>
