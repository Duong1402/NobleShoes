<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Phiếu giảm giá add</h3>
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
            <label class="form-label">Tên phiếu giảm giá</label>
            <input
              v-model="form.ten"
              type="text"
              class="form-control"
              placeholder="Nhập họ tên phiếu giảm giá"
            />
          </div>

          <!-- Ngày bắt đầu -->
          <div class="col-md-6">
            <label class="form-label">Ngày bắt đầu</label>
            <input v-model="form.ngayBatDau" type="date" class="form-control" />
          </div>

          <!-- Ngày kết thúc -->
          <div class="col-md-6">
            <label class="form-label">Ngày kết thúc</label>
            <input v-model="form.email" type="date" class="form-control" />
          </div>
          <!-- Hình thức giảm giá -->
          <div class="col-md-6">
            <label class="form-label d-block">Hình thức giảm giá</label>
            <div class="d-flex gap-3 custom-radio">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="radio"
                  id="%"
                  :value="true"
                  v-model="form.hinhThucGiamGia"
                />
                <label class="form-check-label">%</label>
              </div>
              <div class="form-check custom-radio">
                <input
                  class="form-check-input"
                  type="radio"
                  id="tienMat"
                  :value="false"
                  v-model="form.hinhThucGiamGia"
                />
                <label class="form-check-label">Tiền mặt</label>
              </div>
            </div>
          </div>

          <!-- Giá trị giảm -->
          <div class="col-md-6">
            <label class="form-label">Giá trị giảm</label>
            <input
              v-model="form.giaTriGiam"
              type="number"
              class="form-control"
              placeholder="Nhập giá trị giảm"
            />
          </div>

          <!-- Giá trị giảm tối thiểu-->
          <div class="col-md-6">
            <label class="form-label">Giá trị giảm tối thiểu</label>
            <input
              v-model="form.giaTriGiamToiThieu"
              type="number"
              class="form-control"
              placeholder="Nhập giá trị giảm tối thiểu"
            />
          </div>
          <!-- Giá trị giảm tối đa-->
          <div class="col-md-6">
            <label class="form-label">Giá trị giảm tối đa</label>
            <input
              v-model="form.giaTriGiamToiDa"
              type="number"
              class="form-control"
              placeholder="Nhập giá trị giảm tối đa"
            />
          </div>


          <!-- Mô tả -->
          <div class="col-md-6">
            <label class="form-label">Mô tả</label>
            <textarea
              v-model="form.moTa"
              type="date"
              class="form-control"
              placeholder="Nhập mô tả"
            ></textarea>
          </div>
        </div>

        <!-- Nút hành động -->
        <div class="mt-4 text-end">
          <router-link
            to="/admin/phieu-giam-gia"
            class="btn btn-secondary me-2"
          >
            <i class="fa fa-arrow-left me-1"></i> Quay lại
          </router-link>
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm phiếu giảm giá
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
import { createPhieuGiamGia } from "@/service/phieuGiamGiaService";

const router = useRouter();
const notify = useNotify();

// Form thêm phiếu giảm giá
const form = reactive({
  ten: null,
  ngayBatDau: null,
  ngayKetThuc: null,
  hinhThucGiamGia: false,
  giaTriGiam: null,
  giaTriGiamToiThieu: null,
  giaTriGiamToiDa: null,
  trangThai: true,
  moTa: "",
});

//Thêm phiếu giảm giá
const addPhieuGiamGia = async () => {
  try {
    const res = await createPhieuGiamGia(form);

    if (!res) throw new Error("Lỗi khi thêm phiếu giảm giá");

    notify.success("Thêm phiếu giảm giá thành công!");
    router.push("/admin/phieu-giam-gia");
  } catch (error) {
    console.error("Lỗi khi thêm phiếu giảm giá:", error);
    notify.error("Thêm thất bại, vui lòng thử lại!");
  }
};

// Tạo hàm confirm
const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận thêm phiếu giảm giá ?",
    text: "Bạn có chắc chắn muốn thêm phiếu giảm giá này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107", // màu vàng giống btn
    cancelButtonColor: "#6c757d",
  });

  // if (result.isConfirmed) {
    addPhieuGiamGia(); // gọi hàm lưu
  // }
};
</script>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
.custom-radio .form-check-input:checked {
  background-color: #ffc107 !important; /* màu cam */
  border-color: #ffc107 !important;
}
</style>
.