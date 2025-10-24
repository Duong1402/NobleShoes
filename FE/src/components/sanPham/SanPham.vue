<!-- SanPham.vue -->
<script setup>
import { ref, onMounted } from 'vue';
import { 
  getAllSanPham, createSanPham, updateSanPham, deleteSanPhamById,
  thuongHieuList, danhMucList, xuatXuList, deGiayList, dayGiayList, mucDichSuDungList,
  mauSacList, kichThuocList, chatLieuList
} from './sanPhamService.js';

const listSanPham = ref([]);

// Form chính
const newSanPham = ref({
  ma: '', ten: '', id_thuong_hieu: '', id_danh_muc: '', id_xuat_xu: '',
  id_de_giay: '', id_day_giay: '', id_muc_dich_su_dung: '',
  id_mau_sac: '', id_kich_thuoc: '', id_chat_lieu: '',
  gia_nhap: 0, gia_ban: 0, so_luong_ton: 0, trang_thai: true
});

// Modal chi tiết
const detailForm = ref(false);
const selectedSanPham = ref(null);

onMounted(fetchSanPham);

async function fetchSanPham() {
  const res = await getAllSanPham();
  listSanPham.value = res.data;
}

async function addSanPham() {
  if (!newSanPham.value.ten) return alert('Tên sản phẩm bắt buộc');
  await createSanPham(newSanPham.value);
  resetForm();
  await fetchSanPham();
}

async function editSanPham() {
  if (!newSanPham.value.id) return alert('Vui lòng chọn sản phẩm để sửa');
  await updateSanPham(newSanPham.value.id, newSanPham.value);
  resetForm();
  await fetchSanPham();
}

async function removeSanPham(id) {
  if (confirm('Bạn có chắc muốn xóa sản phẩm này?')) {
    await deleteSanPhamById(id);
    await fetchSanPham();
  }
}

function editFromTable(sp) {
  newSanPham.value = { ...sp, ...sp.chi_tiet };
}

function showDetail(sp) {
  selectedSanPham.value = { ...sp, ...sp.chi_tiet };
  detailForm.value = true;
}

async function saveDetail() {
  if (!selectedSanPham.value.id) return;
  await updateSanPham(selectedSanPham.value.id, selectedSanPham.value);
  detailForm.value = false;
  await fetchSanPham();
}

function resetForm() {
  newSanPham.value = {
    ma: '', ten: '', id_thuong_hieu: '', id_danh_muc: '', id_xuat_xu: '',
    id_de_giay: '', id_day_giay: '', id_muc_dich_su_dung: '',
    id_mau_sac: '', id_kich_thuoc: '', id_chat_lieu: '',
    gia_nhap: 0, gia_ban: 0, so_luong_ton: 0, trang_thai: true
  };
}
</script>


<template>
<div class="main-panel">
  <div class="container">
    <div class="page-inner">
      <div class="row">

        <!-- Form sản phẩm -->
        <div class="col-md-12">
          <div class="card">
            <div class="card-header">
              <h4>Quản lý sản phẩm</h4>
            </div>
            <div class="card-body">
              <form class="row g-3">
                <div class="col-md-3">
                  <label>Tên</label>
                  <input v-model="newSanPham.ten" class="form-control" placeholder="Tên sản phẩm" />
                </div>

                <div class="col-md-3">
                  <label>Thương hiệu</label>
                  <select v-model="newSanPham.id_thuong_hieu" class="form-select">
                    <option value="">Chọn thương hiệu</option>
                    <option v-for="h in thuongHieuList" :key="h.id" :value="h.id">{{ h.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Danh mục</label>
                  <select v-model="newSanPham.id_danh_muc" class="form-select">
                    <option value="">Chọn danh mục</option>
                    <option v-for="d in danhMucList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Xuất xứ</label>
                  <select v-model="newSanPham.id_xuat_xu" class="form-select">
                    <option value="">Chọn xuất xứ</option>
                    <option v-for="x in xuatXuList" :key="x.id" :value="x.id">{{ x.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Dề giày</label>
                  <select v-model="newSanPham.id_de_giay" class="form-select">
                    <option value="">Chọn dề giày</option>
                    <option v-for="d in deGiayList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Dây giày</label>
                  <select v-model="newSanPham.id_day_giay" class="form-select">
                    <option value="">Chọn dây giày</option>
                    <option v-for="d in dayGiayList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Mục đích sử dụng</label>
                  <select v-model="newSanPham.id_muc_dich_su_dung" class="form-select">
                    <option value="">Chọn mục đích</option>
                    <option v-for="m in mucDichSuDungList" :key="m.id" :value="m.id">{{ m.ten }}</option>
                  </select>
                </div>

                <div class="col-md-3">
                  <label>Giá nhập</label>
                  <input type="number" v-model.number="newSanPham.gia_nhap" class="form-control"/>
                </div>

                <div class="col-md-3">
                  <label>Giá bán</label>
                  <input type="number" v-model.number="newSanPham.gia_ban" class="form-control"/>
                </div>

                <div class="col-md-3">
                  <label>Số lượng tồn</label>
                  <input type="number" v-model.number="newSanPham.so_luong_ton" class="form-control"/>
                </div>

                <div class="col-md-3">
                  <label>Trạng thái</label>
                  <div class="d-flex mt-1">
                    <div class="form-check me-3">
                      <input type="radio" class="form-check-input" v-model="newSanPham.trang_thai" :value="true" />
                      <label class="form-check-label">Hoạt động</label>
                    </div>
                    <div class="form-check me-3">
                      <input type="radio" class="form-check-input" v-model="newSanPham.trang_thai" :value="false" />
                      <label class="form-check-label">Ngưng hoạt động</label>
                    </div>
                  </div>
                </div>

                <div class="col-md-3 d-flex align-items-end">
                  <button type="button" class="btn btn-warning me-2" @click="addSanPham">Thêm</button>
                  <!-- <button type="button" class="btn btn-dark" @click="editSanPham">Sửa</button> -->
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Bảng danh sách sản phẩm -->
        <div class="col-md-12 mt-3">
          <div class="card">
            <div class="card-header">
              <h4>Danh sách sản phẩm</h4>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-striped">
                  <thead>
                    <tr>
                      <th>STT</th>
                      <th>Tên</th>
                      <th>Thương hiệu</th>
                      <th>Danh mục</th>
                      <th>Xuất xứ</th>
                      <th>Dề giày</th>
                      <th>Dây giày</th>
                      <th>Mục đích</th>
                      <th>Trạng thái</th>
                      <th>Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                     <tr v-for="(sp, index) in listSanPham" :key="sp.id">
                      <td>{{ index + 1 }}</td>
                      <td>{{ sp.ten }}</td>
                      <td>{{ thuongHieuList.find(h => h.id === sp.id_thuong_hieu)?.ten }}</td>
                      <td>{{ danhMucList.find(d => d.id === sp.id_danh_muc)?.ten }}</td>
                      <td>{{ xuatXuList.find(x => x.id === sp.id_xuat_xu)?.ten }}</td>
                      <td>{{ deGiayList.find(d => d.id === sp.id_de_giay)?.ten }}</td>
                      <td>{{ dayGiayList.find(d => d.id === sp.id_day_giay)?.ten }}</td>
                      <td>{{ mucDichSuDungList.find(m => m.id === sp.id_muc_dich_su_dung)?.ten }}</td>
                      <td>{{ sp.trang_thai ? 'Hoạt động' : 'Ngưng hoạt động' }}</td>
                      <td>
                        <button class="btn btn-primary btn-sm me-2" @click="showDetail(sp)">Chi tiết</button>
                        <!-- <button class="btn btn-dark btn-sm me-2" @click="editFromTable(sp)">Sửa</button> -->
                        <button class="btn btn-danger btn-sm" @click="removeSanPham(sp.id)">Xóa</button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal chi tiết sản phẩm -->
        <div v-if="detailForm" class="modal fade show d-block" tabindex="-1">
          <div class="modal-dialog modal-lg">
            <div class="modal-content p-3">
              <div class="modal-header">
                <h5 class="modal-title">Chi tiết sản phẩm</h5>
                <button type="button" class="btn-close" @click="detailForm=false"></button>
              </div>
              <div class="modal-body">
                <div class="row g-3">
                  <div class="col-md-4">
                    <label class="form-label">Tên</label>
                    <input v-model="selectedSanPham.ten" class="form-control"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Thương hiệu</label>
                    <select v-model="selectedSanPham.id_thuong_hieu" class="form-select">
                      <option v-for="h in thuongHieuList" :key="h.id" :value="h.id">{{ h.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Danh mục</label>
                    <select v-model="selectedSanPham.id_danh_muc" class="form-select">
                      <option v-for="d in danhMucList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Xuất xứ</label>
                    <select v-model="selectedSanPham.id_xuat_xu" class="form-select">
                      <option v-for="x in xuatXuList" :key="x.id" :value="x.id">{{ x.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Dề giày</label>
                    <select v-model="selectedSanPham.id_de_giay" class="form-select">
                      <option v-for="d in deGiayList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Dây giày</label>
                    <select v-model="selectedSanPham.id_day_giay" class="form-select">
                      <option v-for="d in dayGiayList" :key="d.id" :value="d.id">{{ d.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Mục đích sử dụng</label>
                    <select v-model="selectedSanPham.id_muc_dich_su_dung" class="form-select">
                      <option v-for="m in mucDichSuDungList" :key="m.id" :value="m.id">{{ m.ten }}</option>
                    </select>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Giá nhập</label>
                    <input type="number" v-model.number="selectedSanPham.gia_nhap" class="form-control"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Giá bán</label>
                    <input type="number" v-model.number="selectedSanPham.gia_ban" class="form-control"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Số lượng tồn</label>
                    <input type="number" v-model.number="selectedSanPham.so_luong_ton" class="form-control"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Trạng thái</label>
                    <div class="d-flex mt-1">
                      <div class="form-check me-3">
                        <input type="radio" class="form-check-input" v-model="selectedSanPham.trang_thai" :value="true" />
                        <label class="form-check-label">Hoạt động</label>
                      </div>
                      <div class="form-check me-3">
                        <input type="radio" class="form-check-input" v-model="selectedSanPham.trang_thai" :value="false" />
                        <label class="form-check-label">Ngưng hoạt động</label>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-secondary" @click="detailForm=false">Đóng</button>
                <button class="btn btn-primary" @click="saveDetail">Lưu</button>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</div>
</template>
