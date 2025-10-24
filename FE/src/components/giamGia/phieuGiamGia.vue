<script setup>
import { onMounted, ref } from 'vue';
import { createPhieuGiamGia, deletePhieuGiamGiaByID, getAllPhieuGiamGia, getPhieuGiamGiaById, updatePhieuGiamGia } from './phieuGiamGiaService.js'
import Decimal from 'decimal.js';

const listPhieuGiamGia = ref([])
const selectedPhieuGiamGia = ref()
const newPhieuGiamGia = ref({ma: '', ten: '', ngayBatDau: Date, ngayKetThuc: Date, hinhThucGiamGia: Boolean, giaTriGiam: Decimal, giaTriGiamToiThieu: Decimal,giaTriGiamToiDa: Decimal, trangThai: Boolean, moTa: ''})

onMounted(async () => {
    await fetchPhieuGiamGia()
})

const fetchPhieuGiamGia = async () =>{
    const res = await getAllPhieuGiamGia()
    listPhieuGiamGia.value = res.data
}

const addPhieuGiamGia = async () => {
    await createPhieuGiamGia(newPhieuGiamGia.value)
    await fetchPhieuGiamGia()
}

const removePhieuGiamGia = async (id) => {
    await deletePhieuGiamGiaByID(id)
    await fetchPhieuGiamGia()
}

const editPhieuGiamGia = async (p) => {
    await updatePhieuGiamGia(p.id, p)
    await fetchPhieuGiamGia()
}

const detailPhieuGiamGia = async(id) => {
    const res = await getPhieuGiamGiaById(id)
    newPhieuGiamGia.value = res.data

}

const formatDate = (isoString) => {
  const date = new Date(isoString);
  return date.toLocaleString('vi-VN', { timeZone: 'Asia/Ho_Chi_Minh' });
};
</script>

<template>
    <div class="main-panel">
    <div class="container">
      <div class="page-inner">
        <div class="row">
          <div class="col-md-12">
            <div class="card">
              <div class="card-header">
                <div class="d-flex align-items-center">
                  <h4 class="card-title">Phiếu giảm giá</h4>
                </div>
              </div>
              <div class="card-body">
                <form>
                  <div class="row g-3">
                    <!-- <div class="col-md-4">
                          <label class="form-label">Tìm kiếm</label>
                          <input
                            type="text"
                            class="form-control"
                            placeholder="Tìm kiếm theo tên sản phẩm..."
                          />
                        </div> -->

                    <div class="col-md-4">
                      <label class="form-label">Tên</label>
                      <input
                      v-model="newPhieuGiamGia.ten"
                            type="text"
                            class="form-control"
                            placeholder="Tên sự kiện"
                          />
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Ngày bắt đầu</label>
                      <input v-model="newPhieuGiamGia.ngayBatDau" class="form-control" type="date">
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Ngày kết thúc</label>
                      <input v-model="newPhieuGiamGia.ngayKetThuc" class="form-control" type="date">
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Hình thức giảm giá</label>
                      <div class="d-flex mt-1">
                        <div class="form-check me-3">
                          <input
                          v-model="newPhieuGiamGia.hinhThucGiamGia"
                            class="form-check-input"
                            type="radio"
                            name="hinhThucGiamGia"
                            value="true"
                            checked
                          />
                          <label class="form-check-label">%</label>
                        </div>
                        <div class="form-check me-3">
                          <input
                          v-model="newPhieuGiamGia.hinhThucGiamGia"
                            class="form-check-input"
                            type="radio"
                            name="hinhThucGiamGia"
                            value="false"
                          />
                          <label class="form-check-label">Số tiền</label>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-4">
                      <label class="form-label">Giá trị giảm</label>
                      <input v-model="newPhieuGiamGia.giaTriGiam" class="form-control" type="number">
                    </div>
                    <div class="col-md-4">
                      <label class="form-label">Giá trị giảm tối thiểu</label>
                      <input v-model="newPhieuGiamGia.giaTriGiamToiThieu" class="form-control" type="number" placeholder="VNĐ">
                      
                    </div>
                    <div class="col-md-4">
                      <label class="form-label">Giá trị giảm tối đa</label>
                      <input v-model="newPhieuGiamGia.giaTriGiamToiDa" class="form-control" type="number" placeholder="VNĐ">
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Trạng thái</label>
                      <div class="d-flex mt-2">
                        <div class="form-check me-3">
                          <input
                            v-model="newPhieuGiamGia.trangThai"
                            class="form-check-input"
                            type="radio"
                            name="status"
                            checked
                            value="true"
                          />
                          <label class="form-check-label">Hoạt động</label>
                        </div>
                        <div class="form-check me-3">
                          <input
                          v-model="newPhieuGiamGia.trangThai"
                            class="form-check-input"
                            type="radio"
                            name="status"
                            value="false"
                          />
                          <label class="form-check-label">Ngưng hoạt động</label>
                        </div>
                      </div>
                    </div>

                    <div class="col-md-4">
                      <label class="form-label">Mô tả</label>
                      <textarea v-model="newPhieuGiamGia.moTa" class="form-control" type="" placeholder="Mô tả"></textarea>
                    </div>
                  </div>

                  <!-- Footer -->
                  <div
                    class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4"
                  >
                    <p class="mb-2 mb-md-0">
                      <!-- Tổng số sản phẩm:
                      <span class="text-success fw-bold">0</span> -->
                    </p>
                    <div>
                      <!-- <button type="button" class="btn btn-secondary me-2">
                            Tải Excel
                          </button> -->
                      <button @click="addPhieuGiamGia" type="button" class="btn btn-warning me-2">
                        Thêm
                      </button>
                      <button @click="editPhieuGiamGia(newPhieuGiamGia)" type="button" class="btn btn-dark">
                        Sửa
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>

          <div class="col-md-12">
            <div class="card">
              <div class="card-header">
                <div class="d-flex align-items-center">
                  <h4 class="card-title">Danh sách phiếu giảm giá</h4>
                  <!-- <button
                        class="btn btn-primary btn-round ms-auto"
                        data-bs-toggle="modal"
                        data-bs-target="#addRowModal"
                      >
                        <i class="fa fa-plus"></i>
                        Add Row
                      </button> -->
                </div>
              </div>
              <div class="card-body">
                <div class="table-responsive">
                  <table
                    id="add-row"
                    class="display table table-striped table-hover"
                  >
                    <thead>
                      <tr>
                        <th>Mã</th>
                        <th>Tên</th>
                        <th>Ngày bắt đầu</th>
                        <th>Ngày kết thúc</th>
                        <th>Giá trị giảm</th>
                        <th>Trạng thái</th>
                        <th style="width: 10%">Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="p in listPhieuGiamGia" :key="p.id">
                        <td>
                          {{ p.ma }}
                        </td>
                        <td>{{p.ten}}</td>
                        <td>{{ formatDate(p.ngayBatDau) }}</td>
                        <td>{{ formatDate(p.ngayKetThuc) }}</td>
                        <td>{{ p.giaTriGiam }} {{ p.hinhThucGiamGia ? '%' : 'VNĐ'}}</td>
                        <td>{{ p.trangThai ? 'Đang hoạt động' : 'Dừng hoạt động'}}</td>
                        <td>
                          <div class="form-button-action">
                            <button
                                @click="detailPhieuGiamGia(p.id)"
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-primary btn-lg"
                              data-original-title="Edit Task"
                            >
                              <i class="fa fa-edit"></i>
                            </button>
                            <button
                            @click="removePhieuGiamGia(p.id)"
                              type="button"
                              data-bs-toggle="tooltip"
                              title=""
                              class="btn btn-link btn-danger"
                              data-original-title="Remove"
                            >
                              <i class="fa fa-times"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>