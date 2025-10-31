<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getHoaDonById, updateHoaDon } from "@/service/HoaDonService";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const notify = useNotify();

const hoaDon = ref(null);
const TRANG_THAI_HOA_DON = {
  0: { text: "Đã hủy" },
  1: { text: "Chờ xác nhận" },
  2: { text: "Đã xác nhận" },
  3: { text: "Đang giao" },
  4: { text: "Hoàn thành" },
  5: { text: "Chờ thanh toán" },
};

onMounted(async () => {
  const id = route.params.id;
  const res = await getHoaDonById(id);
  hoaDon.value = res.data;
});

const handleSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Hủy",
  });

  if (result.isConfirmed && hoaDon.value) {
    await updateHoaDon(hoaDon.value.id, {
      trangThai: hoaDon.value.trangThai,
      sdt: hoaDon.value.sdt,
      diaChiGiaoHang: hoaDon.value.diaChiGiaoHang,
    });
    notify.success("Cập nhật thành công!");
    router.push({ name: "HoaDon" });
  }
};
</script>

<template>
  <div class="container mt-4 px-5" v-if="hoaDon">
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3 class="fw-bold text-warning">
        Hóa đơn: {{ hoaDon.ma }}
      </h3>
      <button class="btn btn-secondary" @click="router.push({ name: 'HoaDon' })">
        ← Quay lại
      </button>
    </div>

    <div class="card shadow-sm mb-4 p-4">
      <h5>Thông tin chung</h5>
      <div class="row g-3">
        <div class="col-md-6">
          <label>Tên khách hàng</label>
          <input class="form-control" v-model="hoaDon.tenKhachHang" disabled />
        </div>
        <div class="col-md-6">
          <label>Tên nhân viên</label>
          <input class="form-control" v-model="hoaDon.tenNhanVien" disabled />
        </div>

        <div class="col-md-6">
          <label>Trạng thái</label>
          <select class="form-select" v-model="hoaDon.trangThai">
            <option v-for="(v, k) in TRANG_THAI_HOA_DON" :key="k" :value="k">
              {{ v.text }}
            </option>
          </select>
        </div>
        <div class="col-md-6">
          <label>Số điện thoại</label>
          <input class="form-control" v-model="hoaDon.sdt" />
        </div>

        <div class="col-12">
          <label>Địa chỉ giao hàng</label>
          <textarea class="form-control" rows="2" v-model="hoaDon.diaChiGiaoHang" />
        </div>
      </div>
    </div>

    <div class="card shadow-sm mb-4 p-4">
      <h5>Danh sách sản phẩm</h5>
      <table class="table">
        <thead>
          <tr>
            <th>Tên SP</th>
            <th class="text-center">Số lượng</th>
            <th class="text-end">Đơn giá</th>
            <th class="text-end">Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(sp, i) in hoaDon.chiTietSanPham" :key="i">
            <td>{{ sp.tenSanPham }}</td>
            <td class="text-center">{{ sp.soLuong }}</td>
            <td class="text-end">{{ sp.donGia.toLocaleString() }} ₫</td>
            <td class="text-end">{{ sp.thanhTien.toLocaleString() }} ₫</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-end gap-2">
      <button class="btn btn-secondary" @click="router.push({ name: 'HoaDon' })">Hủy</button>
      <button class="btn btn-warning text-white" @click="handleSave">
        💾 Lưu thay đổi
      </button>
    </div>
  </div>
</template>
