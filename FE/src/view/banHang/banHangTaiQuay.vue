<template>
  <div class="container-fluid mt-4 px-1">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Bán hàng tại quầy</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-8 d-flex flex-column gap-3">
        <!-- card 1 -->
        <div class="card p-3 text-center">
          <!-- Header -->
          <div
            class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3"
          >
            <h5 class="mb-0 card-title">
              <i class="fa-solid fa-list-ul me-1"></i>Hóa đơn chờ
            </h5>
            <button
              class="btn btn-warning text-white btn-sm"
              @click="handleTaoHoaDon"
            >
              <i class="fa-solid fa-plus me-1"></i>Tạo hóa đơn
            </button>
          </div>

          <!-- Nội dung -->
          <div class="border rounded p-3 bg-light-subtle">
            <div
              v-if="hoaDonChoList.length === 0"
              class="text-muted text-center"
            >
              <p class="mb-0">Chưa có hóa đơn chờ nào</p>
            </div>

            <div v-else class="hoa-don-container">
              <div
                v-for="hd in hoaDonChoList"
                :key="hd.id"
                class="card hoa-don-card text-start"
                :class="{
                  'border-warning border-2': hd.id === selectedHoaDonId,
                }"
                @click="selectHoaDon(hd.id)"
              >
                <div class="card-body p-2">
                  <!-- Hàng trên: mã + trạng thái -->
                  <div
                    class="d-flex justify-content-between align-items-center mb-1"
                  >
                    <h6 class="mb-0 fw-bold text-truncate">{{ hd.ma }}</h6>
                    <span
                      class="badge text-uppercase"
                      :class="{
                        'bg-success': hd.trangThai === 5,
                        'bg-danger': hd.trangThai === 0,
                        'bg-secondary':
                          hd.trangThai !== 0 && hd.trangThai !== 5,
                      }"
                    >
                      {{ trangThaiText(hd.trangThai) }}
                    </span>
                  </div>

                  <!-- Hàng dưới: tổng sản phẩm + nút xóa -->
                  <div
                    class="d-flex justify-content-between align-items-center"
                  >
                    <small class="text-muted"
                      >SP: {{ hd.tongSoLuong || 0 }}</small
                    >
                    <button
                      class="btn btn-sm btn-outline-danger py-0 px-1"
                      title="Hủy hóa đơn"
                      @click.stop="handleHuyHoaDon(hd.id)"
                    >
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- card 2 -->
        <div class="card p-3 text-center">
          <h5 class="mb-3 text-start">
            <i class="fas fa-shopping-cart me-2 text-warning"></i>
            Giỏ hàng
          </h5>
          <div
            class="border rounded p-2 bg-light-subtle"
            style="min-height: 120px"
          >
            <template v-if="gioHang.length === 0">
              <p class="text-muted text-center mb-0">Giỏ hàng trống</p>
            </template>
            <template v-else>
              <ul class="list-group list-group-flush">
                <li
                  class="list-group-item d-flex justify-content-between align-items-center py-2"
                  v-for="sp in gioHang"
                  :key="sp.id"
                >
                  <div class="text-start me-2 flex-grow-1">
                    <p
                      class="fw-bold mb-0 text-truncate"
                      style="max-width: 200px"
                    >
                      {{ sp.tenSanPham }}
                    </p>
                    <small class="text-muted">
                      Màu: {{ sp.mauSac || "N/A" }} | Size:
                      {{ sp.kichThuoc || "N/A" }}
                    </small>
                    <p class="mb-0 mt-1">
                      SL:
                      <span class="fw-semibold text-primary">{{
                        sp.soLuong
                      }}</span>
                    </p>
                  </div>

                  <div
                    class="d-flex flex-column align-items-end justify-content-center"
                  >
                    <span class="fw-bold text-success mb-1">
                      {{ formatCurrency(sp.donGia * sp.soLuong) }}
                    </span>
                    <button
                      class="btn btn-sm btn-outline-danger py-0 px-1"
                      @click.stop="handleXoaSanPham(sp.id)"
                    >
                      <i class="fa-solid fa-trash"></i>
                    </button>
                  </div>
                </li>
              </ul>
            </template>
          </div>
        </div>

        <!-- card 3 -->
        <div class="card p-3 text-center">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="mb-0 text-start">
              <i class="fa-solid fa-table me-2 text-warning"></i>
              Danh sách sản phẩm
            </h5>

            <!-- Ô tìm kiếm sản phẩm -->
            <div class="d-flex align-items-center" style="gap: 8px">
              <input
                v-model="searchSanPham"
                @input="filterSanPham"
                type="text"
                class="form-control form-control-sm search-input"
                placeholder="Tìm kiếm sản phẩm..."
                style="width: 220px"
              />
            </div>
          </div>

          <!-- Bảng sản phẩm -->
          <div
            class="table-responsive"
            style="max-height: 400px; overflow-y: auto"
          >
            <table class="table table-hover table-bordered align-middle">
              <thead class="table-warning text-center">
                <tr>
                  <th scope="col" style="width: 5%">#</th>
                  <th scope="col">Tên sản phẩm</th>
                  <th scope="col">Màu</th>
                  <th scope="col">Kích thước</th>
                  <th scope="col">Số lượng</th>
                  <th scope="col">Đơn giá (VND)</th>
                  <th scope="col">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredSanPham.length === 0">
                  <td colspan="4" class="text-center text-muted">
                    Không có sản phẩm
                  </td>
                </tr>
                <tr v-for="(sp, index) in filteredSanPham" :key="sp.id">
                  <td class="text-center">{{ index + 1 }}</td>
                  <td class="text-start">
                    {{ sp.tenSanPham }}
                  </td>
                  <td class="text-start">
                    {{ sp.mauSac }}
                  </td>
                  <td class="text-start">
                    {{ sp.kichThuoc }}
                  </td>
                  <td class="text-start">
                    {{ sp.soLuongTon }}
                  </td>
                  <td class="fw-bold">{{ sp.giaBan?.toLocaleString() }} VND</td>
                  <td class="text-center">
                    <button
                      class="btn btn-sm btn-warning text-white"
                      @click="handleThemSanPham(sp)"
                    >
                      <i class="fa-solid fa-cart-plus me-1"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="col-md-4 d-flex flex-column gap-3">
        <!-- Nếu chưa chọn hóa đơn -->
        <div v-if="!hoaDon" class="card p-3 text-center">
          <h5 class="text-muted mb-0">Chưa có hóa đơn được chọn</h5>
        </div>

        <!-- Nếu đã có hóa đơn -->
        <template v-else>
          <!-- card 4 -->
          <div v-if="hoaDon && hoaDon.khachHang" class="card p-3 text-center">
            <h5 class="mb-3 text-start">
              <i class="fas fa-user me-2 text-warning"></i>Khách hàng
            </h5>

            <div class="input-group mb-3">
              <input
                type="text"
                class="form-control"
                v-model="searchKeyword"
                placeholder="Tìm bằng khách hàng, sđt"
                @keyup.enter="handleTimKhachHang"
              />
              <button
                class="btn btn-warning text-white"
                @click="handleTimKhachHang"
                :disabled="!searchKeyword"
              >
                <i class="fa-solid fa-magnifying-glass"></i>
              </button>
            </div>

            <div class="mb-2">
              <input
                type="text"
                class="form-control mb-2"
                v-model="hoaDon.khachHang.hoTen"
                placeholder="Tên khách hàng"
                :disabled="!isGuestEditable"
                @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
              />
              <input
                type="text"
                class="form-control"
                v-model="hoaDon.khachHang.sdt"
                placeholder="Số điện thoại"
                :disabled="!isGuestEditable"
                @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
              />
            </div>

            <div class="d-grid gap-2">
              <button
                class="btn btn-outline-success btn-sm"
                @click="handleThemNhanhKhachHang"
              >
                <i class="fa-solid fa-user-plus me-1"></i>Thêm Khách hàng mới
              </button>
            </div>
          </div>

          <!-- card 5 -->
          <div class="card p-3 text-center flex-grow-1 big-card">
            <h5 class="mb-3 text-start">
              <i class="fa-solid fa-receipt me-2 text-warning"></i>Thông tin đơn
            </h5>

            <!-- Tổng tiền reactive -->
            <div class="border rounded p-2 bg-light-subtle mb-3">
              <div class="d-flex justify-content-between">
                <span>Tổng tiền:</span>
                <span class="fw-bold"
                  >{{ (tongTien ?? 0).toLocaleString() }} VND</span
                >
              </div>
            </div>

            <!-- Phương thức thanh toán -->
            <div class="mb-3 text-start">
              <h6 class="fw-bold">Phương thức thanh toán</h6>
              <div class="btn-group w-100" role="group">
                <button class="btn btn-outline-secondary">Chuyển khoản</button>
                <button class="btn btn-warning text-white">Tiền mặt</button>
                <button class="btn btn-outline-secondary">Cả hai</button>
              </div>
            </div>

            <!-- Nút thanh toán -->
            <button
              class="btn btn-warning w-100 fw-bold"
              @click="handleThanhToan"
              :disabled="!hoaDon"
            >
              Thanh toán
            </button>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import {
  taoHoaDon,
  huyHoaDon as apiHuyHoaDon,
  themSanPhamVaoHoaDon,
  xoaSanPhamKhoiHoaDon,
  capNhatKhachHang,
  apDungGiamGia,
  thanhToan,
  getChiTietHoaDon,
  getDanhSachSanPham,
  timKhachHangBySdt,
  themKhachHangMoi,
  timKhachHangDaDangKy,
} from "@/service/BanHangService";
import Swal from "sweetalert2";
import { useNotify } from "@/composables/useNotify";
import KhachHang from "../khachHang/khachHang.vue";

const notify = useNotify();
const idNhanVien = "02b6c170-6aa5-4cc7-8e52-abc123456789";

// state
const hoaDonChoList = ref([]);
const selectedHoaDonId = ref(null);
const hoaDon = ref(null); // hóa đơn đang chọn
const gioHang = ref([]);
const searchSanPham = ref("");
const filteredSanPham = ref([]);

// ... (các ref khác)

const searchKeyword = ref("");
const isGuestEditable = ref(false);
const showAddGuestButton = ref(false);

const danhSachSanPham = ref([]);

// Ví dụ hàm định dạng tiền tệ đơn giản trong <script setup>
const formatCurrency = (amount) => {
    if (amount === null || amount === undefined) return '0 ₫';
    return amount.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
};

const tongTien = computed(() => {
  if (!gioHang.value || gioHang.value.length === 0) {
    return 0;
  }
  // Đảm bảo các thuộc tính (thanhTien) là số trước khi tính tổng
  return gioHang.value.reduce((sum, p) => sum + (p.thanhTien || 0), 0);
});

// trạng thái
const trangThaiText = (value) => {
  const map = {
    0: "Đã hủy",
    1: "Chờ xác nhận",
    2: "Đã xác nhận",
    3: "Đang giao",
    4: "Hoàn thành",
    5: "Chờ thanh toán",
  };
  return map[value] || "Không xác định";
};

const filterSanPham = () => {
  if (!Array.isArray(danhSachSanPham.value)) {
    filteredSanPham.value = [];
    return;
  }

  const keyword = searchSanPham.value.trim().toLowerCase();
  if (!keyword) {
    filteredSanPham.value = danhSachSanPham.value;
    return;
  }

  filteredSanPham.value = danhSachSanPham.value.filter((sp) => {
    const ten = sp.tenSanPham?.toLowerCase() || sp.ten?.toLowerCase() || "";
    const ma = sp.ma?.toLowerCase() || "";
    const mau = sp.mauSac?.toLowerCase() || "";
    const kt = sp.kichThuoc?.toLowerCase() || "";

    return (
      ten.includes(keyword) ||
      ma.includes(keyword) ||
      mau.includes(keyword) ||
      kt.includes(keyword)
    );
  });
};

// tạo hóa đơn
async function handleTaoHoaDon() {
  if (hoaDonChoList.value.length >= 5) {
    Swal.fire("Giới hạn 5 hóa đơn chờ!", "", "warning");
    return;
  }
  try {
    const res = await taoHoaDon(idNhanVien);
    const newHoaDon = {
      ...res.data,
      tongSoLuong: 0,
      sanPhamList: [],
      khachHang: res.data.khachHang || { ten: "", sdt: "" },
    };
    hoaDonChoList.value.push(newHoaDon);
    selectedHoaDonId.value = newHoaDon.id;
    hoaDon.value = newHoaDon;
    gioHang.value = [];
    tongTien.value = 0;
    notify.success("Tạo hóa đơn mới thành công!");
  } catch (err) {
    console.error(err);
    notify.error("Tạo hóa đơn thất bại!");
  }
}

// chọn hóa đơn
const selectHoaDon = (id) => {
  selectedHoaDonId.value = id;
  hoaDon.value = hoaDonChoList.value.find((h) => h.id === id) || null;
  gioHang.value = hoaDon.value?.sanPhamList || [];
  tongTien.value = gioHang.value.reduce((sum, sp) => sum + sp.thanhTien, 0);

  // LOGIC MỚI: Kiểm tra Khách hàng mặc định
  isGuestEditable.value =
    hoaDon.value &&
    hoaDon.value.khachHang &&
    hoaDon.value.khachHang.ma !== "KHACHLE"; // Giả sử mã KHÁCH LẺ là "KHACHLE"
  searchKeyword.value = hoaDon.value?.khachHang?.sdt || ""; // Set SĐT hiện tại vào ô tìm kiếm
  showAddGuestButton.value = false; // Mặc định ẩn
};

// Giả sử API service mới
// import { timKhachHangBySdt, themKhachHangMoi } from "@/service/KhachHangService";

// Hàm TÌM KIẾM KHÁCH HÀNG
const handleTimKhachHang = async () => {
  const keywword = searchKeyword.value.trim();
  if (!keywword) return notify.warning("Vui lòng nhập Tên/SĐT để tìm kiếm!");

  try {
    const res = await timKhachHangDaDangKy(keywword); // Gọi API tìm kiếm

    if (res.data) {
      // ✅ TÌM THẤY KHÁCH HÀNG
      const foundKhachHang = res.data;

      // Gán Khách hàng mới vào hóa đơn
      await capNhatKhachHang(hoaDon.value.id, foundKhachHang.id);

      hoaDon.value.khachHang = foundKhachHang;
      isGuestEditable.value = true;
      showAddGuestButton.value = false;
      notify.success("Đã tìm thấy và cập nhật Khách hàng!");
    } else {
      // ⚠️ KHÔNG TÌM THẤY
      showAddGuestButton.value = true;
      notify.warning("Không tìm thấy Khách hàng. Bạn có thể thêm mới nhanh.");
    }
  } catch (err) {
    console.error("Lỗi tìm kiếm KH:", err);
    notify.error("Lỗi khi tìm kiếm Khách hàng!");
  }
};

// Hàm THÊM NHANH KHÁCH HÀNG MỚI
const handleThemNhanhKhachHang = async () => {
  const { value: newSdt } = await Swal.fire({
    title: "SĐT Khách hàng mới",
    input: "text",
    inputLabel: "Nhập Số điện thoại Khách hàng (bắt buộc)",
    inputPlaceholder: "Ví dụ: 0987654321",
    showCancelButton: true,
    inputValidator: (value) => {
      if (!value || value.trim().length < 8) {
        // Có thể thêm validation SĐT
        return "SĐT không hợp lệ!";
      }
    },
  });
  if (!newSdt) return;
  const { value: hoTen } = await Swal.fire({
    title: "Tên Khách hàng mới",
    input: "text",
    inputLabel: `Nhập Tên Khách hàng (SĐT: ${newSdt})`,
    inputValue: "",
    showCancelButton: true,
    inputValidator: (value) => {
      if (!value) {
        return "Bạn cần nhập tên!";
      }
    },
  });
  if (hoTen) {
    try {
      // Chuẩn bị dữ liệu gửi lên (Sử dụng hoTen thay vì ten)
      const newKhachHangData = { hoTen, sdt: newSdt, trangThai: 1 };

      const res = await themKhachHangMoi(newKhachHangData); // Gọi API thêm mới

      const newKhachHang = res.data;

      // 3. Gán Khách hàng mới vào hóa đơn (CHỈ TRUYỀN ID)
      await capNhatKhachHang(hoaDon.value.id, newKhachHang.id);

      // 4. Bind dữ liệu về 2 ô input (Đã đúng)
      hoaDon.value.khachHang = newKhachHang;

      // Xóa nội dung ô tìm kiếm sau khi thêm thành công (tùy chọn)
      searchKeyword.value = "";
      isGuestEditable.value = true;

      notify.success("Thêm mới và gán Khách hàng thành công!");
    } catch (err) {
      console.error("Lỗi thêm nhanh KH:", err);
      const errorMessage =
        err.response?.data || "Không thể thêm Khách hàng mới!";
      notify.error(errorMessage);
    }
  }
};

// hủy hóa đơn
const handleHuyHoaDon = async (id) => {
  const confirm = await Swal.fire({
    title: "Hủy hóa đơn này?",
    text: "Sau khi hủy, hóa đơn sẽ không còn trong danh sách chờ.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Hủy hóa đơn",
    cancelButtonText: "Đóng",
  });
  if (!confirm.isConfirmed) return;

  try {
    await apiHuyHoaDon(id);
    hoaDonChoList.value = hoaDonChoList.value.filter((hd) => hd.id !== id);
    if (selectedHoaDonId.value === id) {
      selectedHoaDonId.value = null;
      hoaDon.value = null;
      gioHang.value = [];
      tongTien.value = 0;
    }
    notify.success("Hóa đơn đã được hủy thành công!");
  } catch (err) {
    console.error(err);
    notify.error("Không thể hủy hóa đơn!");
  }
};

// thêm sản phẩm vào hóa đơn
const handleThemSanPham = async (sp) => {
  if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");
  try {
    const res = await themSanPhamVaoHoaDon(hoaDon.value.id, sp.id, 1);
    gioHang.value.push(res.data);
    hoaDon.value.sanPhamList = gioHang.value;
    notify.success("Đã thêm sản phẩm!");
  } catch (err) {
    notify.error("Không thể thêm sản phẩm!");
  }
};

// xóa sản phẩm khỏi hóa đơn
const handleXoaSanPham = async (idSp) => {
  if (!hoaDon.value) return;

  console.log("🧩 Gửi xóa sản phẩm:", {
    idHoaDon: hoaDon.value.id,
    idChiTietSanPham: idSp,
  });

  try {
    await xoaSanPhamKhoiHoaDon(hoaDon.value.id, idSp);

    // Cập nhật lại giỏ hàng hiển thị
    gioHang.value = gioHang.value.filter((item) => item.id !== idSp);
    hoaDon.value.sanPhamList = gioHang.value;

    notify.success("Đã xóa sản phẩm!");
  } catch (err) {
    console.error("❌ Xóa thất bại:", err);
    notify.error("Không thể xóa sản phẩm!");
  }
};

// update khách hàng
const handleCapNhatKhachHang = async (khachHang) => {
  if (!hoaDon.value) return;
  try {
    await capNhatKhachHang(hoaDon.value.id, khachHang);
    hoaDon.value.khachHang = khachHang;
    notify.success("Cập nhật khách hàng thành công!");
  } catch (err) {
    notify.error("Không thể cập nhật khách hàng!");
  }
};

// thanh toán
const handleThanhToan = async () => {
  if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
  try {
    await thanhToan(hoaDon.value.id, idNhanVien);
    notify.success("Thanh toán thành công!");
    hoaDonChoList.value = hoaDonChoList.value.filter(
      (hd) => hd.id !== hoaDon.value.id
    );
    hoaDon.value = null;
    gioHang.value = [];
    tongTien.value = 0;
    selectedHoaDonId.value = null;
  } catch (err) {
    notify.error("Không thể thanh toán!");
  }
};

// load danh sách sản phẩm (fake data hoặc từ API)
const loadSanPham = async () => {
  try {
    const res = await getDanhSachSanPham();
    console.log(
      "👉 Dữ liệu sản phẩm chi tiết:",
      JSON.parse(JSON.stringify(filteredSanPham.value))
    );
    danhSachSanPham.value = res.data || [];
    filteredSanPham.value = danhSachSanPham.value;
  } catch (err) {
    console.error("Lỗi khi load sản phẩm:", err);
    danhSachSanPham.value = [];
    filteredSanPham.value = [];
  }
};

loadSanPham();
</script>

<style scoped>
.card {
  background-color: #f8f9fa;
  border: 1px solid #ddd;
}
.search-input::placeholder {
  color: #999;
  opacity: 0.8;
  font-style: italic;
}
.empty-icon {
  width: 60px;
  height: 60px;
  background-color: #ffc107; /* màu xanh dịu */
}
/* Card to (cao bằng 2 card nhỏ bên trái) */
.big-card {
  height: calc(
    (100% - 1rem) * 2 / 3
  ); /* Tự động tính cao bằng 2/3 của cột trái */
}

.card:hover {
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  transition: all 0.2s ease;
}

/* Nếu muốn cố định chiều cao cho các card nhỏ để dễ nhìn */
.col-md-6 .card {
  height: auto;
}
.qr-btn {
  height: 100%; /* Cùng chiều cao với ô input */
  white-space: nowrap; /* Không xuống dòng */
  font-size: 0.9rem; /* Nhỏ hơn một chút cho gọn */
  padding: 0 10px; /* Giảm padding ngang */
}
.nav-tabs .nav-link.active {
  background-color: #ffc107;
  color: white;
}
.nav-tabs .nav-link {
  border-radius: 6px 6px 0 0;
}
.hoa-don-container {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: thin;
}

.hoa-don-container::-webkit-scrollbar {
  height: 6px;
}

.hoa-don-container::-webkit-scrollbar-thumb {
  background: #bbb;
  border-radius: 3px;
}

.hoa-don-card {
  flex: 0 0 180px; /* cố định kích thước mỗi card */
  min-height: 90px;
  background: #fff;
  border: 1px solid #ddd;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hoa-don-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.hoa-don-card .badge {
  font-size: 0.7rem;
  padding: 4px 6px;
  letter-spacing: 0.3px;
}
</style>
