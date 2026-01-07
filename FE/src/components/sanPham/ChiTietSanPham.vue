<script setup>
import { ref, onMounted } from 'vue'
import { 
  getAllSanPham, getAllChiTietSanPham, createChiTietSanPham, 
  updateChiTietSanPham, deleteChiTietSanPhamByID, 
  getAllMauSac, getAllKichThuoc, getAllChatLieu, getAllDotGiamGia 
} from './sanPhamService.js'

const chiTietSPList = ref([])
const listSanPham = ref([])
const listMauSac = ref([])
const listKichThuoc = ref([])
const listChatLieu = ref([])
const listDotGiamGia = ref([])

const newChiTietSP = ref({
  id_san_pham: '',
  id_mau_sac: '',
  id_kich_thuoc: '',
  id_chat_lieu: '',
  id_dot_giam_gia: '',
  gia_nhap: 0,
  gia_ban: 0,
  so_luong_ton: 0
})

onMounted(async () => {
  await fetchAllData()
})

const fetchAllData = async () => {
  listSanPham.value = await getAllSanPham()
  listMauSac.value = await getAllMauSac()
  listKichThuoc.value = await getAllKichThuoc()
  listChatLieu.value = await getAllChatLieu()
  listDotGiamGia.value = await getAllDotGiamGia()
  await fetchChiTietSP()
}

const fetchChiTietSP = async () => {
  chiTietSPList.value = await getAllChiTietSanPham()
}

const addChiTietSP = async () => {
  await createChiTietSanPham(newChiTietSP.value)
  await fetchChiTietSP()
}

const editChiTietSP = async (ct) => {
  await updateChiTietSanPham(ct.id, ct)
  await fetchChiTietSP()
}

const removeChiTietSP = async (id) => {
  await deleteChiTietSanPhamByID(id)
  await fetchChiTietSP()
}

</script>

<template>
  <div class="container mt-4">
    <h3>Quản lý chi tiết sản phẩm</h3>
    <!-- Form -->
    <form class="row g-3 mb-4">
      <div class="col-md-3">
        <label>Sản phẩm</label>
        <select v-model="newChiTietSP.id_san_pham" class="form-control">
          <option v-for="sp in listSanPham" :key="sp.id" :value="sp.id">{{ sp.ten }}</option>
        </select>
      </div>
      <div class="col-md-2">
        <label>Màu sắc</label>
        <select v-model="newChiTietSP.id_mau_sac" class="form-control">
          <option v-for="m in listMauSac" :key="m.id" :value="m.id">{{ m.ten }}</option>
        </select>
      </div>
      <div class="col-md-2">
        <label>Kích thước</label>
        <select v-model="newChiTietSP.id_kich_thuoc" class="form-control">
          <option v-for="k in listKichThuoc" :key="k.id" :value="k.id">{{ k.ten }}</option>
        </select>
      </div>
      <div class="col-md-2">
        <label>Chất liệu</label>
        <select v-model="newChiTietSP.id_chat_lieu" class="form-control">
          <option v-for="c in listChatLieu" :key="c.id" :value="c.id">{{ c.ten }}</option>
        </select>
      </div>
      <div class="col-md-3">
        <label>Đợt giảm giá</label>
        <select v-model="newChiTietSP.id_dot_giam_gia" class="form-control">
          <option v-for="d in listDotGiamGia" :key="d.id" :value="d.id">{{ d.ten }}</option>
        </select>
      </div>
      <div class="col-md-2">
        <label>Giá nhập</label>
        <input v-model="newChiTietSP.gia_nhap" type="number" class="form-control">
      </div>
      <div class="col-md-2">
        <label>Giá bán</label>
        <input v-model="newChiTietSP.gia_ban" type="number" class="form-control">
      </div>
      <div class="col-md-2">
        <label>Số lượng tồn</label>
        <input v-model="newChiTietSP.so_luong_ton" type="number" class="form-control">
      </div>
      <div class="col-md-6 d-flex align-items-end">
        <button type="button" class="btn btn-warning me-2" @click="addChiTietSP">Thêm</button>
        <button type="button" class="btn btn-dark" @click="editChiTietSP(newChiTietSP)">Sửa</button>
      </div>
    </form>

    <!-- Table -->
    <table class="table table-bordered table-striped">
      <thead>
        <tr>
          <th>Sản phẩm</th>
          <th>Màu sắc</th>
          <th>Kích thước</th>
          <th>Chất liệu</th>
          <th>Đợt giảm giá</th>
          <th>Giá nhập</th>
          <th>Giá bán</th>
          <th>Số lượng tồn</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="ct in chiTietSPList" :key="ct.id">
          <td>{{ listSanPham.find(s => s.id === ct.id_san_pham)?.ten }}</td>
          <td>{{ listMauSac.find(m => m.id === ct.id_mau_sac)?.ten }}</td>
          <td>{{ listKichThuoc.find(k => k.id === ct.id_kich_thuoc)?.ten }}</td>
          <td>{{ listChatLieu.find(c => c.id === ct.id_chat_lieu)?.ten }}</td>
          <td>{{ listDotGiamGia.find(d => d.id === ct.id_dot_giam_gia)?.ten }}</td>
          <td>{{ ct.gia_nhap }}</td>
          <td>{{ ct.gia_ban }}</td>
          <td>{{ ct.so_luong_ton }}</td>
          <td>
            <button class="btn btn-primary btn-sm" @click="newChiTietSP = { ...ct }">Sửa</button>
            <button class="btn btn-danger btn-sm" @click="removeChiTietSP(ct.id)">Xóa</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
