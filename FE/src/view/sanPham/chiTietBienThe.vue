<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed, watch } from "vue";
import Swal from "sweetalert2";
import { useRoute } from "vue-router";
import { useNotify } from "@/composables/useNotify";
import axios from "@/service/axios.js";

import { getAllChiTietSanPham } from "@/service/ChiTietSanPhamService";
import {
  getAllDanhMuc,
  getAllThuongHieu,
  getAllXuatXu,
  getAllMucDichSuDung,
  getAllMauSac,
  getAllKichThuoc,
  getAllChatLieu,
} from "@/service/ComboBoxService";
import { uploadImageToCloudinary } from "@/service/UploadService";
import QRCode from "qrcode";

const route = useRoute();
const sanPhamId = route.params.id;
const notify = useNotify();

const chiTietSP = ref([]);
const filterMau = ref("");
const filterSize = ref("");
const filterKeyword = ref("");
const filterDanhMuc = ref("");
const filterThuongHieu = ref("");
const filterChatLieu = ref("");
const filterMucDich = ref("");
const filterTrangThai = ref("");
const filterMinPrice = ref(0);
const filterMaxPrice = ref(0);

const resetFilters = () => {
  filterKeyword.value = "";
  filterDanhMuc.value = "";
  filterThuongHieu.value = "";
  filterMau.value = "";
  filterChatLieu.value = "";
  filterSize.value = "";
  filterMucDich.value = "";
  filterTrangThai.value = "";
  filterMinPrice.value = 0;
  filterMaxPrice.value = 0;
};

// Phân trang
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = computed(() =>
  Math.ceil(filteredChiTietSP.value.length / pageSize.value)
);
const goToPage = (page) => {
  if (totalPages.value === 0) currentPage.value = 1;
  else if (page < 1) currentPage.value = 1;
  else if (page > totalPages.value) currentPage.value = totalPages.value;
  else currentPage.value = page;
};

// Combo box lists
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const xuatXuList = ref([]);
const mucDichSuDungList = ref([]);
const mauSacList = ref([]);
const kichThuocList = ref([]);
const chatLieuList = ref([]);

// Inline edit
const editingCTSP = ref(null);
const previewImage = ref(null);

// Filtered & paged
const filteredChiTietSP = computed(() =>
  chiTietSP.value.filter((ct) => {
    const matchKeyword =
      !filterKeyword.value ||
      ct.tenSanPham
        ?.toLowerCase()
        .includes(filterKeyword.value.toLowerCase()) ||
      ct.ma?.toLowerCase().includes(filterKeyword.value.toLowerCase());
    const matchDanhMuc =
      !filterDanhMuc.value || ct.danhMucId === filterDanhMuc.value;
    const matchThuongHieu =
      !filterThuongHieu.value || ct.thuongHieuId === filterThuongHieu.value;
    const matchChatLieu =
      !filterChatLieu.value || ct.chatLieuId === filterChatLieu.value;
    const matchMau = !filterMau.value || ct.mauSacId === filterMau.value;
    const matchSize = !filterSize.value || ct.kichThuocId === filterSize.value;
    const matchMucDich =
      !filterMucDich.value || ct.mucDichSuDungId === filterMucDich.value;
    const matchTrangThai =
      !filterTrangThai.value || ct.trangThai === filterTrangThai.value;
    const matchGia =
      (!filterMinPrice.value || ct.giaBan >= filterMinPrice.value) &&
      (!filterMaxPrice.value || ct.giaBan <= filterMaxPrice.value);

    return (
      matchKeyword &&
      matchDanhMuc &&
      matchThuongHieu &&
      matchChatLieu &&
      matchMau &&
      matchSize &&
      matchMucDich &&
      matchTrangThai &&
      matchGia
    );
  })
);

const pagedChiTietSP = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredChiTietSP.value.slice(start, start + pageSize.value);
});

// Watch filters để reset trang
watch([filterMau, filterSize], () => {
  currentPage.value = 1;
});

// Load combo box
const loadComboBox = async () => {
  try {
    danhMucList.value = await getAllDanhMuc();
    thuongHieuList.value = await getAllThuongHieu();
    xuatXuList.value = await getAllXuatXu();
    mucDichSuDungList.value = await getAllMucDichSuDung();
    mauSacList.value = await getAllMauSac();
    kichThuocList.value = await getAllKichThuoc();
    chatLieuList.value = await getAllChatLieu();
  } catch (err) {
    console.error(err);
    notify.error("Lỗi khi tải combo box!");
  }
};

// Load chi tiết sản phẩm
const loadChiTietSP = async () => {
  try {
    const res = await getAllChiTietSanPham(sanPhamId);
    const data = Array.isArray(res) ? res : res.data || [];
    chiTietSP.value = data.map((ct) => ({
      ...ct,
      ma: ct.ma,
      tenSP: ct.tenSanPham,
      soLuongTon: ct.soLuongTon || 0,
      danhMucId:
        danhMucList.value.find((d) => d.ten === ct.danhMuc)?.id || null,
      thuongHieuId:
        thuongHieuList.value.find((t) => t.ten === ct.thuongHieu)?.id || null,
      xuatXuId:
        xuatXuList.value.find((x) => x.ten === ct.tenXuatXu)?.id || null,
      mucDichSuDungId:
        mucDichSuDungList.value.find((m) => m.ten === ct.mucDich)?.id || null,
      mauSacId: mauSacList.value.find((m) => m.ten === ct.mauSac)?.id || null,
      kichThuocId:
        kichThuocList.value.find((k) => k.ten === ct.kichThuoc)?.id || null,
      chatLieuId:
        chatLieuList.value.find((c) => c.ten === ct.chatLieu)?.id || null,
    }));
  } catch (err) {
    console.error(err);
    notify.error("Lỗi khi tải chi tiết sản phẩm!");
  }
};

// Inline edit
const startEditing = (ct) => {
  editingCTSP.value = { ...ct };
  previewImage.value = ct.hinhAnhUrl || null;
};

const onImageChangeInline = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const url = await uploadImageToCloudinary(file);
    editingCTSP.value.hinhAnhUrl = url;
    previewImage.value = url;
  } catch (err) {
    notify.error("Upload ảnh thất bại!");
  }
};

const saveInline = async () => {
  if (!editingCTSP.value.tenSanPham) {
    notify.error("Tên sản phẩm không được để trống!");
    return;
  }
  if (!editingCTSP.value.giaBan || editingCTSP.value.giaBan <= 0) {
    notify.error("Giá bán phải lớn hơn 0!");
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận cập nhật?",
    text: "Bạn có chắc chắn muốn lưu thay đổi sản phẩm này không?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Huỷ",
    reverseButtons: true,
  });

  if (!result.isConfirmed) return;

  const payload = {
    tenSP: editingCTSP.value.tenSP,
    danhMucId: editingCTSP.value.danhMucId,
    thuongHieuId: editingCTSP.value.thuongHieuId,
    xuatXuId: editingCTSP.value.xuatXuId,
    mucDichSuDungId: editingCTSP.value.mucDichSuDungId,
    giaBan: Number(editingCTSP.value.giaBan),
    mauSacId: editingCTSP.value.mauSacId,
    kichThuocId: editingCTSP.value.kichThuocId,
    chatLieuId: editingCTSP.value.chatLieuId,
    hinhAnhUrl: editingCTSP.value.hinhAnhUrl || null,
    soLuongTon: editingCTSP.value.soLuongTon,
  };

  try {
    await axios.put(
      `/admin/chi-tiet-san-pham/${editingCTSP.value.id}`,
      payload
    );
    notify.success("Cập nhật thành công!");
    editingCTSP.value = null;
    await loadChiTietSP();
  } catch (err) {
    console.error(err.response?.data || err);
    notify.error("Cập nhật thất bại!");
  }
};

// QR code
const downloadQR = async () => {
  if (!editingCTSP.value) return;
  try {
    const qrContent = `https://yourdomain.com/san-pham/${editingCTSP.value.id}`;
    const canvas = document.createElement("canvas");
    await QRCode.toCanvas(canvas, qrContent, { width: 300 });
    const link = document.createElement("a");
    link.href = canvas.toDataURL("image/png");
    link.download = `${editingCTSP.value.tenSP}_QR.png`;
    link.click();
  } catch (err) {
    console.error(err);
    notify.error("Không thể tạo mã QR!");
  }
};

onMounted(async () => {
  await loadComboBox();
  await loadChiTietSP();
});
</script>

<template>
  <div class="container-fluid mt-4 px-1">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý chi tiết sản phẩm</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <!-- Bộ lọc -->
    <div class="card mb-3 shadow-sm border-0">
      <div class="card-header">
        <div class="d-flex align-items-center">
          <h4 class="card-title"><i class="fa fa-filter me-2"></i> Bộ Lọc</h4>
        </div>
      </div>
      <div class="card-body">
        <div class="row g-2 align-items-end">
          <div class="col-md-3">
            <label class="form-label text-muted small mb-1"
              >Mã hoặc tên sản phẩm</label
            >
            <input
              v-model="filterKeyword"
              type="text"
              class="form-control"
              placeholder="Nhập mã hoặc tên sản phẩm..."
            />
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1">Danh mục</label>
            <select class="form-select" v-model="filterDanhMuc">
              <option value="">Tất cả</option>
              <option
                v-for="item in danhMucList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1">Thương hiệu</label>
            <select class="form-select" v-model="filterThuongHieu">
              <option value="">Tất cả</option>
              <option
                v-for="item in thuongHieuList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1">Màu sắc</label>
            <select class="form-select" v-model="filterMau">
              <option value="">Tất cả</option>
              <option
                v-for="item in mauSacList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1">Chất liệu</label>
            <select class="form-select" v-model="filterChatLieu">
              <option value="">Tất cả</option>
              <option
                v-for="item in chatLieuList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1">Kích cỡ</label>
            <select class="form-select" v-model="filterSize">
              <option value="">Tất cả</option>
              <option
                v-for="item in kichThuocList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-2">
            <label class="form-label text-muted small mb-1"
              >Mục đích sử dụng</label
            >
            <select class="form-select" v-model="filterMucDich">
              <option value="">Tất cả</option>
              <option
                v-for="item in mucDichSuDungList"
                :key="item.id"
                :value="item.id"
              >
                {{ item.ten }}
              </option>
            </select>
          </div>
          <div class="col-md-3">
            <label class="form-label text-muted small mb-1"
              >Khoảng giá (VND)</label
            >
            <div class="d-flex align-items-center gap-2">
              <input
                type="number"
                v-model.number="filterMinPrice"
                class="form-control"
                placeholder="Từ"
              />
              <span>-</span>
              <input
                type="number"
                v-model.number="filterMaxPrice"
                class="form-control"
                placeholder="Đến"
              />
            </div>
          </div>
          <div class="col-md-2">
            <button class="btn btn-warning w-100" @click="resetFilters">
              <i class="fa fa-undo me-1"></i>Đặt lại
            </button>
          </div>
          <!-- <div class="col-md-2">
            <router-link to="/admin/san-pham" class="btn btn-secondary w-100">
              <i class="fa fa-arrow-left me-1"></i>Quay lại
            </router-link>
          </div> -->
        </div>
      </div>
    </div>

    <!-- Inline edit form -->
    <div v-if="editingCTSP" class="card mb-3 shadow-sm border-0 p-3">
      <h5 class="text-warning">Chỉnh sửa sản phẩm: {{ editingCTSP.tenSP }}</h5>
      <div class="row g-3">
        <div class="col-md-2 text-center">
          <img
            :src="
              previewImage ||
              editingCTSP.hinhAnhUrl ||
              'https://via.placeholder.com/150'
            "
            class="img-fluid rounded mb-2"
            style="max-height: 150px; object-fit: cover; border: 1px solid #ddd"
          />
          <label
            for="image-upload-inline"
            class="btn btn-sm btn-outline-secondary w-100 mb-2"
          >
            Chọn tệp
          </label>
          <input
            type="file"
            id="image-upload-inline"
            @change="onImageChangeInline"
            class="d-none"
          />
          <small class="text-muted d-block overflow-hidden text-truncate">
            {{ previewImage ? "Đã chọn tệp" : "Không có tệp nào được chọn" }}
          </small>
        </div>

        <div class="col-md-10">
          <div class="row g-2">
            <div class="col-md-3">
              <label class="form-label">Mã SP</label>
              <input class="form-control" v-model="editingCTSP.ma" disabled />
            </div>
            <div class="col-md-3">
              <label class="form-label">Tên SP</label>
              <input class="form-control" v-model="editingCTSP.tenSP" />
            </div>
            <div class="col-md-3">
              <label class="form-label">Danh mục</label>
              <select class="form-select" v-model="editingCTSP.danhMucId">
                <option
                  v-for="item in danhMucList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Thương hiệu</label>
              <select class="form-select" v-model="editingCTSP.thuongHieuId">
                <option
                  v-for="item in thuongHieuList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Xuất xứ</label>
              <select class="form-select" v-model="editingCTSP.xuatXuId">
                <option
                  v-for="item in xuatXuList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Mục đích</label>
              <select class="form-select" v-model="editingCTSP.mucDichSuDungId">
                <option
                  v-for="item in mucDichSuDungList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Số lượng tồn</label>
              <input
                type="number"
                class="form-control"
                v-model="editingCTSP.soLuongTon"
              />
            </div>
            <div class="col-md-3">
              <label class="form-label">Giá bán</label>
              <input
                type="number"
                class="form-control"
                v-model="editingCTSP.giaBan"
              />
            </div>
            <div class="col-md-3">
              <label class="form-label">Màu sắc</label>
              <select class="form-select" v-model="editingCTSP.mauSacId">
                <option
                  v-for="item in mauSacList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Kích thước</label>
              <select class="form-select" v-model="editingCTSP.kichThuocId">
                <option
                  v-for="item in kichThuocList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Chất liệu</label>
              <select class="form-select" v-model="editingCTSP.chatLieuId">
                <option
                  v-for="item in chatLieuList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.ten }}
                </option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <div class="mt-3">
        <button class="btn btn-success me-2" @click="saveInline">Lưu</button>
        <button class="btn btn-secondary me-2" @click="editingCTSP = null">
          Hủy
        </button>
        <button class="btn btn-primary" @click="downloadQR">Tải QR</button>
      </div>
    </div>

    <!-- Table -->
    <div class="card">
      <div class="card-body table-responsive">
        <table class="table table-bordered table-hover">
          <thead class="table-warning text-center">
            <tr>
              <th>STT</th>
              <th>Mã</th>
              <th>Hình ảnh</th>
              <th>Tên SP</th>
              <th>Danh mục</th>
              <th>Thương hiệu</th>
              <th>Xuất xứ</th>
              <th>Mục đích</th>
              <th>Số lượng tồn</th>
              <th>Giá bán</th>
              <th>Màu sắc</th>
              <th>Kích thước</th>
              <th>Chất liệu</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(ct, index) in pagedChiTietSP" :key="ct.id">
              <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>
              <td>{{ ct.ma }}</td>
              <td class="text-center">
                <img
                  :src="ct.hinhAnhUrl || 'https://via.placeholder.com/40'"
                  alt="img"
                  style="width: 40px; height: 40px; object-fit: cover"
                />
              </td>
              <td>{{ ct.tenSanPham }}</td>
              <td>{{ ct.danhMuc }}</td>
              <td>
                {{ ct.thuongHieu }}
              </td>
              <td>{{ xuatXuList.find((x) => x.id === ct.xuatXuId)?.ten }}</td>
              <td>
                {{ ct.mucDich }}
              </td>
              <td>{{ ct.soLuongTon }}</td>
              <td>{{ ct.giaBan }}</td>
              <td>{{ mauSacList.find((m) => m.id === ct.mauSacId)?.ten }}</td>
              <td>
                {{ kichThuocList.find((k) => k.id === ct.kichThuocId)?.ten }}
              </td>
              <td>
                {{ chatLieuList.find((c) => c.id === ct.chatLieuId)?.ten }}
              </td>
              <td class="text-center">
                <button
                  class="btn btn-link text-info btn-lg p-0"
                  @click="startEditing(ct)"
                  title="Sửa chi tiết"
                >
                  <i class="fa fa-eye fs-5"></i>
                </button>
              </td>
            </tr>
            <tr v-if="filteredChiTietSP.length === 0">
              <td colspan="13" class="text-center">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>

        <nav v-if="totalPages > 1" aria-label="Page navigation">
          <ul class="pagination justify-content-end mt-3">
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
              :class="{ active: currentPage === page }"
            >
              <a class="page-link" href="#" @click.prevent="goToPage(page)">{{
                page
              }}</a>
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
</template>

<style scoped>
.table th,
.table td {
  font-size: 0.85rem;
  padding: 0.3rem 0.5rem;
  vertical-align: middle;
  text-align: center;
}

.table thead th {
  font-weight: 600;
  width: auto;
}
.table th:nth-child(1),
.table td:nth-child(1) {
  width: 5%;
} /* STT */
.table th:nth-child(2),
.table td:nth-child(2) {
  width: 7%;
} /* Hình ảnh */
.table th:nth-child(3),
.table td:nth-child(3) {
  width: 12%;
} /* Tên SP */
.table th:nth-child(4),
.table td:nth-child(4) {
  width: 9%;
} /* Danh mục */
.table th:nth-child(5),
.table td:nth-child(5) {
  width: 9%;
} /* Thương hiệu */
.table th:nth-child(6),
.table td:nth-child(6) {
  width: 8%;
} /* Xuất xứ */
.table th:nth-child(7),
.table td:nth-child(7) {
  width: 8%;
} /* Mục đích */
.table th:nth-child(8),
.table td:nth-child(8) {
  width: 7%;
} /* Số lượng tồn */
.table th:nth-child(9),
.table td:nth-child(9) {
  width: 9%;
} /* Giá bán */
.table th:nth-child(10),
.table td:nth-child(10) {
  width: 7%;
} /* Màu sắc */
.table th:nth-child(11),
.table td:nth-child(11) {
  width: 7%;
} /* Kích thước */
.table th:nth-child(12),
.table td:nth-child(12) {
  width: 7%;
} /* Chất liệu */
.table th:nth-child(13),
.table td:nth-child(13) {
  width: 5%;
} /* Thao tác */

.table td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px;
}
</style>
