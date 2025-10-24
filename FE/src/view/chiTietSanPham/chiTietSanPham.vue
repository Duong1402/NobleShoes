<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { ref, onMounted, computed, watch } from "vue"; // Thêm watch
import Swal from "sweetalert2";
import { useRoute } from "vue-router";
import { useNotify } from "@/composables/useNotify";
import axios from "@/service/axios.js";

import { getChiTietSanPhamBySanPhamId } from "@/service/ChiTietSanPhamService";
import {
  getAllDanhMuc,
  getAllThuongHieu,
  getAllXuatXu,
  getAllMucDichSuDung,
  getAllMauSac,
  getAllKichThuoc,
  getAllChatLieu
} from "@/service/ComboBoxService";
import { uploadImageToCloudinary } from "@/service/UploadService";

const route = useRoute();
const sanPhamId = route.params.id;

const chiTietSP = ref([]);
const filterMau = ref("");
const filterSize = ref("");

// 💡 PHÂN TRANG: Khởi tạo biến
const currentPage = ref(1);
const pageSize = ref(10); // Số lượng mục trên 1 trang

const selectedCTSP = ref({
  id: null,
  maSP: "",
  tenSP: "",
  danhMucId: null,
  thuongHieuId: null,
  xuatXuId: null,
  mucDichSuDungId: null,
  giaBan: 0,
  mauSacId: null,
  kichThuocId: null,
  chatLieuId: null,
  hinhAnhUrl: ""
});

const showModal = ref(false);
const previewImage = ref(null);
const notify = useNotify();

// Combo box lists
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const xuatXuList = ref([]);
const mucDichSuDungList = ref([]);
const mauSacList = ref([]);
const kichThuocList = ref([]);
const chatLieuList = ref([]);

// Filter table (GIỮ NGUYÊN)
const filteredChiTietSP = computed(() =>
  chiTietSP.value.filter(ct =>
    (!filterMau.value || ct.mauSacId === filterMau.value) &&
    (!filterSize.value || ct.kichThuocId === filterSize.value)
  )
);

// 💡 PHÂN TRANG: Tính tổng số trang (Dựa trên filteredChiTietSP)
const totalPages = computed(() => Math.ceil(filteredChiTietSP.value.length / pageSize.value));

// 💡 PHÂN TRANG: Danh sách chi tiết SP hiển thị theo trang
const pagedChiTietSP = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredChiTietSP.value.slice(start, start + pageSize.value);
});

// 💡 WATCH: Theo dõi bộ lọc để đặt lại trang về 1
watch([filterMau, filterSize], () => {
    currentPage.value = 1;
});


// Load combo box data
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
    console.error("Lỗi khi load combo box:", err);
    notify.error("Lỗi khi tải dữ liệu combo box!");
  }
};

// Load chi tiết sản phẩm
const loadChiTietSP = async () => {
  try {
    const res = await getChiTietSanPhamBySanPhamId(sanPhamId);
    const data = Array.isArray(res) ? res : res.data || [];
    chiTietSP.value = data.map(ct => ({
      ...ct,
      danhMucId: danhMucList.value.find(d => d.ten === ct.tenDanhMuc)?.id || null,
      thuongHieuId: thuongHieuList.value.find(t => t.ten === ct.tenThuongHieu)?.id || null,
      xuatXuId: xuatXuList.value.find(x => x.ten === ct.tenXuatXu)?.id || null,
      mucDichSuDungId: mucDichSuDungList.value.find(m => m.ten === ct.mucDichSuDung)?.id || null,
      mauSacId: mauSacList.value.find(m => m.ten === ct.mauSac)?.id || null,
      kichThuocId: kichThuocList.value.find(k => k.ten === ct.kichThuoc)?.id || null,
      chatLieuId: chatLieuList.value.find(c => c.ten === ct.chatLieu)?.id || null
    }));
    // Đảm bảo trang hiện tại hợp lệ sau khi load dữ liệu
    if (currentPage.value > totalPages.value) currentPage.value = 1;
  } catch (err) {
    console.error(err);
    notify.error("Lỗi khi tải chi tiết sản phẩm!");
  }
};

// Mở modal chi tiết
const openDetail = (ct) => {
  selectedCTSP.value = { ...ct };
  previewImage.value = ct.hinhAnhUrl || null;
  showModal.value = true;
};

// Đóng modal
const closeModal = () => {
  selectedCTSP.value = {
    id: null,
    maSP: "",
    tenSP: "",
    danhMucId: null,
    thuongHieuId: null,
    xuatXuId: null,
    mucDichSuDungId: null,
    giaBan: 0,
    mauSacId: null,
    kichThuocId: null,
    chatLieuId: null,
    hinhAnhUrl: ""
  };
  previewImage.value = null;
  showModal.value = false;
};

// Upload ảnh
const onImageChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;
  try {
    const url = await uploadImageToCloudinary(file);
    selectedCTSP.value.hinhAnhUrl = url;
    previewImage.value = url;
  } catch (err) {
    notify.error("Upload ảnh thất bại!");
  }
};

// Lưu chi tiết (chỉ update các trường hiển thị)
const saveDetail = async () => {
  if (!selectedCTSP.value.tenSP) {
    notify.error("Tên sản phẩm không được để trống!");
    return;
  }
  if (!selectedCTSP.value.giaBan || selectedCTSP.value.giaBan <= 0) {
    notify.error("Giá bán phải lớn hơn 0!");
    return;
  }

  const payload = {
    tenSP: selectedCTSP.value.tenSP,
    danhMucId: selectedCTSP.value.danhMucId,
    thuongHieuId: selectedCTSP.value.thuongHieuId,
    xuatXuId: selectedCTSP.value.xuatXuId,
    mucDichSuDungId: selectedCTSP.value.mucDichSuDungId,
    giaBan: Number(selectedCTSP.value.giaBan),
    mauSacId: selectedCTSP.value.mauSacId,
    kichThuocId: selectedCTSP.value.kichThuocId,
    chatLieuId: selectedCTSP.value.chatLieuId,
    hinhAnhUrl: selectedCTSP.value.hinhAnhUrl || null
  };

  try {
    await axios.put(`/admin/chi-tiet-san-pham/${selectedCTSP.value.id}`, payload);
    notify.success("Cập nhật thành công!");
    closeModal();
    await loadChiTietSP();
  } catch (err) {
    console.error(err.response?.data || err);
    notify.error("Cập nhật thất bại!");
  }
};

// 💡 PHÂN TRANG: Hàm chuyển trang
const goToPage = (page) => {
  if (totalPages.value === 0) {
    currentPage.value = 1;
    return;
  }
  if (page < 1) page = 1;
  if (page > totalPages.value) page = totalPages.value;
  currentPage.value = page;
};

onMounted(async () => {
  await loadComboBox();
  await loadChiTietSP();
});
</script>

<template>
  <div class="container-fluid mt-4 px-5">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý chi tiết sản phẩm</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card mb-3">
      <div class="card-body d-flex gap-2 flex-wrap">
        <select class="form-select" v-model="filterMau">
          <option value="">-- Chọn màu --</option>
          <option v-for="m in mauSacList" :key="m.id" :value="m.id">{{ m.ten }}</option>
        </select>
        <select class="form-select" v-model="filterSize">
          <option value="">-- Chọn size --</option>
          <option v-for="k in kichThuocList" :key="k.id" :value="k.id">{{ k.ten }}</option>
        </select>
      </div>
    </div>

    <div class="card">
      <div class="card-body table-responsive">
        <table class="table table-bordered table-hover">
          <thead class="table-warning text-center">
            <tr>
              <th>STT</th>
              <th>Hình ảnh</th>
              <th>Tên SP</th>
              <th>Danh mục</th>
              <th>Thương hiệu</th>
              <th>Xuất xứ</th>
              <th>Mục đích</th>
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
              <td class="text-center">
                <img :src="ct.hinhAnhUrl || 'https://via.placeholder.com/40'" alt="img"
                  style="width:40px;height:40px;object-fit:cover;" />
              </td>
              <td>{{ ct.tenSP }}</td>
              <td>{{danhMucList.find(d => d.id === ct.danhMucId)?.ten}}</td>
              <td>{{thuongHieuList.find(t => t.id === ct.thuongHieuId)?.ten}}</td>
              <td>{{xuatXuList.find(x => x.id === ct.xuatXuId)?.ten}}</td>
              <td>{{mucDichSuDungList.find(m => m.id === ct.mucDichSuDungId)?.ten}}</td>
              <td>{{ ct.giaBan }}</td>
              <td>{{mauSacList.find(m => m.id === ct.mauSacId)?.ten}}</td>
              <td>{{kichThuocList.find(k => k.id === ct.kichThuocId)?.ten}}</td>
              <td>{{chatLieuList.find(c => c.id === ct.chatLieuId)?.ten}}</td>
              <td class="text-center">
                <button class="btn btn-sm btn-info" @click="openDetail(ct)">Chi tiết</button>
              </td>
            </tr>
            <tr v-if="filteredChiTietSP.length === 0">
              <td colspan="12" class="text-center">Không có dữ liệu</td>
            </tr>
          </tbody>
        </table>
        
        <nav v-if="totalPages > 1" aria-label="Page navigation">
            <ul class="pagination justify-content-end mt-3">
                <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <a class="page-link" href="#" @click.prevent="goToPage(currentPage - 1)">Trước</a>
                </li>
                <li class="page-item" v-for="page in totalPages" :key="page" :class="{ active: currentPage === page }">
                    <a class="page-link" href="#" @click.prevent="goToPage(page)">{{ page }}</a>
                </li>
                <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <a class="page-link" href="#" @click.prevent="goToPage(currentPage + 1)">Sau</a>
                </li>
            </ul>
        </nav>
        </div>
    </div>

    <div v-if="showModal" class="modal fade show d-block" style="background: rgba(0,0,0,0.5);">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Chi tiết sản phẩm: {{ selectedCTSP.tenSP }}</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div class="row g-3">
              <div class="col-md-2 text-center">
                <img :src="previewImage || selectedCTSP.hinhAnhUrl || 'https://via.placeholder.com/150'" alt="img"
                  class="img-fluid rounded mb-2" />
                <input type="file" @change="onImageChange" />
              </div>
              <div class="col-md-10">
                <div class="row g-2">
                  <div class="col-md-4">
                    <label class="form-label">Tên SP</label>
                    <input class="form-control" v-model="selectedCTSP.tenSP" />
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Danh mục</label>
                    <select class="form-select" v-model="selectedCTSP.danhMucId">
                      <option v-for="item in danhMucList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Thương hiệu</label>
                    <select class="form-select" v-model="selectedCTSP.thuongHieuId">
                      <option v-for="item in thuongHieuList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Xuất xứ</label>
                    <select class="form-select" v-model="selectedCTSP.xuatXuId">
                      <option v-for="item in xuatXuList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Mục đích</label>
                    <select class="form-select" v-model="selectedCTSP.mucDichSuDungId">
                      <option v-for="item in mucDichSuDungList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Giá bán</label>
                    <input type="number" class="form-control" v-model="selectedCTSP.giaBan" />
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Màu sắc</label>
                    <select class="form-select" v-model="selectedCTSP.mauSacId">
                      <option v-for="item in mauSacList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Kích thước</label>
                    <select class="form-select" v-model="selectedCTSP.kichThuocId">
                      <option v-for="item in kichThuocList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Chất liệu</label>
                    <select class="form-select" v-model="selectedCTSP.chatLieuId">
                      <option v-for="item in chatLieuList" :key="item.id" :value="item.id">{{ item.ten }}</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn btn-success" @click="saveDetail">Lưu</button>
            <button class="btn btn-secondary" @click="closeModal">Đóng</button>
          </div>
        </div>
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
}

.table thead th {
  font-weight: 600;
}

.modal-backdrop.show {
  display: none;
}
</style>