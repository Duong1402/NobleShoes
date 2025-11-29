<template>
  <div class="container-fluid mt-4 px-5">
    <!-- Tiêu đề -->
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-warning mb-1">Quản lý sản phẩm</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <!-- Form thêm sản phẩm -->
    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="row g-3">
          <!-- Các thông tin cơ bản -->
          <div class="col-md-6">
            <label class="form-label">Tên sản phẩm</label>
            <input v-model="form.tenSanPham" type="text" class="form-control" placeholder="Nhập tên sản phẩm" required />
          </div>

          <div class="col-md-6">
            <label class="form-label">Mục đích sử dụng</label>
            <select v-model="form.mucDichSuDungId" class="form-select">
              <option disabled value="">-- Chọn mục đích sử dụng --</option>
              <option v-for="md in mucDichList" :key="md.id" :value="md.id">{{ md.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Danh mục</label>
            <select v-model="form.danhMucId" class="form-select" required>
              <option disabled value="">-- Chọn danh mục --</option>
              <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">{{ dm.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Thương hiệu</label>
            <select v-model="form.thuongHieuId" class="form-select" required>
              <option disabled value="">-- Chọn thương hiệu --</option>
              <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">{{ th.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Đế giày</label>
            <select v-model="form.deGiayId" class="form-select">
              <option disabled value="">-- Chọn đế giày --</option>
              <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">{{ dg.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Dây giày</label>
            <select v-model="form.dayGiayId" class="form-select">
              <option disabled value="">-- Chọn dây giày --</option>
              <option v-for="d in dayGiayList" :key="d.id" :value="d.id">{{ d.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Chất liệu</label>
            <select v-model="form.chatLieuId" class="form-select">
              <option disabled value="">-- Chọn chất liệu --</option>
              <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">{{ cl.ten }}</option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Xuất xứ</label>
            <select v-model="form.xuatXuId" class="form-select">
              <option disabled value="">-- Chọn xuất xứ --</option>
              <option v-for="xx in xuatXuList" :key="xx.id" :value="xx.id">{{ xx.ten }}</option>
            </select>
          </div>

          <div class="col-12">
            <label class="form-label">Mô tả sản phẩm</label>
            <textarea v-model="form.moTa" class="form-control" rows="3" placeholder="Nhập mô tả chi tiết sản phẩm"></textarea>
          </div>
        </div>

        <!-- Màu sắc & kích thước -->
        <div class="row g-3 mt-4">
          <!-- Màu sắc -->
          <div class="col-md-6">
            <label class="form-label">Màu sắc</label>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <div v-for="(m, index) in selectedMauSac" :key="index"
                class="d-flex align-items-center gap-1 border rounded px-2 py-1" style="cursor: pointer;">
                <div :style="{ backgroundColor: m.ma, width: '25px', height: '25px', borderRadius: '4px' }"></div>
                <span>{{ m.ten }}</span>
                <button type="button" class="btn btn-sm btn-outline-danger" @click="removeSelectedColor(index)">x</button>
              </div>
              <button type="button" class="btn btn-outline-primary btn-sm" @click="showColorModal = true">+ Chọn màu</button>
            </div>
          </div>

          <!-- Kích thước -->
          <div class="col-md-6">
            <label class="form-label">Kích thước</label>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <div v-for="(k, index) in selectedKichThuoc" :key="index"
                class="border rounded px-3 py-1 d-flex align-items-center gap-1" style="cursor: pointer;">
                <span>{{ k.ten }}</span>
                <button type="button" class="btn btn-sm btn-outline-danger" @click="removeSelectedSize(index)">x</button>
              </div>
              <button type="button" class="btn btn-outline-primary btn-sm" @click="showSizeModal = true">+ Chọn kích thước</button>
            </div>
          </div>
        </div>

        <!-- ===== MODAL CHỌN MÀU ===== -->
        <div v-if="showColorModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.4);">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content p-3">
              <h5 class="text-center fw-bold mb-3">Chọn màu sắc</h5>

              <!-- Danh sách màu -->
              <div class="d-flex flex-wrap justify-content-center gap-2 mb-3">
                <div v-for="m in mauSacList" :key="m.id"
                  class="position-relative p-2 rounded d-flex justify-content-center align-items-center"
                  :style="{ backgroundColor: m.ma, width: '80px', height: '40px', cursor: 'pointer', border: '1px solid #ccc' }"
                  @click="selectColor(m)">
                  <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0 p-0 px-1"
                    @click.stop="deleteColor(m)">x</button>
                </div>
              </div>

              <div class="text-center">
                <button class="btn btn-secondary me-2" @click="closeColorModal">Đóng</button>
                <button class="btn btn-primary" @click="showAddColorForm = true">Thêm mới</button>
              </div>

              <!-- Form thêm màu -->
              <div v-if="showAddColorForm" class="border rounded p-3 mt-3">
                <h6 class="fw-bold text-center mb-2">Thêm màu mới</h6>
                <div class="d-flex gap-2 mb-2">
                  <input v-model="newColorName" type="text" class="form-control" placeholder="Tên màu" />
                  <input v-model="newColorCode" type="color" class="form-control form-control-color" />
                </div>
                <div class="text-end">
                  <button class="btn btn-success btn-sm me-2" @click="addNewColor">Lưu</button>
                  <button class="btn btn-outline-secondary btn-sm" @click="showAddColorForm = false">Hủy</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ===== MODAL CHỌN KÍCH THƯỚC ===== -->
        <div v-if="showSizeModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.4);">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content p-3">
              <h5 class="text-center fw-bold mb-3">Chọn kích thước</h5>

              <div class="d-flex flex-wrap justify-content-center gap-2 mb-3">
                <div v-for="k in kichThuocList" :key="k.id" class="border rounded px-3 py-2"
                  style="cursor:pointer" @click="selectSize(k)">
                  {{ k.ten }}
                  <button type="button" class="btn btn-sm btn-danger ms-2 py-0 px-1" @click.stop="deleteSize(k)">x</button>
                </div>
              </div>

              <div class="text-center">
                <button class="btn btn-secondary me-2" @click="closeSizeModal">Đóng</button>
                <button class="btn btn-primary" @click="showAddSizeForm = true">Thêm mới</button>
              </div>

              <!-- Form thêm kích thước -->
              <div v-if="showAddSizeForm" class="border rounded p-3 mt-3">
                <h6 class="fw-bold text-center mb-2">Thêm kích thước mới</h6>
                <div class="d-flex gap-2 mb-2">
                  <input v-model="newSizeName" type="text" class="form-control" placeholder="Tên kích thước" />
                </div>
                <div class="text-end">
                  <button class="btn btn-success btn-sm me-2" @click="addNewSize">Lưu</button>
                  <button class="btn btn-outline-secondary btn-sm" @click="showAddSizeForm = false">Hủy</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Biến thể -->
        <div v-if="bienTheList.length" class="mt-4">
          <h5 class="fw-bold text-secondary mb-3">Danh sách sản phẩm biến thể</h5>
          <div class="table-responsive">
            <table class="table table-bordered align-middle">
              <thead class="table-light">
                <tr>
                  <th>STT</th>
                  <th>Tên sản phẩm</th>
                  <th>Màu sắc</th>
                  <th>Kích cỡ</th>
                  <th>Giá</th>
                  <th>Số lượng</th>
                  <th>Ảnh (tối đa 3)</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(bt, index) in bienTheList" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td><input v-model="bt.ten" type="text" class="form-control" /></td>
                  <td>{{ bt.mauSac.ten }}</td>
                  <td>{{ bt.kichThuoc.ten }}</td>
                  <td><input v-model.number="bt.gia" type="number" class="form-control" min="0" /></td>
                  <td><input v-model.number="bt.soLuong" type="number" class="form-control" min="0" /></td>
                  <td>
                    <div class="d-flex flex-wrap gap-2">
                      <div v-for="(img, i) in bt.imagePreviews" :key="i" class="position-relative">
                        <img :src="img" width="70" height="70" class="rounded shadow" />
                        <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0"
                          @click="removeImage(bt, i)">x</button>
                      </div>
                      <label v-if="bt.imagePreviews.length < 3"
                        class="border rounded d-flex justify-content-center align-items-center"
                        style="width: 70px; height: 70px; cursor: pointer;">
                        <input type="file" accept="image/*" @change="onImageChange($event, bt)" hidden />
                        <span class="text-muted">+</span>
                      </label>
                    </div>
                  </td>
                  <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" type="button" @click="removeBienThe(index)">❌</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Nút lưu -->
        <div class="mt-4 text-end">
          <router-link to="/admin/san-pham" class="btn btn-secondary me-2">
            <i class="fa fa-arrow-left me-1"></i> Quay lại
          </router-link>
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm sản phẩm
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";

const router = useRouter();
const notify = useNotify();

/* --- FORM CHÍNH --- */
const form = reactive({
  tenSanPham: "",
  mucDichSuDungId: "",
  danhMucId: "",
  thuongHieuId: "",
  deGiayId: "",
  dayGiayId: "",
  moTa: "",
  chatLieuId: "",
  xuatXuId: "",
});

/* --- DỮ LIỆU --- */
const mucDichList = ref([]);
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const mauSacList = ref([]);
const kichThuocList = ref([]);
const deGiayList = ref([]);
const dayGiayList = ref([]);
const chatLieuList = ref([]);
const xuatXuList = ref([]);

const selectedMauSac = ref([]);
const selectedKichThuoc = ref([]);
const bienTheList = ref([]);

/* --- CLOUDINARY --- */
const CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dsojjxs1n/upload";
const UPLOAD_PRESET = "unsigned_preset";

/* --- SINH BIẾN THỂ --- */
const generateVariants = () => {
  bienTheList.value = [];
  for (let mau of selectedMauSac.value) {
    for (let size of selectedKichThuoc.value) {
      bienTheList.value.push({
        ten: `${form.tenSanPham || "Sản phẩm"} - ${mau.ten} - ${size.ten}`,
        mauSac: mau,
        kichThuoc: size,
        gia: 0,
        soLuong: 0,
        images: [],
        imagePreviews: [],
      });
    }
  }
};

/* --- UPLOAD ẢNH --- */
const onImageChange = async (event, bt) => {
  const file = event.target.files[0];
  if (!file) return;
  if (bt.imagePreviews.length >= 3) return alert("Chỉ được tối đa 3 ảnh");
  try {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", UPLOAD_PRESET);
    const res = await fetch(CLOUDINARY_URL, { method: "POST", body: formData });
    const data = await res.json();
    bt.imagePreviews.push(data.secure_url);
    bt.images.push(data.secure_url);
  } catch (err) {
    console.error("Upload Cloudinary thất bại:", err);
    alert("Upload ảnh thất bại!");
  }
};

/* --- XÓA ẢNH / BIẾN THỂ --- */
const removeImage = (bt, i) => {
  bt.imagePreviews.splice(i, 1);
  bt.images.splice(i, 1);
};
const removeBienThe = (i) => bienTheList.value.splice(i, 1);

/* --- LOAD DỮ LIỆU --- */
const loadData = async () => {
  try {
    const [md, dm, th, ms, kt, dg, dy, cl, xx] = await Promise.all([
      fetch("http://localhost:8080/admin/muc-dich-su-dung"),
      fetch("http://localhost:8080/admin/danh-muc"),
      fetch("http://localhost:8080/admin/thuong-hieu"),
      fetch("http://localhost:8080/admin/mau-sac"),
      fetch("http://localhost:8080/admin/kich-thuoc"),
      fetch("http://localhost:8080/admin/de-giay"),
      fetch("http://localhost:8080/admin/day-giay"),
      fetch("http://localhost:8080/admin/chat-lieu"),
      fetch("http://localhost:8080/admin/xuat-xu"),
    ]);
    mucDichList.value = await md.json();
    danhMucList.value = await dm.json();
    thuongHieuList.value = await th.json();
    mauSacList.value = await ms.json();
    kichThuocList.value = await kt.json();
    deGiayList.value = await dg.json();
    dayGiayList.value = await dy.json();
    chatLieuList.value = await cl.json();
    xuatXuList.value = await xx.json();
  } catch (err) {
    console.error(err);
  }
};
onMounted(loadData);

/* --- MODAL MÀU & SIZE --- */
const showColorModal = ref(false);
const showSizeModal = ref(false);
const showAddColorForm = ref(false);
const showAddSizeForm = ref(false);

const closeColorModal = () => { showColorModal.value = false; showAddColorForm.value = false; };
const closeSizeModal = () => { showSizeModal.value = false; showAddSizeForm.value = false; };

/* --- THÊM/XÓA MÀU & SIZE --- */
const newColorName = ref("");
const newColorCode = ref("#000000");
const newSizeName = ref("");

const selectColor = (mau) => {
  if (!selectedMauSac.value.find(c => c.id === mau.id)) {
    selectedMauSac.value.push(mau);
    generateVariants();
  }
  showColorModal.value = false;
};
const selectSize = (size) => {
  if (!selectedKichThuoc.value.find(k => k.id === size.id)) {
    selectedKichThuoc.value.push(size);
    generateVariants();
  }
  showSizeModal.value = false;
};
const addNewColor = () => {
  if (!newColorName.value.trim()) return alert("Vui lòng nhập tên màu!");
  const newColor = { id: Date.now(), ten: newColorName.value.trim(), ma: newColorCode.value };
  mauSacList.value.push(newColor);
  newColorName.value = ""; newColorCode.value = "#000000";
  showAddColorForm.value = false;
};
const addNewSize = () => {
  if (!newSizeName.value.trim()) return alert("Vui lòng nhập tên kích thước!");
  const newSize = { id: Date.now(), ten: newSizeName.value.trim() };
  kichThuocList.value.push(newSize);
  newSizeName.value = ""; showAddSizeForm.value = false;
};

/* --- XÓA --- */
const deleteColor = (m) => { mauSacList.value = mauSacList.value.filter(x => x.id !== m.id); };
const deleteSize = (k) => { kichThuocList.value = kichThuocList.value.filter(x => x.id !== k.id); };
const removeSelectedColor = (i) => { selectedMauSac.value.splice(i, 1); generateVariants(); };
const removeSelectedSize = (i) => { selectedKichThuoc.value.splice(i, 1); generateVariants(); };

/* --- LƯU --- */
const confirmSave = () => {
  Swal.fire({
    title: "Xác nhận thêm sản phẩm?",
    text: "Bạn có chắc chắn muốn lưu sản phẩm này?",
    icon: "question",
    showCancelButton: true,
    confirmButtonText: "Lưu",
    cancelButtonText: "Hủy",
  }).then((r) => {
    if (r.isConfirmed) saveSanPham();
  });
};
const saveSanPham = async () => {
  try {
    const res = await fetch("http://localhost:8080/admin/san-pham", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        ...form,
        bienTheList: bienTheList.value.map(bt => ({
          ten: bt.ten,
          mauSacId: bt.mauSac.id,
          kichThuocId: bt.kichThuoc.id,
          gia: bt.gia,
          soLuong: bt.soLuong,
          images: bt.images,
        })),
      }),
    });
    if (res.ok) {
      notify.success("Thêm sản phẩm thành công!");
      router.push("/admin/san-pham");
    } else throw new Error("Thêm thất bại");
  } catch (err) {
    console.error(err);
    notify.error("Có lỗi xảy ra khi lưu sản phẩm!");
  }
};
</script>

<style scoped>
.modal-content {
  border-radius: 15px;
}
</style>
