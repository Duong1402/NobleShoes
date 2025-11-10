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
              <i class="fa-solid fa-list-ul me-1 text-warning"></i>Hóa đơn chờ
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
                    <small class="text-muted">SP: {{ hd.soLuong || 0 }}</small>
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
            style="min-height: 120px; max-height: 400px; overflow-y: auto"
          >
            <template v-if="gioHang.length === 0">
              <div
                class="d-flex flex-column align-items-center justify-content-center py-4"
              >
                <div
                  class="bg-warning text-white rounded-circle d-flex align-items-center justify-content-center mb-3"
                  style="width: 50px; height: 50px; font-size: 1.5rem"
                >
                  <i class="fa-solid fa-cart-plus"></i>
                </div>

                <p class="text-muted text-center mb-0">Giỏ hàng trống</p>
              </div>
            </template>
            <template v-else>
              <ul class="list-group list-group-flush">
                <li
                  class="list-group-item d-flex align-items-start py-2 px-1 border-0 mb-2 rounded shadow-sm bg-white"
                  v-for="(sp, index) in gioHang"
                  :key="sp.id"
                >
                  <div
                    class="me-2 pt-2 fw-semibold text-muted"
                    style="width: 5%"
                  >
                    {{ index + 1 }}
                  </div>

                  <div class="me-3" style="width: 30%">
                    <img
                      :src="sp.hinhAnhUrl"
                      :alt="sp.tenSanPham"
                      class="cart-thumb"
                    />
                  </div>

                  <div
                    class="text-start flex-grow-1 me-2 pt-1"
                    style="width: 45%"
                  >
                    <p
                      class="h4 fw-bold mb-1 text-truncate"
                      style="max-width: 100%"
                    >
                      {{ sp.tenSanPham }}
                    </p>
                    <div class="text-muted mb-1">
                      <span class="badge bg-primary me-2">{{
                        sp.mauSac || "N/A"
                      }}</span>
                      <span class="badge bg-primary me-2">{{
                        sp.tenXuatXu || "N/A"
                      }}</span>
                      <span class="badge bg-primary">{{
                        sp.kichThuoc || "N/A"
                      }}</span>
                    </div>
                    <p class="mb-0 mt-1 small">
                      Số lượng:
                      <span class="fw-semibold text-dark fs-5">{{
                        sp.soLuong
                      }}</span>
                    </p>
                  </div>

                  <div
                    class="d-flex flex-column align-items-end justify-content-start pt-4"
                    style="width: 20%"
                  >
                    <small class="text-muted mb-1">
                      Đơn giá: {{ formatCurrency(sp.donGia) }}
                    </small>

                    <span class="h5 fw-bold text-warning mb-2">
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
          <div class="d-flex justify-content-between align-items-center mb-2">
            <h5 class="mb-0 text-start">
              <i class="fa-solid fa-table-list me-2 text-warning"></i>
              Danh sách sản phẩm
            </h5>
            <div></div>
          </div>

          <div
            class="d-flex align-items-center mb-3 flex-wrap"
            style="gap: 4px; justify-content: space-between"
          >
            <input
              v-model="searchSanPham"
              @input="filterSanPham"
              type="text"
              class="form-control rounded-pill border-warning shadow-sm"
              placeholder="Tìm kiếm sản phẩm..."
              style="flex-grow: 1; flex-basis: 120px"
            />

            <select
              v-model="filterMauSac"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Màu sắc</option>
              <option v-for="mau in listMauSac" :key="mau" :value="mau">
                {{ mau }}
              </option>
            </select>

            <select
              v-model="filterKichThuoc"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Kích thước</option>
              <option v-for="kt in listKichThuoc" :key="kt" :value="kt">
                {{ kt }}
              </option>
            </select>

            <select
              v-model="filterXuatXu"
              @change="filterSanPham"
              class="form-select rounded-3 border-warning"
              style="flex-grow: 1; flex-basis: 120px"
            >
              <option value="" selected>Tất cả Xuất xứ</option>
              <option v-for="xx in listXuatXu" :key="xx" :value="xx">
                {{ xx }}
              </option>
            </select>
          </div>

          <div
            class="table-responsive"
            style="max-height: 570px; overflow-y: auto"
          >
            <table class="table table-hover table-bordered align-middle">
              <thead class="table-warning text-center">
                <tr>
                  <th scope="col">#</th>
                  <th scope="col" style="width: 10%">Ảnh</th>
                  <th scope="col">Tên sản phẩm</th>
                  <th scope="col">Mã</th>
                  <th scope="col">Màu</th>
                  <th scope="col">Kích thước</th>
                  <th scope="col">Xuất xứ</th>
                  <th scope="col">Số lượng</th>
                  <th scope="col">Giá</th>
                  <th scope="col">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredSanPham.length === 0">
                  <td colspan="10" class="text-center text-muted py-3">
                    <div
                      class="d-flex flex-column align-items-center justify-content-center"
                    >
                      <div
                        class="bg-warning text-white rounded-circle d-flex align-items-center justify-content-center mb-3"
                        style="width: 40px; height: 40px; font-size: 1.2rem"
                      >
                        <i class="fa-solid fa-exclamation"></i>
                      </div>
                      <span class="fw-semibold">
                        Không có sản phẩm nào được tìm thấy.
                      </span>
                    </div>
                  </td>
                </tr>
                <tr v-for="(sp, index) in paginatedSanPham" :key="sp.id">
                  <td class="text-center">
                    {{ (currentPage - 1) * itemsPerPage + index + 1 }}
                  </td>
                  <td class="text-center">
                    <img
                      :src="sp.hinhAnhUrl"
                      :alt="'Ảnh ' + sp.tenSanPham"
                      class="product-thumb"
                    />
                  </td>
                  <td class="text-start">
                    {{ sp.tenSanPham }}
                  </td>
                  <td class="text-start">
                    {{ sp.ma }}
                  </td>
                  <td class="text-start">
                    {{ sp.mauSac }}
                  </td>
                  <td class="text-start">
                    {{ sp.kichThuoc }}
                  </td>
                  <td class="text-start">
                    {{ sp.tenXuatXu }}
                  </td>
                  <td class="text-start">
                    {{ sp.soLuongTon }}
                  </td>
                  <td class="fw-bold text-warning">
                    {{ sp.giaBan?.toLocaleString() }} VND
                  </td>
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
          <div v-if="totalPages > 1" class="d-flex justify-content-end mt-3">
            <nav aria-label="Phân trang sản phẩm">
              <ul class="pagination pagination-sm mb-0">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage - 1)"
                    >Trước</a
                  >
                </li>

                <li
                  class="page-item"
                  v-for="page in totalPages"
                  :key="page"
                  :class="{ active: page === currentPage }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(page)"
                    >{{ page }}</a
                  >
                </li>

                <li
                  class="page-item"
                  :class="{ disabled: currentPage === totalPages }"
                >
                  <a
                    class="page-link"
                    href="#"
                    @click.prevent="goToPage(currentPage + 1)"
                    >Sau</a
                  >
                </li>
              </ul>
            </nav>
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

            <div class="position-relative">
              <div class="input-group mb-4">
                <input
                  type="text"
                  class="form-control"
                  v-model="searchKeyword"
                  placeholder="Tìm tên khách hàng, sđt"
                  @keyup.enter="handleTimKhachHang"
                  @blur="handleBlurSearch"
                />
              </div>

              <div
                v-if="searchResults.length > 0"
                class="search-results-dropdown"
              >
                <ul class="list-group list-group-flush shadow">
                  <li
                    v-for="kh in searchResults"
                    :key="kh.id"
                    class="list-group-item list-group-item-action"
                    @mousedown.prevent="handleSelectKhachHang(kh)"
                  >
                    <div class="d-flex flex-column text-start">
                      <div class="fw-bold text-dark">
                        {{ kh.hoTen }}
                      </div>
                      <div class="small text-muted mt-0">
                        {{ kh.sdt }} | Mã: {{ kh.ma }}
                      </div>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="text-start">
              <div class="row g-2 mb-1">
                <div class="col-6">
                  <label
                    for="hoTen"
                    class="form-label fw-bold small mb-0 text-dark"
                  >
                    Tên khách hàng <span class="text-danger">*</span>
                  </label>
                </div>
                <div class="col-6">
                  <label
                    for="sdt"
                    class="form-label fw-bold small mb-0 text-dark"
                  >
                    Số điện thoại <span class="text-danger">*</span>
                  </label>
                </div>
              </div>

              <div class="row g-2 mb-3">
                <div class="col-6">
                  <div class="input-group">
                    <span class="input-group-text">
                      <i class="fa-regular fa-user"></i>
                    </span>
                    <input
                      id="hoTen"
                      type="text"
                      class="form-control"
                      v-model="hoaDon.khachHang.hoTen"
                      placeholder="Tên khách hàng"
                      disabled
                      @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
                    />
                  </div>
                </div>
                <div class="col-6">
                  <div class="input-group">
                    <span class="input-group-text">
                      <i class="fa-solid fa-phone"></i>
                    </span>
                    <input
                      id="sdt"
                      type="text"
                      class="form-control"
                      v-model="hoaDon.khachHang.sdt"
                      placeholder="Số điện thoại"
                      disabled
                      @blur="handleCapNhatKhachHang(hoaDon.khachHang)"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="d-grid gap-2 mt-2">
              <button
                class="btn btn-outline-warning btn-sm"
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

            <div class="border rounded p-2 bg-light-subtle mb-3">
              <div class="d-flex justify-content-between mb-1">
                <span class="text-muted">Tổng tiền hàng:</span>
                <span class="text-muted fw-bold">
                  {{ (tongTienHang ?? 0).toLocaleString() }} VND
                </span>
              </div>

              <div class="d-flex justify-content-between mb-2">
                <span class="text-danger">Giảm giá:</span>
                <span class="text-danger fw-bold">
                  - {{ (soTienGiamGia ?? 0).toLocaleString() }} VND
                </span>
              </div>

              <hr class="my-1" />

              <div class="d-flex justify-content-between pt-1">
                <span class="fw-bold">THÀNH TIỀN:</span>
                <span class="fw-bolder fs-5 text-warning">
                  {{ (tongTienSauGiam ?? 0).toLocaleString() }} VND
                </span>
              </div>
            </div>

            <div class="mb-3 text-start">
              <h6 class="fw-bold">Phương thức thanh toán</h6>

              <div class="d-flex justify-content-between gap-2">
                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('CHUYEN_KHOAN')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'CHUYEN_KHOAN',
                      'btn-outline-secondary':
                        phuongThucThanhToan !== 'CHUYEN_KHOAN',
                    }"
                    class="btn w-100"
                  >
                    Chuyển khoản
                  </button>
                </div>

                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('TIEN_MAT')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'TIEN_MAT',
                      'btn-outline-secondary':
                        phuongThucThanhToan !== 'TIEN_MAT',
                    }"
                    class="btn w-100"
                  >
                    Tiền mặt
                  </button>
                </div>

                <div class="flex-fill">
                  <button
                    @click="handleChonPhuongThuc('CA_HAI')"
                    :class="{
                      'btn-warning text-white':
                        phuongThucThanhToan === 'CA_HAI',
                      'btn-outline-secondary': phuongThucThanhToan !== 'CA_HAI',
                    }"
                    class="btn w-100"
                  >
                    Cả hai
                  </button>
                </div>
              </div>
            </div>

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
import { ref, computed, watch } from "vue";
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
import { useRoute } from "vue-router";
import router from "@/router";

const notify = useNotify();
const idNhanVien = "02b6c170-6aa5-4cc7-8e52-abc123456789";

// state
const hoaDonChoList = ref([]);
const selectedHoaDonId = ref(null);
const hoaDon = ref(null); // hóa đơn đang chọn
const gioHang = ref([]);

const filterMauSac = ref("");
const filterKichThuoc = ref("");
const filterXuatXu = ref("");
const searchSanPham = ref("");
const filteredSanPham = ref([]);
const searchKeyword = ref("");
const searchResults = ref([]);

const isGuestEditable = ref(false);
const showAddGuestButton = ref(false);

const danhSachSanPham = ref([]);

const currentPage = ref(1);
const itemsPerPage = 5; // Số phần tử mỗi trang

const totalPages = computed(() => {
  return Math.ceil(filteredSanPham.value.length / itemsPerPage);
});

// Danh sách sản phẩm chỉ hiển thị trên trang hiện tại
const paginatedSanPham = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredSanPham.value.slice(start, end);
});

// Hàm chuyển trang
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

// Đảm bảo về trang 1 khi danh sách sản phẩm thay đổi (do lọc/tìm kiếm)
watch(filteredSanPham, () => {
  currentPage.value = 1;
});

// Hàm định dạng tiền tệ đơn giản trong
const formatCurrency = (amount) => {
  if (amount === null || amount === undefined) return "0 ₫";
  return amount.toLocaleString("vi-VN", { style: "currency", currency: "VND" });
};

// trạng thái
const trangThaiText = (value) => {
  const map = {
    0: "Đã hủy",
    1: "Chờ xác nhận",
    2: "Đã xác nhận",
    4: "Đang giao",
    5: "Hoàn thành",
    3: "Chờ thanh toán",
  };
  return map[value] || "Không xác định";
};

const listMauSac = computed(() => {
  const maus = danhSachSanPham.value.map((sp) => sp.mauSac).filter(Boolean);
  return [...new Set(maus)];
});

const listKichThuoc = computed(() => {
  const kts = danhSachSanPham.value.map((sp) => sp.kichThuoc).filter(Boolean);
  return [...new Set(kts)];
});

const listXuatXu = computed(() => {
  const xxs = danhSachSanPham.value.map((sp) => sp.tenXuatXu).filter(Boolean);
  return [...new Set(xxs)];
});

const filterSanPham = () => {
  if (!Array.isArray(danhSachSanPham.value)) {
    filteredSanPham.value = [];
    return;
  }

  const keyword = searchSanPham.value.trim().toLowerCase();

  filteredSanPham.value = danhSachSanPham.value.filter((sp) => {
    const ten = sp.tenSanPham?.toLowerCase() || sp.ten?.toLowerCase() || "";
    const ma = sp.ma?.toLowerCase() || "";
    const mau = sp.mauSac?.toLowerCase() || "";
    const kt = sp.kichThuoc?.toLowerCase() || "";
    const xx = sp.tenXuatXu?.toLowerCase() || "";

    const matchesSearch =
      ten.includes(keyword) ||
      ma.includes(keyword) ||
      mau.includes(keyword) ||
      kt.includes(keyword) ||
      xx.includes(keyword);

    const matchesMauSac =
      !filterMauSac.value || mau === filterMauSac.value.toLowerCase();

    const matchesKichThuoc =
      !filterKichThuoc.value || kt === filterKichThuoc.value.toLowerCase();

    const matchesXuatXu =
      !filterXuatXu.value || xx === filterXuatXu.value.toLowerCase();

    return matchesSearch && matchesMauSac && matchesKichThuoc && matchesXuatXu;
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
      khachHang: khachLeMacDinh,
    };
    hoaDonChoList.value.push(newHoaDon);
    selectedHoaDonId.value = newHoaDon.id;
    hoaDon.value = newHoaDon;
    gioHang.value = [];
    tongTienHang.value = 0;
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
  tongTienHang.value = gioHang.value.reduce((sum, sp) => sum + sp.thanhTien, 0);

  // LOGIC MỚI: Kiểm tra Khách hàng mặc định
  const currentKhachHang = hoaDon.value?.khachHang;
  isGuestEditable.value =
    !currentKhachHang ||
    currentKhachHang.id === khachLeMacDinh.id ||
    currentKhachHang.ma === khachLeMacDinh.ma; // Giả sử mã KHÁCH LẺ là "KHACHLE"
  searchKeyword.value = hoaDon.value?.khachHang?.sdt || ""; // Set SĐT hiện tại vào ô tìm kiếm
  showAddGuestButton.value = false; // Mặc định ẩn
};

const KHACH_LE_ID = 1;

// 1. Định nghĩa Khách vãng lai Mặc định
const khachLeMacDinh = {
  id: KHACH_LE_ID,
  hoTen: "Khách lẻ",
  sdt: "0000000000",
  // Thêm các trường khác nếu cần (ví dụ: email: '', diaChi: '')
};

// Hàm XỬ LÝ KHI MẤT FOCUS KHỎI INPUT TÌM KIẾM
const handleBlurSearch = () => {
  setTimeout(() => {
    searchResults.value = [];
  }, 0);
};

// Hàm TÌM KIẾM KHÁCH HÀNG
const handleTimKhachHang = async () => {
  const keywword = searchKeyword.value.trim();
  if (keywword.length < 2) {
    searchResults.value = [];
    if (!keywword) {
      return notify.warning("Vui lòng nhập Tên/SĐT để tìm kiếm!");
    }
    return;
  }

  try {
    const res = await timKhachHangDaDangKy(keywword); // Gọi API tìm kiếm

    if (res.data && res.data.length > 0) {
      searchResults.value = res.data;
      if (res.data.length === 1) {
        const foundKhachHang = res.data[0];
        await assignKhachHang(foundKhachHang);
        notify.success("Đã tìm thấy 1 Khách hàng!");
      } else {
        notify.info(`Tìm thấy ${res.data.length} Khách hàng.`);
      }
      showAddGuestButton.value = false;
    } else {
      // ⚠️ KHÔNG TÌM THẤY
      searchResults.value = [];
      showAddGuestButton.value = true;
      notify.warning("Không tìm thấy Khách hàng. Bạn có thể thêm mới nhanh.");
    }
  } catch (err) {
    console.error("Lỗi tìm kiếm KH:", err);
    notify.error("Lỗi khi tìm kiếm Khách hàng!");
  }
};

const assignKhachHang = async (khachHang) => {
  try {
    // 1. Gán Khách hàng mới vào hóa đơn trên BE
    await capNhatKhachHang(hoaDon.value.id, khachHang.id);

    // 2. Cập nhật trạng thái FE
    hoaDon.value.khachHang = khachHang;
    isGuestEditable.value = false; // Khóa input khi đã gán KH đăng ký
    searchResults.value = []; // Xóa danh sách kết quả
    searchKeyword.value = khachHang.hoTen; // Cập nhật SĐT vào ô tìm kiếm
    notify.success("Cập nhật Khách hàng thành công!");
  } catch (error) {
    console.error("Lỗi khi gán Khách hàng cho hóa đơn:", error);
    notify.error("Không thể gán Khách hàng này cho Hóa đơn.");
  }
};

// Bạn sẽ gọi hàm này từ giao diện Modal/Dropdown
const handleSelectKhachHang = (khachHang) => {
  assignKhachHang(khachHang);
  notify.success(`Đã chọn Khách hàng: ${khachHang.hoTen}`);
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
      if (!value || value.trim().length < 10 || value.trim().length > 10) {
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
      if (!value || value.trim() === "") {
        return "Bạn cần nhập tên!";
      }
    },
  });
  if (hoTen) {
    try {
      // Chuẩn bị dữ liệu gửi lên (Sử dụng hoTen thay vì ten)
      const newKhachHangData = { hoTen: hoTen.trim(), sdt: newSdt.trim() };

      const res = await themKhachHangMoi(newKhachHangData); // Gọi API thêm mới

      if (!res.data) {
        throw new Error("API thêm mới không trả về dữ liệu Khách hàng.");
      }

      const newKhachHang = res.data;

      // 3. Gán Khách hàng mới vào hóa đơn (CHỈ TRUYỀN ID)
      await capNhatKhachHang(hoaDon.value.id, newKhachHang.id);

      // 4. Bind dữ liệu về 2 ô input (Đã đúng)
      hoaDon.value.khachHang = newKhachHang;

      // Xóa nội dung ô tìm kiếm sau khi thêm thành công (tùy chọn)
      isGuestEditable.value = false;
      searchKeyword.value = "";
      showAddGuestButton.value = false;
      notify.success("Thêm mới và gán Khách hàng thành công!");
    } catch (err) {
      console.error("Lỗi thêm nhanh KH:", err);
      let errorMessage = "Không thể thêm Khách hàng mới!";
      if (err.response) {
        console.error("Lỗi Response Data:", err.response.data);
        errorMessage =
          err.response.data || err.response.statusText || errorMessage;
      }
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
      tongTienHang.value = 0;
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
    const chiTietHDMoi = res.data;
    const chiTietGioHang = {
      ...chiTietHDMoi,
      tenSanPham: sp.tenSanPham,
      mauSac: sp.mauSac,
      kichThuoc: sp.kichThuoc,
      hinhAnhUrl: sp.hinhAnhUrl,
      tenXuatXu: sp.tenXuatXu,
    };
    const indexGioHang = gioHang.value.findIndex(
      (item) => item.id === chiTietGioHang.id
    );

    if (indexGioHang !== -1) {
      gioHang.value[indexGioHang] = chiTietGioHang;
    } else {
      gioHang.value.push(chiTietGioHang);
    }

    hoaDon.value.sanPhamList = gioHang.value;

    const newTongSoLuong = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuong;
    }

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

    gioHang.value = gioHang.value.filter((item) => item.id !== idSp);
    hoaDon.value.sanPhamList = gioHang.value;

    const newTongSoLuong = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );

    const indexHoaDonCho = hoaDonChoList.value.findIndex(
      (hd) => hd.id === hoaDon.value.id
    );

    if (indexHoaDonCho !== -1) {
      hoaDonChoList.value[indexHoaDonCho].soLuong = newTongSoLuong;
    }

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

const soTienGiamGia = ref(0); // Bắt đầu bằng 0, sau này sẽ là kết quả của việc áp dụng mã giảm giá

// ... (sau hàm tongTien computed)

// 💡 Tính Tổng tiền Hàng (Tổng tiền cũ của bạn)
const tongTienHang = computed(() => {
  if (!gioHang.value || gioHang.value.length === 0) {
    return 0;
  } // Đảm bảo các thuộc tính (thanhTien) là số trước khi tính tổng
  return gioHang.value.reduce((sum, p) => sum + (p.thanhTien || 0), 0);
});

// 💡 Tính Tổng tiền Sau Giảm
const tongTienSauGiam = computed(() => {
  // Tổng tiền hàng - Số tiền giảm (Đảm bảo không âm)
  const result = tongTienHang.value - soTienGiamGia.value;
  return Math.max(0, result);
});

const PHUONG_THUC_ID_MAP = {
  TIEN_MAT: "145B12D7-25E0-4B1A-AC21-CD64328FD446",
  CHUYEN_KHOAN: "B6A1BBF4-E9DF-4C88-90F9-C89599679FDC",
  CA_HAI: "AF15E02B-80D8-41CA-9C8C-D3ECB0B290C7",
};
const phuongThucThanhToan = ref("TIEN_MAT"); // Mặc định là Tiền mặt

const handleChonPhuongThuc = (phuongThuc) => {
  phuongThucThanhToan.value = phuongThuc;
  notify.info(`Đã chọn thanh toán bằng: ${phuongThuc}`);
};
// thanh toán
const handleThanhToan = async () => {
  if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
  if (gioHang.value.length === 0) return notify.warning("Giỏ hàng rỗng!");

  const selectedPtttCode = phuongThucThanhToan.value;
  const idPhuongThucThanhToan = PHUONG_THUC_ID_MAP[selectedPtttCode];

  if (!idPhuongThucThanhToan) {
    return notify.error("Phương thức thanh toán không hợp lệ!");
  }

  const confirm = await Swal.fire({
    title: "Xác nhận Thanh toán?",
    // Hiển thị tổng tiền và phương thức thanh toán
    html: `
        Bạn chắc chắn muốn thanh toán <strong class="text-danger">${(
          tongTienSauGiam.value ?? 0
        ).toLocaleString()} VND</strong> cho đơn hàng này ?
    `,
    icon: "question",
    showCancelButton: true,
    cancelButtonText: "Hủy",
    confirmButtonText: "Xác nhận Thanh toán",
    reverseButtons: true,
  });

  if (!confirm.isConfirmed) {
    notify.info("Đã hủy thanh toán.");
    return; // Dừng lại nếu người dùng hủy
  }

  try {
    // Gửi ID Hóa đơn và ID Phương thức Thanh toán lên BE
    await thanhToan(hoaDon.value.id, idPhuongThucThanhToan);

    const completedHoaDonId = hoaDon.value.id;

    // Xử lý logic FE sau khi thành công
    notify.success(
      "Thanh toán thành công! Chuẩn bị chuyển đến chi tiết hóa đơn ..."
    );
    hoaDonChoList.value = hoaDonChoList.value.filter(
      (hd) => hd.id !== hoaDon.value.id
    );
    hoaDon.value = null;
    gioHang.value = [];
    selectedHoaDonId.value = null;
    router.push({ name: "ChiTietHD", params: { id: completedHoaDonId } });
  } catch (err) {
    console.error("Lỗi thanh toán:", err);
    notify.error("Thanh toán thất bại! Vui lòng kiểm tra Server.");
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
    filterSanPham();
    if (filteredSanPham.value.length > 0) {
      console.log(
        "Dữ liệu sản phẩm đầu tiên (Kiểm tra URL ảnh):",
        filteredSanPham.value[0]
      );
    }
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
.product-thumb {
  width: 60px; /* Chiều rộng cố định */
  height: 60px; /* Chiều cao cố định */
  object-fit: cover; /* Đảm bảo ảnh không bị méo */
  border-radius: 4px;
  border: 1px solid #ddd;
}
/* CSS cho hình ảnh sản phẩm trong Giỏ hàng (Card 2) */
.cart-thumb {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}
</style>
