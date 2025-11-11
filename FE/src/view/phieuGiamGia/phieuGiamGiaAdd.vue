<script setup>
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import {
  createPhieuGiamGia,
  getAllKhachHang,
  createPhieuGiamGiaCaNhan,
} from "@/service/phieuGiamGiaService";

const router = useRouter();
const notify = useNotify();
const errors = reactive({});
const loaiPhieu = ref("");
const khachHang = ref([]);
const khachHangSelected = ref([]);
const totalPages = ref(0);
const page = ref(0);
const size = ref(10);
const res = ref(null);
// Form thêm phiếu giảm giá
const form = reactive({ hinhThucGiamGia: true });

const loadKhachHang = async () => {
  try {
    const res = await getAllKhachHang(page.value, size.value);
    khachHang.value = res.data.content;
    totalPages.value = res.totalPages;
  } catch (error) {
    console.error("Lỗi khi tải danh sách khách hàng:", error);
  }
};

const gotoPage = (p) => {
  if (p >= 0 && p < totalPages.value) {
    page.value = p;
    loadKhachHang();
  }
};

//Thêm phiếu giảm giá
const addPhieuGiamGia = async () => {
  try {
    // 🔹 Xóa lỗi cũ
    Object.keys(errors).forEach((key) => (errors[key] = ""));

    // 🔹 Chuẩn hóa dữ liệu gửi
    const payload = {
      ...form,
      trangThai: true,
      giaTriGiamToiThieu: form.giaTriGiamToiThieu || 0,
      giaTriGiamToiDa: form.giaTriGiamToiDa || 0,
    };

    // 🔹 Nếu là phiếu cá nhân
    if (loaiPhieu.value === "Cá nhân") {
      if (!khachHangSelected.value.length) {
        notify.error("Vui lòng chọn 1 khách hàng!");
        return;
      }

      // 1️⃣ Tạo phiếu giảm giá
      const { data: phieu } = await createPhieuGiamGia(JSON.parse(JSON.stringify(payload)));

      // 2️⃣ Chuẩn bị dữ liệu phiếu cá nhân
      const payloadCaNhan = {
        trangThai: true,
        ten: form.ten,
        ngayNhan: form.ngayBatDau,
        ngayHetHan: form.ngayKetThuc,
        phieuGiamGia: { id: phieu.id },
        khachHang: { id: khachHangSelected.value[0].id },
      };

      // 3️⃣ Gửi dữ liệu cá nhân
       res = await createPhieuGiamGiaCaNhan(payloadCaNhan);
       if (!res) throw new Error("Lỗi khi thêm đợt giảm giá cá nhân");
    } else {
      // 🔹 Nếu là phiếu chung
      res = await createPhieuGiamGia(JSON.parse(JSON.stringify(payload)));
      if (!res) throw new Error("Lỗi khi thêm đợt giảm giá");
    }

    // 🔹 Thông báo và điều hướng
    notify.success("Thêm phiếu giảm giá thành công!");
    router.push("/admin/phieu-giam-gia");
  } catch (error) {
    if (error.response?.status === 400) {
      // 🔹 BE trả về object có errors
      const backendErrors = error.response.data.errors || error.response.data;
      Object.assign(errors, backendErrors);
      console.log("Lỗi validation:", backendErrors);
      notify.error("Vui lòng kiểm tra lại thông tin!");
    } else {
      console.error("Lỗi khi thêm phiếu giảm giá:", error);
      notify.error("Thêm thất bại, vui lòng thử lại!");
    }
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

  if (result.isConfirmed) {
    await addPhieuGiamGia(); // gọi hàm lưu
  }
};
</script>
<template>
  <div class="container-fluid mt-4 px-1">
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
              :class="{ 'is-invalid': errors.ten }"
              placeholder="Nhập họ tên phiếu giảm giá"
            />
            <div class="invalid-feedback">{{ errors.ten }}</div>
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
          <!-- Hình thức giảm giá -->
          <div class="col-md-6">
            <label class="form-label d-block">Hình thức giảm giá</label>
            <div class="d-flex gap-3">
              <div class="form-check custom-radio">
                <input
                  class="form-check-input"
                  type="radio"
                  id="%"
                  :value="true"
                  v-model="form.hinhThucGiamGia"
                  checked
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
              :class="{ 'is-invalid': errors.giaTriGiam }"
              placeholder="Nhập giá trị giảm"
            />
            <div class="invalid-feedback">{{ errors.giaTriGiam }}</div>
          </div>

          <!-- Giá trị giảm tối thiểu-->
          <div class="col-md-6" v-if="form.hinhThucGiamGia === true">
            <label class="form-label">Giá trị giảm tối thiểu</label>
            <input
              v-model="form.giaTriGiamToiThieu"
              type="number"
              class="form-control"
              placeholder="Nhập giá trị giảm tối thiểu"
            />
          </div>
          <!-- Giá trị giảm tối đa-->
          <div class="col-md-6" v-if="form.hinhThucGiamGia === true">
            <label class="form-label">Giá trị giảm tối đa</label>
            <input
              v-model="form.giaTriGiamToiDa"
              type="number"
              class="form-control"
              :class="{ 'is-invalid': errors.giaTriGiamToiDa }"
              placeholder="Nhập giá trị giảm tối đa"
            />
            <div class="invalid-feedback" v-if="form.hinhThucGiamGia === false">
              {{ errors.giaTriGiamToiDa }}
            </div>
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
          <!-- Loại giảm giá -->
          <div class="col-md-6">
            <label class="form-label d-block">Loại phiếu giảm giá</label>
            <div class="d-flex gap-3">
              <div class="form-check custom-radio">
                <input
                  class="form-check-input"
                  type="radio"
                  id="congKhai"
                  value="Công khai"
                  v-model="loaiPhieu"
                  checked
                />
                <label class="form-check-label">Công khai</label>
              </div>
              <div class="form-check custom-radio">
                <input
                  class="form-check-input"
                  type="radio"
                  id="caNhan"
                  value="Cá nhân"
                  v-model="loaiPhieu"
                  @click="loadKhachHang"
                />
                <label class="form-check-label">Cá nhân</label>
              </div>
            </div>
          </div>
        </div>

        <div></div>

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

    <!-- Hiển thị danh sách khách hàng khi bấm vào loại phiếu cá nhân -->
    <div class="row" v-if="loaiPhieu === 'Cá nhân'">
      <div class="col-md-12">
        <div class="card">
          <div class="card-header">
            <div class="d-flex align-items-center justify-content-between">
              <h4 class="card-title mb-0">Danh sách Khách hàng</h4>
            </div>
          </div>

          <div class="card-body">
            <div class="table-responsive">
              <table id="add-row" class="display table">
                <thead>
                  <tr style="text-align: center">
                    <th class="text-center">
                      <input
                        type="checkbox"
                      />
                    </th>
                    <th>STT</th>
                    <th>Mã</th>
                    <th>Ảnh</th>
                    <th>Họ tên</th>
                    <th>SĐT</th>
                    <th>Email</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(p, index) in khachHang" :key="p.id">
                    <td>
                      <input
                        type="checkbox"
                        :value="p"
                        v-model="khachHangSelected"
                      />
                    </td>
                    <td>{{ index + 1 + page * size }}</td>
                    <td>
                      {{ p.ma }}
                    </td>
                    <td class="text-center">
                      <img
                        :src="p.urlAnh || '/src/assets/img/default-avatar.png'"
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
                    <td>{{ p.hoTen }}</td>
                    <td>{{ p.sdt }}</td>
                    <td>{{ p.email }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <nav aria-label="Page navigation">
              <ul class="pagination justify-content-end mt-3">
                <li class="page-item" :class="{ disabled: page === 0 }">
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(page - 1)"
                    >Trước</a
                  >
                </li>
                <li
                  class="page-item"
                  v-for="n in totalPages"
                  :key="n"
                  :class="{ active: n - 1 === page }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(n - 1)"
                    >{{ n }}</a
                  >
                </li>
                <li
                  class="page-item"
                  :class="{ disabled: page === totalPages - 1 }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="gotoPage(page + 1)"
                    >Sau</a
                  >
                </li>
              </ul>
            </nav>
          </div>
        </div>
      </div>
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
.custom-radio .form-check-input:checked {
  background-color: #ffc107 !important; /* màu cam */
  border-color: #ffc107 !important;
}
</style>