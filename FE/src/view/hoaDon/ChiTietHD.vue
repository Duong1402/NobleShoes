<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  getHoaDonById,
  updateHoaDon,
  getLichSuHoaDon,
} from "@/service/HoaDonService";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";

const route = useRoute();
const router = useRouter();
const notify = useNotify();

const hoaDon = ref(null);
const currentStep = ref(1);
const showHistory = ref(false);
const lichSuThayDoi = ref([]);
const lichSuHienThi = ref([]); // sẽ dùng để render timeline

const TRANG_THAI_HOA_DON = {
  1: "Chờ xác nhận",
  2: "Đã xác nhận",
  3: "Chờ thanh toán",
  4: "Đang giao",
  5: "Hoàn thành",
  0: "Đã hủy",
};

const formatDateTime = (str) => {
  if (!str) return "";
  const d = new Date(str);
  // format theo vi-VN, nếu backend đã trả dạng yyyy-mm-dd hh:mm thì new Date có thể parse
  return d.toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

// Hàm load dữ liệu (hoaDon + lich su)
const loadData = async (id) => {
  // 1. Tải Hóa đơn
  try {
    const res = await getHoaDonById(id);
    hoaDon.value = res.data;
    console.log("Response data received:", res.data);
    currentStep.value = Number(hoaDon.value.trangThai);
  } catch (err) {
    console.error("Lỗi load hoa don:", err);
    notify.error("Không tải được thông tin hóa đơn!");
    return;
  }

  // 2. Tải Lịch sử (Dữ liệu này dùng để hiển thị trong Modal)
  try {
    const historyRes = await getLichSuHoaDon(id);
    // Lịch sử được gán vào lichSuThayDoi.value
    lichSuThayDoi.value = historyRes.data || [];
    // Log để kiểm tra dữ liệu
    console.log("Lịch sử thay đổi:", lichSuThayDoi.value);
  } catch (err) {
    console.warn("Không có lịch sử hoặc lỗi gọi lịch sử:", err);
    lichSuThayDoi.value = [];
  }

  // 3. --- Xây dựng lichSuHienThi (Timeline) ---
  const trangThaiHienTai = Number(hoaDon.value.trangThai);
  const steps = [];

  // Yêu cầu: Nếu trạng thái là 5 (Hoàn thành) HOẶC 0 (Đã hủy), chỉ hiển thị 1 bước duy nhất
  if (trangThaiHienTai === 5 || trangThaiHienTai === 0) {
    lichSuHienThi.value = [
      {
        id: trangThaiHienTai,
        text: TRANG_THAI_HOA_DON[trangThaiHienTai],
        thoiGian: hoaDon.value.ngayCapNhat || hoaDon.value.ngayTao,
        isDone: trangThaiHienTai === 5,
        isCanceled: trangThaiHienTai === 0,
      },
    ];
    return;
  }

  // Trường hợp còn lại (1, 2, 3, 4): Xây dựng timeline dựa trên lịch sử (ưu tiên) hoặc trạng thái hiện tại

  if (lichSuThayDoi.value.length > 0) {
    const statusSet = new Set();
    // ⚠️ Sửa: Sắp xếp theo THỜI GIAN, không phải trạng thái mới
    const sortedHistory = [...lichSuThayDoi.value].sort((a, b) => {
      // Giả sử item.thoiGian là timestamp/ISO string mà new Date() có thể so sánh
      const timeA = new Date(a.thoiGian || a.ngayTao || 0);
      const timeB = new Date(b.thoiGian || b.ngayTao || 0);
      return timeA.getTime() - timeB.getTime();
    });

    // Tạo một map để lưu thời gian của lần chuyển đổi cuối cùng cho mỗi trạng thái
    const statusTimes = {};
    sortedHistory.forEach((item) => {
      const statusId = item.trangThaiMoi;
      if (statusId >= 1 && statusId <= 5) {
        const thoiGian =
          item.thoiGianCapNhat || item.thoiGian || item.ngayTao || null;
        // Chỉ lưu lần chuyển đổi cuối cùng đến trạng thái này
        statusTimes[statusId] = thoiGian;
      }
    });

    // Xây dựng steps từ 1 đến trạng thái hiện tại, lấy thời gian từ statusTimes
    for (let s = 1; s <= 5; s++) {
      // Lặp qua tất cả 5 bước chính
      if (s <= trangThaiHienTai) {
        // Chỉ thêm các bước <= trạng thái hiện tại
        steps.push({
          id: s,
          text: TRANG_THAI_HOA_DON[s],
          thoiGian: statusTimes[s] || hoaDon.value.ngayTao, // Lấy thời gian từ lịch sử hoặc ngày tạo
          isDone: s < trangThaiHienTai,
          isCurrent: s === trangThaiHienTai,
          isCanceled: false,
        });
      }
    }

    lichSuHienThi.value = steps;
  } else {
    // Không có lịch sử (Có thể là hóa đơn mới vừa tạo), tạo các bước từ 1 đến trạng thái hiện tại
    const steps = [];
    for (let s = 1; s <= trangThaiHienTai; s++) {
      steps.push({
        id: s,
        text: TRANG_THAI_HOA_DON[s],
        thoiGian: hoaDon.value.ngayCapNhat || hoaDon.value.ngayTao,
        isDone: s < trangThaiHienTai,
        isCurrent: s === trangThaiHienTai,
        isCanceled: false,
      });
    }
    lichSuHienThi.value = steps;
  }
};

console.log("📌 HoaDon:", hoaDon.value);
console.log("📌 Chi tiết SP:", hoaDon.value?.chiTietSanPham);

// load lần đầu
onMounted(async () => {
  const id = route.params.id;
  if (id) {
    await loadData(id);
    console.log("🌟 HoaDon raw data:", hoaDon.value);
  }
});

// reload khi route.params.id thay đổi (khi click view detail từ list)
watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadData(newId);
    }
  }
);

const handleSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận lưu thay đổi?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Hủy",
  });

  if (result.isConfirmed && hoaDon.value) {
    try {
      await updateHoaDon(hoaDon.value.id, {
        trangThai: hoaDon.value.trangThai,
        sdt: hoaDon.value.sdt,
        diaChiGiaoHang: hoaDon.value.diaChiGiaoHang,
      });

      notify.success("Cập nhật thành công!");
      router.push({ name: "HoaDon" });
    } catch (err) {
      console.error("Lỗi cập nhật:", err);
      notify.error("Cập nhật thất bại!");
    }
  }
};

// Hàm thay đổi trạng thái bằng nút (có tùy chọn ghi chú khi hủy)
const confirmChange = async (newStatus) => {
  if (!hoaDon.value) return;
  const oldStatus = hoaDon.value.trangThai;

  // Nếu là hủy đơn => popup ghi chú
  if (newStatus === 0) {
    const result = await Swal.fire({
      title: "Xác nhận hủy đơn hàng?",
      input: "text",
      inputPlaceholder: "Nhập lý do hủy (không bắt buộc)",
      showCancelButton: true,
      confirmButtonText: "Xác nhận hủy",
      cancelButtonText: "Đóng",
      icon: "warning",
      inputAttributes: { maxlength: 255 },
      preConfirm: (note) => note?.trim() || "",
    });

    if (result.isConfirmed) {
      try {
        await updateHoaDon(hoaDon.value.id, {
          trangThai: newStatus,
          ghiChu: result.value || "",
        });
        notify.success("Đã hủy đơn hàng!");
        await loadData(hoaDon.value.id);
      } catch (err) {
        console.error("Lỗi hủy đơn:", err);
        notify.error("Không thể hủy đơn hàng!");
      }
    }
  } else {
    const result = await Swal.fire({
      title: "Xác nhận thay đổi trạng thái?",
      text: `Chuyển từ "${TRANG_THAI_HOA_DON[oldStatus]}" sang "${TRANG_THAI_HOA_DON[newStatus]}"`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Xác nhận",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      try {
        await updateHoaDon(hoaDon.value.id, {
          trangThai: newStatus,
          ghiChu: "",
        });
        notify.success("Cập nhật trạng thái thành công!");
        await loadData(hoaDon.value.id);
      } catch (err) {
        console.error("Lỗi cập nhật trạng thái:", err);
        notify.error("Không thể cập nhật trạng thái!");
      }
    }
  }
};
</script>

<template>
  <div class="container mt-4 px-1" v-if="hoaDon">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h3 class="fw-bold text-warning">Hóa đơn: {{ hoaDon.ma }}</h3>
      <button
        class="btn btn-secondary"
        @click="router.push({ name: 'HoaDon' })"
      >
        ← Quay lại
      </button>
    </div>

    <!-- Timeline -->
    <div class="card shadow-sm mb-4 p-4">
      <h5>Lịch sử đơn hàng</h5>

      <div
        class="timeline-container d-flex align-items-center position-relative"
        v-if="lichSuHienThi && lichSuHienThi.length"
      >
        <div
          v-for="(step, index) in lichSuHienThi"
          :key="step.id + '-' + index"
          class="timeline-step text-center flex-fill"
        >
          <div
            class="timeline-circle mx-auto"
            :class="{
              done:
                index < lichSuHienThi.length - 1 || step.text === 'Hoàn thành',
              current:
                index === lichSuHienThi.length - 1 &&
                step.text !== 'Hoàn thành',
            }"
          >
            <span class="circle-number">{{ index + 1 }}</span>
          </div>

          <div class="timeline-label mt-2">{{ step.text }}</div>
          <div v-if="step.thoiGian" class="text-muted small mt-1">
            {{ formatDateTime(step.thoiGian) }}
          </div>

          <!-- connector line (we keep it visual): use pseudo element via CSS, but keep fallback div for compatibility -->
          <div
            v-if="index < lichSuHienThi.length - 1"
            class="timeline-line"
          ></div>
        </div>
      </div>

      <div class="text-start mt-3">
        <button
          class="btn btn-outline-warning btn-sm"
          @click="showHistory = true"
        >
          Chi tiết lịch sử
        </button>
      </div>
    </div>

    <!-- Thông tin chung -->
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
          <label>Số điện thoại</label>
          <input class="form-control" v-model="hoaDon.sdt" disabled />
        </div>

        <div class="col-md-6">
          <label>Trạng thái hiện tại</label>
          <div class="d-flex align-items-center gap-2 mt-1 flex-wrap">
            <span
              class="badge fs-6"
              :class="{
                'bg-secondary': hoaDon.trangThai == 1,
                'bg-info': hoaDon.trangThai == 2,
                'bg-primary': hoaDon.trangThai == 3,
                'bg-warning text-dark': hoaDon.trangThai == 4,
                'bg-success': hoaDon.trangThai == 5,
                'bg-danger': hoaDon.trangThai == 0,
              }"
            >
              {{ TRANG_THAI_HOA_DON[hoaDon.trangThai] }}
            </span>
          </div>

          <div class="mt-3 d-flex flex-wrap gap-2">
            <!-- Chờ xác nhận -->
            <button
              v-if="hoaDon.trangThai == 1"
              class="btn btn-success btn-sm"
              @click="confirmChange(2)"
            >
              ✅ Xác nhận
            </button>
            <button
              v-if="hoaDon.trangThai == 1"
              class="btn btn-outline-danger btn-sm"
              @click="confirmChange(0)"
            >
              ❌ Hủy
            </button>

            <!-- Đã xác nhận -->
            <button
              v-if="hoaDon.trangThai == 2"
              class="btn btn-primary btn-sm"
              @click="confirmChange(3)"
            >
              ➡️ Chờ thanh toán
            </button>
            <button
              v-if="hoaDon.trangThai == 2"
              class="btn btn-outline-secondary btn-sm"
              @click="confirmChange(1)"
            >
              🔙 Quay lại
            </button>
            <button
              v-if="hoaDon.trangThai == 2"
              class="btn btn-outline-danger btn-sm"
              @click="confirmChange(0)"
            >
              ❌ Hủy
            </button>

            <!-- Chờ thanh toán -->
            <button
              v-if="hoaDon.trangThai == 3"
              class="btn btn-warning btn-sm"
              @click="confirmChange(4)"
            >
              🚚 Giao hàng
            </button>
            <button
              v-if="hoaDon.trangThai == 3"
              class="btn btn-outline-secondary btn-sm"
              @click="confirmChange(2)"
            >
              🔙 Quay lại
            </button>
            <button
              v-if="hoaDon.trangThai == 3"
              class="btn btn-outline-danger btn-sm"
              @click="confirmChange(0)"
            >
              ❌ Hủy
            </button>

            <!-- Đang giao -->
            <button
              v-if="hoaDon.trangThai == 4"
              class="btn btn-success btn-sm"
              @click="confirmChange(5)"
            >
              🎉 Hoàn thành
            </button>
            <button
              v-if="hoaDon.trangThai == 4"
              class="btn btn-outline-secondary btn-sm"
              @click="confirmChange(3)"
            >
              🔙 Quay lại
            </button>
            <button
              v-if="hoaDon.trangThai == 4"
              class="btn btn-outline-danger btn-sm"
              @click="confirmChange(0)"
            >
              ❌ Hủy
            </button>
            <!-- Hoàn thành -->
            <button
              v-if="hoaDon.trangThai == 5"
              class="btn btn-outline-secondary btn-sm"
              @click="confirmChange(4)"
            >
              🔙 Quay lại
            </button>
            <!-- Đã hủy -->
            <button
              v-if="hoaDon.trangThai == 0"
              class="btn btn-outline-primary btn-sm"
              @click="confirmChange(1)"
            >
              ↩️ Khôi phục (Chờ xác nhận)
            </button>
          </div>
        </div>

        <div class="col-12" v-if="hoaDon">
          <div
            v-if="
              (hoaDon.loaiHoaDon &&
                String(hoaDon.loaiHoaDon).toLowerCase() === 'online') ||
              (hoaDon.diaChiGiaoHang && hoaDon.diaChiGiaoHang.length > 0)
            "
          >
            <label class="form-label fw-bold">
              <i class="fa-solid fa-truck-fast me-1 text-warning"></i> Địa chỉ
              giao hàng
            </label>

            <textarea
              class="form-control"
              rows="3"
              :value="hoaDon.diaChiGiaoHang || 'Chưa có địa chỉ giao hàng'"
              readonly
              style="background-color: #e9ecef; cursor: default; resize: none"
            ></textarea>
          </div>
        </div>
      </div>
    </div>

    <!-- Danh sách sản phẩm -->
    <div class="card shadow-sm mb-4 p-4 table-card">
      <h5 class="fw-bold mb-3">Danh sách sản phẩm</h5>
      <div class="table-responsive">
        <table class="table align-middle">
          <thead class="table-light">
            <tr>
              <th>Ảnh</th>
              <th>Tên SP</th>
              <th class="text-center">Số lượng</th>
              <th class="text-center">Màu sắc</th>
              <th class="text-center">Size</th>
              <th class="text-end">Đơn giá</th>
              <th class="text-end">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(sp, i) in hoaDon.chiTietSanPham" :key="i">
              <td class="text-center">
                <img
                  :src="sp.hinhAnhUrl"
                  class="img-thumbnail"
                  style="width: 70px; height: 70px; object-fit: cover"
                />
              </td>
              <td>{{ sp.tenSanPham }}</td>
              <td class="text-center">{{ sp.soLuong }}</td>
              <td class="text-center">{{ sp.mauSac }}</td>
              <td class="text-center">{{ sp.size }}</td>
              <td class="text-end">{{ sp.donGia.toLocaleString() }} ₫</td>
              <td class="text-end">{{ sp.thanhTien.toLocaleString() }} ₫</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Tổng tiền -->
    <div class="card shadow-sm mb-5 p-4 outer-total-card">
      <div class="row">
        <!-- Phiếu giảm giá -->
        <div class="col-md-6">
          <div class="card p-3 inner-card">
            <p class="mb-0">
              <strong>Phiếu giảm giá:</strong>
              {{ hoaDon.phieuGiamGia || "Không áp dụng" }}
            </p>
          </div>
        </div>

        <!-- Tổng tiền -->
        <div class="col-md-6">
          <div class="card p-3 text-end inner-card">
            <p class="mb-1">
              Tổng tiền hàng: {{ hoaDon.tongTien.toLocaleString() }} ₫
            </p>
            <p class="mb-1">Phí vận chuyển: Miễn phí (0 ₫)</p>
            <h5 class="fw-bold text-danger mb-0">
              Tổng tiền: {{ hoaDon.tongTien.toLocaleString() }} ₫
            </h5>
          </div>
        </div>
      </div>
    </div>

    <!-- Nút hành động -->
    <div class="d-flex justify-content-end gap-2">
      <button
        class="btn btn-secondary"
        @click="router.push({ name: 'HoaDon' })"
      >
        Hủy
      </button>
      <button class="btn btn-warning text-white" @click="handleSave">
        💾 Lưu thay đổi
      </button>
    </div>

    <!-- Modal lịch sử -->
    <div
      class="modal fade show d-block"
      tabindex="-1"
      style="background: rgba(0, 0, 0, 0.4)"
      v-if="showHistory"
    >
      <div class="modal-dialog modal-xl modal-dialog-centered">
        <div class="modal-content shadow-lg">
          <div class="modal-header">
            <h5 class="modal-title fw-bold">Lịch sử đơn hàng</h5>
            <button
              type="button"
              class="btn-close"
              @click="showHistory = false"
            ></button>
          </div>
          <div class="modal-body">
            <table class="table table-striped align-middle text-center">
              <thead class="table-light">
                <tr>
                  <th>Thời gian</th>
                  <th>Người chỉnh sửa</th>
                  <th>Trạng thái HĐ</th>
                  <th>Ghi chú</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="lichSuThayDoi.length === 0">
                  <td colspan="4" class="text-muted py-4">Không có lịch sử.</td>
                </tr>
                <tr v-for="(item, i) in lichSuThayDoi" :key="i">
                  <td>{{ formatDateTime(item.thoiGian || item.ngayTao) }}</td>
                  <td>{{ item.nguoiCapNhat || item.nguoiThucHien || "-" }}</td>
                  <td>
                    {{
                      TRANG_THAI_HOA_DON[item.trangThaiMoi] ||
                      item.tenTrangThai ||
                      item.trangThaiMoi ||
                      "-"
                    }}
                  </td>
                  <td>{{ item.ghiChu || "-" }}</td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="modal-footer justify-content-center border-0">
            <button class="btn btn-secondary px-5" @click="showHistory = false">
              OK
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Modal */
.modal {
  overflow-y: auto;
  z-index: 1050;
}
.modal-content {
  border-radius: 12px;
}
.modal-footer {
  padding-top: 0;
  padding-bottom: 20px;
}

/* layout tweaks preserved from original file */
.table-card,
.total-card {
  width: 100%;
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
}
.table {
  width: 100%;
}
.table th,
.table td {
  vertical-align: middle;
}
.text-end {
  text-align: right;
}
.text-center {
  text-align: center;
}
.container {
  max-width: 1500px !important;
}
.total-card {
  border: 1px solid #dee2e6;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.outer-total-card {
  background-color: #fff;
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}
.inner-card {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  background-color: #fff;
}

/* Timeline */
.timeline-container {
  position: relative;
  gap: 8px;
}

/* ensure steps spread evenly */
.timeline-step {
  position: relative;
  flex: 1 1 0;
  padding: 0 8px;
}

/* circle */
.timeline-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #e0e0e0;
  color: #555;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  z-index: 2;
  margin: 0 auto;
  position: relative;
  transition: all 0.25s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.timeline-circle .circle-number {
  font-weight: 700;
}

/* done = những bước trước bước hiện tại */
.timeline-circle.done {
  background-color: #198754;
  color: white;
}

/* current = bước hiện tại */
.timeline-circle.current {
  background-color: #f5c542;
  color: #000;
  transform: scale(1.05);
}

/* connector line: use pseudo element so it stretches between steps */
.timeline-step::after {
  content: "";
  position: absolute;
  top: 24px; /* vertical center-ish */
  right: 0;
  width: calc(100% - 32px); /* extend to next step */
  height: 4px;
  background-color: #ddd;
  z-index: 1;
}

/* hide last connector */
.timeline-step:last-child::after {
  display: none;
}

/* label */
.timeline-label {
  font-size: 0.95rem;
  margin-top: 8px;
  font-weight: 600;
}

/* timeline-line fallback (if browser doesn't like pseudo rule) */
.timeline-line {
  position: absolute;
  top: 28px;
  right: -50%;
  width: 100%;
  height: 4px;
  background-color: transparent;
}

.timeline-circle.done {
  background-color: #198754;
  color: #fff;
}

.timeline-circle.current {
  background-color: #f5c542;
  color: #000;
  transform: scale(1.05);
}

.timeline-circle.canceled {
  background-color: #dc3545 !important;
  color: #fff !important;
  border: 2px solid #a71d2a;
}

.timeline-step.done::after {
  background-color: #198754;
}

.timeline-step.canceled::after {
  background-color: #dc3545;
}
</style>
