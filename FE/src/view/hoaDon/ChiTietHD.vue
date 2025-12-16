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
const currentStep = ref(0);
const showHistory = ref(false);
const lichSuThayDoi = ref([]);
const lichSuHienThi = ref([]);

const LOAI_HOA_DON = ["Online", "Tại cửa hàng"];

const TRANG_THAI_HOA_DON = {
  0: "Chờ thanh toán",
  1: "Chờ xác nhận",
  2: "Đã xác nhận",
  3: "Đang giao",
  4: "Hoàn thành",
  5: "Đã hủy",
};

const formatDateTime = (str) => {
  if (!str) return "";
  const d = new Date(str);
  return d.toLocaleString("vi-VN", {
    hour: "2-digit",
    minute: "2-digit",
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  });
};

const renderTimeline = () => {
  if (!hoaDon.value) return;

  const currentStatus = Number(hoaDon.value.trangThai ?? 0);
  const currentType = hoaDon.value.loaiHoaDon;
  const isTaiCuaHang =
    currentType && currentType.toLowerCase() === "tại cửa hàng";

  let allowedSteps = [];

  if (isTaiCuaHang) {
    allowedSteps = [0, 4];
  } else {
    allowedSteps = [1, 2, 3, 4];
  }
  const findTimeInHistory = (status) => {
    const statusNum = Number(status);
    const item = lichSuThayDoi.value.find(
      (h) => Number(h.trangThaiMoi) === statusNum
    );
    let time =
      item?.thoiGianCapNhat ||
      item?.thoiGian ||
      item?.ngayTao ||
      item?.createDate ||
      item?.createdAt;

    if (!time && statusNum === currentStatus) {
      time = hoaDon.value.ngayCapNhat || hoaDon.value.ngayTao;
    }
    return time || null;
  };

  if (currentStatus === 5) {
    lichSuHienThi.value = [
      {
        id: 5,
        text: TRANG_THAI_HOA_DON[5],
        thoiGian:
          hoaDon.value.thoiGianHuy ||
          hoaDon.value.ngayCapNhat ||
          hoaDon.value.ngayTao,
        isCanceled: true,
        isDone: true,
        isCurrent: true,
      },
    ];
  } else {
    const visibleSteps = allowedSteps.filter((step) => step <= currentStatus);

    const anchorTime =
      findTimeInHistory(currentStatus) ||
      hoaDon.value.ngayCapNhat ||
      new Date().toISOString();

    lichSuHienThi.value = visibleSteps.map((step) => {
      let thoiGian = findTimeInHistory(step);

      if (!thoiGian) {
        if (step === 0) {
          thoiGian = hoaDon.value.ngayTao;
        } else {
          thoiGian = anchorTime;
        }
      }

      return {
        id: step,
        text: TRANG_THAI_HOA_DON[step],
        thoiGian: thoiGian,
        isDone: true,
        isCurrent: step === currentStatus,
        isCanceled: false,
      };
    });
  }
};

const loadData = async (id) => {
  try {
    const res = await getHoaDonById(id);
    hoaDon.value = res.data;
    currentStep.value = Number(hoaDon.value.trangThai ?? 0);
  } catch (err) {
    console.error("Lỗi load hoa don:", err);
    notify.error("Không tải được thông tin hóa đơn!");
    return;
  }

  try {
    const historyRes = await getLichSuHoaDon(id);
    lichSuThayDoi.value = historyRes.data || [];
  } catch (err) {
    lichSuThayDoi.value = [];
  }

  renderTimeline();
};

onMounted(async () => {
  const id = route.params.id;
  if (id) {
    await loadData(id);
  }
});

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
const confirmChange = async (newStatus) => {
  if (!hoaDon.value) return;
  const oldStatus = hoaDon.value.trangThai;

  let isConfirmed = false;
  let cancelReason = "";

  if (newStatus === 5) {
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
      isConfirmed = true;
      cancelReason = result.value || "";
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
    isConfirmed = result.isConfirmed;
  }

  if (isConfirmed) {
    try {
      await updateHoaDon(hoaDon.value.id, {
        trangThai: newStatus,
        ghiChu: cancelReason,
      });

      notify.success(
        newStatus === 5 ? "Đã hủy đơn hàng!" : "Cập nhật trạng thái thành công!"
      );

      const now = new Date().toISOString();

      const userInfor = JSON.parse(localStorage.getItem("userData")) || {};
      const realName = userInfor.hoTen || "Quản lý";

      hoaDon.value.trangThai = newStatus;
      hoaDon.value.ngayCapNhat = now;
      if (newStatus === 5) hoaDon.value.thoiGianHuy = now;
      currentStep.value = newStatus;

      lichSuThayDoi.value.push({
        trangThaiMoi: newStatus,
        thoiGian: now,
        thoiGianCapNhat: now,
        nguoiChinhSua: realName,
        ghiChu: cancelReason,
      });
      renderTimeline();
      await loadData(hoaDon.value.id);
    } catch (err) {
      console.error("Lỗi cập nhật:", err);
      notify.error("Có lỗi xảy ra, vui lòng thử lại!");
      loadData(hoaDon.value.id);
    }
  }
};

const getStepIcon = (stepId) => {
  switch (stepId) {
    case 0:
      return "fa-file-invoice-dollar";
    case 1:
      return "fa-clipboard-check";
    case 2:
      return "fa-box-open";
    case 3:
      return "fa-truck-fast";
    case 4:
      return "fa-check-circle";
    case 5:
      return "fa-ban";
    default:
      return "fa-circle";
  }
};

const getStepColor = (stepId) => {
  switch (stepId) {
    case 0:
      return "#ffc107";
    case 1:
      return "#6c757d";
    case 2:
      return "#0dcaf0";
    case 3:
      return "#0d6efd";
    case 4:
      return "#198754";
    case 5:
      return "#dc3545";
    default:
      return "#e9ecef";
  }
};

const getCircleStyle = (step) => {
  const color = getStepColor(step.id);
  if (step.isCurrent) {
    return {
      backgroundColor: color,
      borderColor: color,
      color: "#fff",
      boxShadow: `0 0 0 5px ${color}33`,
    };
  }
  if (step.isDone) {
    return {
      backgroundColor: "#fff",
      borderColor: color,
      color: color,
    };
  }
  return {};
};

const calculateProgressWidth = () => {
  if (!lichSuHienThi.value || lichSuHienThi.value.length <= 1) return 0;
  let lastDoneIndex = lichSuHienThi.value.findLastIndex((s) => s.isDone);
  if (hoaDon.value.trangThai === 5) return 100;

  const totalSteps = lichSuHienThi.value.length - 1;
  return (lastDoneIndex / totalSteps) * 100;
};
</script>

<template>
  <div class="container-fluid mt-4" v-if="hoaDon">
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
    <div class="card shadow-sm mb-4 p-4 border-0">
      <h5 class="fw-bold mb-4 text-secondary">
        <i class="fa-solid fa-clock-rotate-left me-2"></i>Tiến độ đơn hàng
      </h5>

      <div
        class="timeline-wrapper px-3 py-3"
        v-if="lichSuHienThi && lichSuHienThi.length"
      >
        <div class="d-flex justify-content-between position-relative">
          <div class="progress-line-bg"></div>

          <div
            class="progress-line-fill"
            :style="{
              width: calculateProgressWidth() + '%',
              backgroundColor: getStepColor(hoaDon.trangThai),
            }"
          ></div>

          <div
            v-for="(step, index) in lichSuHienThi"
            :key="index"
            class="timeline-item position-relative d-flex flex-column align-items-center"
            :class="{
              active: step.isDone,
              current: step.isCurrent,
              canceled: step.isCanceled,
            }"
            style="z-index: 2; flex: 1"
          >
            <div
              class="icon-circle shadow-sm d-flex align-items-center justify-content-center"
              :style="getCircleStyle(step)"
            >
              <i :class="['fa-solid', getStepIcon(step.id)]"></i>
            </div>

            <div class="mt-2 text-center content-box">
              <div
                class="fw-bold step-text"
                :style="{
                  color: step.isDone ? getStepColor(step.id) : '#6c757d',
                }"
              >
                {{ step.text }}
              </div>

              <div v-if="step.thoiGian" class="step-time text-secondary">
                {{ formatDateTime(step.thoiGian) }}
              </div>

              <div
                v-else-if="step.isDone"
                class="step-time text-muted fst-italic"
              >
                --:--
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="text-end mt-3">
        <button
          class="btn btn-link text-decoration-none fw-bold"
          @click="showHistory = true"
        >
          Xem chi tiết lịch sử <i class="fa-solid fa-arrow-right ms-1"></i>
        </button>
      </div>
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
                  <th>Ghi chú</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="lichSuThayDoi.length === 0">
                  <td colspan="4" class="text-muted py-4">Không có lịch sử.</td>
                </tr>
                <tr v-for="(item, i) in lichSuThayDoi" :key="i">
                  <td>{{ formatDateTime(item.thoiGian || item.ngayTao) }}</td>
                  <td>{{ item.nguoiChinhSua || "-" }}</td>
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
                'bg-warning text-dark': hoaDon.trangThai == 0,
                'bg-secondary': hoaDon.trangThai == 1,
                'bg-info': hoaDon.trangThai == 2,
                'bg-primary': hoaDon.trangThai == 3,
                'bg-success': hoaDon.trangThai == 4,
                'bg-danger': hoaDon.trangThai == 5,
              }"
            >
              {{ TRANG_THAI_HOA_DON[hoaDon.trangThai] }}
            </span>
          </div>

          <div class="mt-3 d-flex flex-wrap gap-2">
            <!-- 0: Chờ thanh toán -->
            <template v-if="hoaDon.trangThai == 0">
              <button class="btn btn-success btn-sm" @click="confirmChange(1)">
                ✅ Chờ xác nhận
              </button>
              <button
                class="btn btn-outline-danger btn-sm"
                @click="confirmChange(5)"
              >
                ❌ Hủy
              </button>
            </template>

            <!-- 1: Chờ xác nhận -->
            <template v-else-if="hoaDon.trangThai == 1">
              <button class="btn btn-success btn-sm" @click="confirmChange(2)">
                ✅ Xác nhận
              </button>
              <button
                class="btn btn-outline-secondary btn-sm"
                @click="confirmChange(0)"
              >
                🔙 Quay lại (Chờ thanh toán)
              </button>
              <button
                class="btn btn-outline-danger btn-sm"
                @click="confirmChange(5)"
              >
                ❌ Hủy
              </button>
            </template>

            <!-- 2: Đã xác nhận -->
            <template v-else-if="hoaDon.trangThai == 2">
              <button class="btn btn-primary btn-sm" @click="confirmChange(3)">
                🚚 Đang giao
              </button>
              <button
                class="btn btn-outline-secondary btn-sm"
                @click="confirmChange(1)"
              >
                🔙 Quay lại (Chờ xác nhận)
              </button>
              <button
                class="btn btn-outline-danger btn-sm"
                @click="confirmChange(5)"
              >
                ❌ Hủy
              </button>
            </template>

            <!-- 3: Đang giao -->
            <template v-else-if="hoaDon.trangThai == 3">
              <button class="btn btn-success btn-sm" @click="confirmChange(4)">
                🎉 Hoàn thành
              </button>
              <button
                class="btn btn-outline-secondary btn-sm"
                @click="confirmChange(2)"
              >
                🔙 Quay lại (Đã xác nhận)
              </button>
              <button
                class="btn btn-outline-danger btn-sm"
                @click="confirmChange(5)"
              >
                ❌ Hủy
              </button>
            </template>

            <!-- 4: Hoàn thành -->
            <template v-else-if="hoaDon.trangThai == 4">
              <span class="text-success fw-bold fst-italic"
                >Đơn hàng đã hoàn tất</span
              >
            </template>

            <!-- 5: Đã hủy -->
            <template v-else-if="hoaDon.trangThai == 5">
              <button
                class="btn btn-outline-primary btn-sm"
                @click="confirmChange(1)"
              >
                ↩️ Khôi phục (Chờ xác nhận)
              </button>
            </template>
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
              <th>Mã SP</th>
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
              <td>{{ sp.maSanPham }}</td>
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
        <div class="col-md-6">
            <div class="card p-3 inner-card">
                <p class="mb-1">
                    <i class="fa-solid fa-tag text-primary me-2"></i>
                    <strong>Phiếu giảm giá:</strong>
                    {{ hoaDon.phieuGiamGiaResponse?.ten || "Không áp dụng" }}
                </p>
                <p
                    v-if="hoaDon.phieuGiamGiaResponse?.giaTriGiam"
                    class="mb-0 text-success"
                >
                    <i class="fa-solid fa-arrow-down-long me-2"></i>
                    <strong>Giảm giá:</strong>
                    -{{ hoaDon.phieuGiamGiaResponse.giaTriGiam.toLocaleString() }} ₫
                </p>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card p-3 text-end inner-card">
                <p class="mb-1">
                    <i class="fa-solid fa-cart-shopping me-2"></i>
                    Tổng tiền hàng: {{ hoaDon.tongTien.toLocaleString() }} ₫
                </p>
                <p
                    v-if="hoaDon.phieuGiamGiaResponse?.giaTriGiam"
                    class="mb-0 text-success"
                >
                    <i class="fa-solid fa-arrow-down-long me-2"></i>
                    <strong>Giá giảm:</strong>
                    -{{ hoaDon.phieuGiamGiaResponse.giaTriGiam.toLocaleString() }} ₫
                </p>
                <hr class="my-2" />
                <h5 class="fw-bold text-danger mb-0">
                    <i class="fa-solid fa-credit-card me-2"></i>
                    Tổng tiền thanh toán:
                    {{
                        (
                            hoaDon.tongTienSauGiam ||
                            hoaDon.tongTien ||
                            0
                        ).toLocaleString()
                    }}
                    ₫
                </h5>
            </div>
        </div>
    </div>
</div>

    <!-- Nút hành động -->
    <div class="d-flex justify-content-end gap-2">
      <button
        v-if="hoaDon.trangThai < 2"
        class="btn btn-secondary"
        @click="router.push({ name: 'HoaDon' })"
      >
        Hủy
      </button>
      <button
        v-if="hoaDon.trangThai < 2"
        class="btn btn-warning text-white"
        @click="handleSave"
      >
        💾 Lưu thay đổi
      </button>
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

/* Layout tweaks */
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

/* Wrapper tổng */
.timeline-wrapper {
  position: relative;
  margin-top: 10px;
  padding-bottom: 10px;
}

.progress-line-bg {
  position: absolute;
  top: 20px;
  left: 0;
  width: 100%;
  height: 4px;
  background-color: #e9ecef;
  z-index: 1;
  border-radius: 10px;
}

/* 🔥 Cập nhật: Xóa background-color cứng ở đây vì đã set inline style */
.progress-line-fill {
  position: absolute;
  top: 20px;
  left: 0;
  height: 4px;
  /* background-color: #28a745;  <-- ĐÃ XÓA DÒNG NÀY */
  z-index: 1;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 10px;
}

.timeline-item {
  position: relative;
  z-index: 2;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Vòng tròn Icon Mặc định */
.icon-circle {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background-color: #fff;
  border: 4px solid #e9ecef; /* Mặc định xám */
  color: #adb5bd; /* Mặc định xám */
  font-size: 18px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 🔥 XÓA HẾT CÁC CLASS .active .icon-circle Ở DƯỚI ĐÂY */
/* Vì logic màu sắc đã chuyển sang hàm getCircleStyle trong JS */

/* Chỉ giữ lại hiệu ứng phóng to cho current */
.timeline-item.current .icon-circle {
  transform: scale(1.15);
}

.content-box {
  margin-top: 10px;
  min-height: 45px;
}

.step-text {
  font-size: 0.9rem;
  margin-bottom: 2px;
  font-weight: 600;
  /* color: ... Đã xử lý inline */
  transition: color 0.3s;
}

.step-time {
  font-size: 0.75rem;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .icon-circle {
    width: 35px;
    height: 35px;
    font-size: 14px;
    border-width: 3px;
  }
  .progress-line-bg,
  .progress-line-fill {
    top: 15px;
  }
  .step-text {
    font-size: 0.75rem;
  }
  .step-time {
    font-size: 0.65rem;
  }
}
</style>
