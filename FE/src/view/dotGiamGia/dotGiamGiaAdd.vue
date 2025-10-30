<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import { createDotGiamGia } from "@/service/dotGiamGiaService";

const router = useRouter();
const notify = useNotify();
const errors = reactive({});

const form = reactive({
  ma: "",
  ten: null,
  giaTriGiam: null,
  soTienGiamToiDa: null,
  ngayBatDau: "",
  ngayKetThuc: "",
  trangThai: true,
});

const addDotGiamGia = async () => {
  try {
    // Xóa lỗi cũ trước khi submit
    Object.keys(errors).forEach((key) => (errors[key] = ""));

    const res = await createDotGiamGia(form);
    if (!res) throw new Error("Lỗi khi thêm đợt giảm giá");

    notify.success("Thêm đợt giảm giá thành công!");
    router.push("/admin/dot-giam-gia");
  } catch (error) {
    if (error.response && error.response.status === 400) {
      // Gán lỗi validation từ backend
      Object.assign(errors, error.response.data);
      notify.error("Vui lòng kiểm tra lại thông tin!");
      console.log("Lỗi validation:", errors);
    } else {
      console.error("Lỗi khi thêm đợt giảm giá:", error);
      notify.error("Thêm thất bại, vui lòng thử lại!");
    }
  }
};

const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận thêm đợt giảm giá?",
    text: "Bạn có chắc chắn muốn thêm đợt giảm giá này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    await addDotGiamGia();
  }
};
</script>

<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Đợt giảm giá add</h3>
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
            <label class="form-label">Tên đợt giảm giá</label>
            <input
              v-model="form.ten"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': errors.ten }"
              placeholder="Nhập họ tên đợt giảm giá"
            />
            <div class="invalid-feedback">{{ errors.ten }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Giá trị giảm</label>
            <div class="input-group">
              <input
                v-model="form.giaTriGiam"
                type="number"
                class="form-control"
                :class="{ 'is-invalid': errors.giaTriGiam }"
              />
              <span class="input-group-text" id="basic-addon2">%</span>
            </div>
            <div class="invalid-feedback">{{ errors.giaTriGiam }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Số tiền giảm tối đa</label>
            <div class="input-group">
              <input
                v-model="form.soTienGiamToiDa"
                type="number"
                class="form-control"
                :class="{ 'is-invalid': errors.soTienGiamToiDa }"
              />
              <span class="input-group-text" id="basic-addon2">VNĐ</span>
            </div>
            <div class="invalid-feedback">{{ errors.soTienGiamToiDa }}</div>
          </div>

          <!-- Ngày bắt đầu -->
          <div class="col-md-6">
            <label class="form-label">Ngày bắt đầu</label>
            <input
              v-model="form.ngayBatDau"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': errors.ngayBatDau }"
            />
            <div class="invalid-feedback">{{ errors.ngayBatDau }}</div>
          </div>

          <!-- Ngày kết thúc -->
          <div class="col-md-6">
            <label class="form-label">Ngày kết thúc</label>
            <input
              v-model="form.ngayKetThuc"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': errors.ngayKetThuc }"
            />
            <div class="invalid-feedback">{{ errors.ngayKetThuc }}</div>
          </div>
        </div>

        <!-- Nút hành động -->
        <div class="mt-4 text-end">
          <router-link to="/admin/dot-giam-gia" class="btn btn-secondary me-2">
            <i class="fa fa-arrow-left me-1"></i> Quay lại
          </router-link>
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm đợt giảm giá
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
</style>
