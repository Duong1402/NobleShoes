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

const LOAI_HOA_DON = ["Online", "Tại cửa hàng", "ONLINE"];

const TRANG_THAI_HOA_DON = {
  0: "Chờ thanh toán",
  1: "Chờ xác nhận",
  2: "Đã xác nhận",
  3: "Đang chuẩn bị",
  4: "Đang giao",
  5: "Giao hàng thất bại",
  6: "Hoàn thành",
  7: "Đã hủy",
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

  if (currentStatus === 7) {
    lichSuHienThi.value = [
      {
        id: 7,
        text: TRANG_THAI_HOA_DON[7],
        thoiGian:
          hoaDon.value.thoiGianHuy ||
          hoaDon.value.ngayCapNhat ||
          hoaDon.value.ngayTao,
        isCanceled: true,
        isDone: true,
        isCurrent: true,
      },
    ];
    return;
  }

  let allowedSteps = [];
  switch ((currentType || "").toLowerCase()) {
    case "tại cửa hàng":
      allowedSteps = [0, 6];
      break;

    case "online":
      allowedSteps = [1, 2, 3, 4, 5, 6];
      break;

    default:
      allowedSteps = [currentStatus];
  }

  const findTimeInHistory = (status) => {
    const item = lichSuThayDoi.value.find(
      (h) => Number(h.trangThaiMoi) === Number(status)
    );
    let time =
      item?.thoiGianCapNhat ||
      item?.thoiGian ||
      item?.ngayTao ||
      item?.createDate ||
      item?.createdAt;

    if (!time && status === currentStatus) {
      time = hoaDon.value.ngayCapNhat || hoaDon.value.ngayTao;
    }
    return time || null;
  };

  // --- 3. Lọc step hiển thị ---
  let visibleSteps = allowedSteps.filter((step) => step <= currentStatus);

  // Nếu hoàn thành (6) → bỏ step 5 nếu chưa có trong lịch sử
  if (currentStatus === 6) {
    const step5Happen = lichSuThayDoi.value.some(
      (h) => Number(h.trangThaiMoi) === 5
    );
    if (!step5Happen) {
      visibleSteps = visibleSteps.filter((step) => step !== 5);
    }
  }

  const anchorTime =
    findTimeInHistory(currentStatus) ||
    hoaDon.value.ngayCapNhat ||
    new Date().toISOString();

  // --- 4. Map các step ---
  lichSuHienThi.value = visibleSteps.map((step) => {
    let thoiGian = findTimeInHistory(step);
    if (!thoiGian) {
      thoiGian = step === 0 ? hoaDon.value.ngayTao : anchorTime;
    }
    return {
      id: step,
      text: TRANG_THAI_HOA_DON[step],
      thoiGian,
      isDone: true,
      isCurrent: step === currentStatus,
      // chỉ đánh dấu canceled nếu currentStatus là 5
      isCanceled: step === 5 && currentStatus === 5,
    };
  });
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
        tenKhachHang: hoaDon.value.tenKhachHang,
      });
      notify.success("Cập nhật thành công!");
      router.push({ name: "ChiTietHD" });
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
  let targetStatus = newStatus;

  if (newStatus === 7) {
    const result = await Swal.fire({
      title: "Xác nhận hủy đơn hàng?",
      input: "text",
      inputPlaceholder: "Nhập lý do hủy ? *",
      showCancelButton: true,
      confirmButtonText: "Xác nhận hủy",
      cancelButtonText: "Đóng",
      icon: "warning",
      inputAttributes: { maxlength: 255 },
      preConfirm: (note) => {
        const trimmedNote = note?.trim();
        if (!trimmedNote || trimmedNote.length === 0) {
          return "Lý do hủy không được để trống! Vui lòng nhập chi tiết.";
        }
        return trimmedNote;
      },
    });
    if (result.isConfirmed) {
      isConfirmed = true;
      cancelReason = result.value;
      targetStatus = 7;
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
      const now = new Date().toISOString();
      const userInfor = JSON.parse(localStorage.getItem("userData")) || {};
      const realName = userInfor.hoTen || "Quản lý";

      hoaDon.value.trangThai = targetStatus;
      hoaDon.value.ngayCapNhat = now;
      if (targetStatus === 7) hoaDon.value.thoiGianHuy = now;

      lichSuThayDoi.value.push({
        trangThaiMoi: targetStatus,
        thoiGian: now,
        thoiGianCapNhat: now,
        nguoiChinhSua: realName,
        ghiChu: cancelReason,
      });

      renderTimeline();

      await updateHoaDon(hoaDon.value.id, {
        trangThai: targetStatus,
        ghiChu: cancelReason,
      });
    } catch (err) {
      console.error("Lỗi cập nhật:", err);
      notify.error("Có lỗi xảy ra, vui lòng thử lại!");
    }
  }
};

const getActionButtons = (status) => {
  const buttons = [];

  const createButton = (label, target, className) => ({
    label,
    target,
    class: className,
  });

  switch (status) {
    case 0: // Chờ thanh toán
      buttons.push(
        createButton("✅ Chờ xác nhận", 1, "btn btn-success btn-sm")
      );
      buttons.push(createButton("❌ Hủy", 7, "btn btn-outline-danger btn-sm"));
      break;

    case 1: // Chờ xác nhận
      buttons.push(createButton("✅ Xác nhận", 2, "btn btn-success btn-sm"));
      buttons.push(
        createButton("🔙 Quay lại", 0, "btn btn-outline-secondary btn-sm")
      );
      buttons.push(createButton("❌ Hủy", 7, "btn btn-outline-danger btn-sm"));
      break;

    case 2: // Đã xác nhận
      buttons.push(
        createButton("🚚 Đang chuẩn bị", 3, "btn btn-primary btn-sm")
      );
      buttons.push(
        createButton("🔙 Quay lại", 1, "btn btn-outline-secondary btn-sm")
      );
      buttons.push(createButton("❌ Hủy", 7, "btn btn-outline-danger btn-sm"));
      break;

    case 3: // Đang chuẩn bị
      buttons.push(createButton("🚚 Đang giao", 4, "btn btn-primary btn-sm"));
      buttons.push(
        createButton("🔙 Quay lại", 2, "btn btn-outline-secondary btn-sm")
      );
      buttons.push(createButton("❌ Hủy", 7, "btn btn-outline-danger btn-sm"));
      break;

    case 4: // Đang giao
      buttons.push(createButton("🎉 Hoàn thành", 6, "btn btn-success btn-sm"));
      buttons.push(
        createButton(
          "❌ Giao hàng thất bại",
          5,
          "btn btn-outline-danger btn-sm"
        )
      );
      // buttons.push(
      //   createButton("🔙 Quay lại", 3, "btn btn-outline-secondary btn-sm")
      // );
      break;

    case 5: // Giao hàng thất bại
    case 7: // Đã hủy
      buttons.push(
        createButton(
          "↩️ Khôi phục (Chờ xác nhận)",
          1,
          "btn btn-outline-primary btn-sm"
        )
      );
      break;

    case 6: // Hoàn thành
      // Không có nút gì, chỉ hiển thị thông báo
      break;

    default:
      break;
  }

  return buttons;
};

const getStepIcon = (stepId) => {
  switch (stepId) {
    case 0: // Chờ thanh toán
      return "fa-file-invoice-dollar";
    case 1: // Chờ xác nhận
      return "fa-clipboard-check";
    case 2: // Đã xác nhận
      return "fa-box-open";
    case 3: // Đang chuẩn bị
      return "fa-boxes-packing";
    case 4: // Đang giao
      return "fa-truck-fast";
    case 5: // Giao hàng thất bại / Đã hủy
      return "fa-ban";
    case 6: // Hoàn thành
      return "fa-check-circle";
    case 7: // Đã hủy
      return "fa-times-circle";
    default:
      return "fa-circle";
  }
};

const getStepColor = (stepId) => {
  switch (stepId) {
    case 0:
      return "#ffc107"; // warning
    case 1:
      return "#6c757d"; // secondary
    case 2:
      return "#0dcaf0"; // info
    case 3:
      return "#6f42c1"; // purple (chuẩn bị)
    case 4:
      return "#0d6efd"; // primary
    case 5:
      return "#dc3545"; // danger
    case 6:
      return "#198754"; // success
    case 7:
      return "#343a40"; // dark
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

const isConfirmedOrBeyond = computed(() => {
  const currentStatus = Number(hoaDon.value?.trangThai ?? 0);
  return currentStatus >= 2 && currentStatus !== 5;
});
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
      <h4 class="fw-bold">
        <i class="fa-solid fa-circle-info me-2 text-primary"></i>
        Thông tin chung
      </h4>
      <div class="row g-3">
        <div class="col-md-6">
          <label>Tên khách hàng</label>
          <input
            class="form-control"
            v-model="hoaDon.tenKhachHang"
            :disabled="isConfirmedOrBeyond"
          />
        </div>
        <div class="col-md-6">
          <label>Tên nhân viên</label>
          <input class="form-control" v-model="hoaDon.tenNhanVien" disabled />
        </div>

        <div class="col-md-6">
          <label>Số điện thoại</label>
          <input
            class="form-control"
            v-model="hoaDon.sdt"
            :disabled="isConfirmedOrBeyond"
          />
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
                'bg-purple': hoaDon.trangThai == 3,
                'bg-primary': hoaDon.trangThai == 4,
                'bg-danger': hoaDon.trangThai == 5,
                'bg-success': hoaDon.trangThai == 6,
                'bg-dark': hoaDon.trangThai == 7,
              }"
            >
              {{ TRANG_THAI_HOA_DON[hoaDon.trangThai] }}
            </span>
          </div>

          <div class="mt-3 d-flex flex-wrap gap-2">
            <button
              v-for="(btn, i) in getActionButtons(hoaDon.trangThai)"
              :key="i"
              :class="btn.class"
              @click="confirmChange(btn.target)"
            >
              {{ btn.label }}
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
    <div class="card shadow-sm mb-4 p-4">
      <h4 class="fw-bold mb-3">
        <i class="fa-solid fa-clipboard-list me-2 text-warning"></i>
        Danh sách sản phẩm
      </h4>
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
              <td>
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
          <div class="card p-3 inner-card">
            <p class="mb-1 d-flex justify-content-between">
              <span class="px-4">
                <i class="fa-solid fa-cart-shopping me-2"></i>
                Tổng tiền hàng:
              </span>
              <span class="px-4">
                {{ hoaDon.tongTien.toLocaleString() }} ₫
              </span>
            </p>

            <p
              class="mb-1 d-flex justify-content-between"
              v-if="
                hoaDon.loaiHoaDon &&
                hoaDon.loaiHoaDon.toLowerCase() === 'online'
              "
            >
              <span class="px-4">
                <i class="fa-solid fa-truck-fast me-2"></i>
                Phí vận chuyển:
              </span>
              <span class="px-4">
                {{
                  hoaDon.phiVanChuyen
                    ? hoaDon.phiVanChuyen.toLocaleString() + " ₫"
                    : "(0 ₫)"
                }}
              </span>
            </p>

            <hr class="my-2" />

            <h5 class="fw-bold text-danger mb-0 d-flex justify-content-between">
              <span class="px-4">
                <i class="fa-solid fa-credit-card me-2"></i>
                Tổng tiền thanh toán:
              </span>
              <span class="px-4">
                {{
                  (
                    hoaDon.tongTienSauGiam ||
                    hoaDon.tongTien ||
                    0
                  ).toLocaleString()
                }}
                ₫
              </span>
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

/* Màu tím của trạng thái hóa đơn */
.bg-purple {
  background-color: #6f42c1 !important;
  color: #fff !important;
}
</style>
