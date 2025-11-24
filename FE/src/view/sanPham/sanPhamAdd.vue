<template>
  <div class="container-fluid mt-4 px-5">
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

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label">Tên sản phẩm</label>
            <input v-model="form.tenSanPham" type="text" class="form-control"
                   placeholder="Nhập tên sản phẩm" required />
          </div>

          <div class="col-md-6">
            <label class="form-label">Mục đích sử dụng</label>
            <select v-model="form.mucDichSuDungId" class="form-select">
              <option disabled value="">-- Chọn mục đích sử dụng --</option>
              <option v-for="md in mucDichList" :key="md.id" :value="md.id">
                {{ md.ten }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Danh mục</label>
            <select v-model="form.danhMucId" class="form-select" required>
              <option disabled value="">-- Chọn danh mục --</option>
              <option v-for="dm in danhMucList" :key="dm.id" :value="dm.id">
                {{ dm.ten }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Thương hiệu</label>
            <select v-model="form.thuongHieuId" class="form-select" required>
              <option disabled value="">-- Chọn thương hiệu --</option>
              <option v-for="th in thuongHieuList" :key="th.id" :value="th.id">
                {{ th.ten }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Đế giày</label>
            <select v-model="form.deGiayId" class="form-select">
              <option disabled value="">-- Chọn đế giày --</option>
              <option v-for="dg in deGiayList" :key="dg.id" :value="dg.id">
                {{ dg.ten }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Dây giày</label>
            <select v-model="form.dayGiayId" class="form-select">
              <option disabled value="">-- Chọn dây giày --</option>
              <option v-for="d in dayGiayList" :key="d.id" :value="d.id">
                {{ d.ten }}
              </option>
            </select>
          </div>

          <div class="col-md-6">
            <label class="form-label">Chất liệu</label>
            <select v-model="form.chatLieuId" class="form-select">
              <option disabled value="">-- Chọn chất liệu --</option>
              <option v-for="cl in chatLieuList" :key="cl.id" :value="cl.id">
                {{ cl.ten }}
              </option>
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
            <textarea v-model="form.moTa" class="form-control" rows="3"
                      placeholder="Nhập mô tả chi tiết sản phẩm"></textarea>
          </div>
        </div>

        <div class="row g-3 mt-4">
          <div class="col-md-6">
            <label class="form-label">Màu sắc</label>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <div v-for="(m, index) in selectedMauSac" :key="index"
                class="d-flex align-items-center gap-1 border rounded px-2 py-1" style="cursor: pointer;">
                <div :style="{ backgroundColor: m.ma, width: '25px', height: '25px', borderRadius: '4px' }"></div>
                <span>{{ m.ten }}</span>
                <button type="button" class="btn btn-sm btn-outline-danger py-0 px-1" @click="removeSelectedColor(index)">x</button>
              </div>
              <button type="button" class="btn btn-outline-primary btn-sm" @click="showColorModal = true">+ Chọn màu</button>
            </div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Kích thước</label>
            <div class="d-flex gap-2 align-items-center flex-wrap">
              <div v-for="(k, index) in selectedKichThuoc" :key="index"
                class="border rounded px-3 py-1 d-flex align-items-center gap-1" style="cursor: pointer;">
                <span>{{ k.ten }}</span>
                <button type="button" class="btn btn-sm btn-outline-danger py-0 px-1" @click="removeSelectedSize(index)">x</button>
              </div>
              <button type="button" class="btn btn-outline-primary btn-sm" @click="showSizeModal = true">+ Chọn kích thước</button>
            </div>
          </div>
        </div>

        <div v-if="showColorModal" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5);">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content p-3">
              <h5 class="text-center fw-bold mb-3">Chọn màu sắc</h5>

              <div class="d-flex flex-wrap justify-content-center gap-2 mb-3">
                <div v-for="m in mauSacList" :key="m.id"
                  class="position-relative p-2 rounded d-flex justify-content-center align-items-center"
                  :style="{
                    backgroundColor: m.ma,
                    width: '80px',
                    height: '40px',
                    cursor: 'pointer',
                    border: selectedMauSac.some(c => c.id === m.id) ? '3px solid #007bff' : '1px solid #ccc'
                  }"
                  @click="selectColor(m)">
                  <button type="button" class="btn btn-sm btn-danger position-absolute top-0 end-0 p-0 px-1"
                    @click.stop="deleteColor(m)">x</button>
                </div>
              </div>

              <div class="text-center">
                <button class="btn btn-secondary me-2" @click="closeColorModal">Đóng</button>
                <button class="btn btn-primary" @click="showAddColorForm = true">Thêm mới</button>
              </div>

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

        <div v-if="showSizeModal" class="modal fade show" style="display: block; background-color: rgba(0,0,0,0.5);">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content p-3">
              <h5 class="text-center fw-bold mb-3">Chọn kích thước</h5>

              <div class="d-flex flex-wrap justify-content-center gap-2 mb-3">
                <div v-for="k in kichThuocList" :key="k.id" class="border rounded px-3 py-2"
                  :class="{ 'border-2 border-primary': selectedKichThuoc.some(s => s.id === k.id) }"
                  style="cursor:pointer" @click="selectSize(k)">
                  {{ k.ten }}
                  <button type="button" class="btn btn-sm btn-danger ms-2 py-0 px-1" @click.stop="deleteSize(k)">x</button>
                </div>
              </div>

              <div class="text-center">
                <button class="btn btn-secondary me-2" @click="closeSizeModal">Đóng</button>
                <button class="btn btn-primary" @click="showAddSizeForm = true">Thêm mới</button>
              </div>

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
import axios from "axios"; // ✅ IMPORT AXIOS (quan trọng nhất)

const router = useRouter();
const notify = useNotify();

// Form chính
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

// Lists
const mucDichList = ref([]);
const danhMucList = ref([]);
const thuongHieuList = ref([]);
const mauSacList = ref([]);
const kichThuocList = ref([]);
const deGiayList = ref([]);
const dayGiayList = ref([]);
const chatLieuList = ref([]);
const xuatXuList = ref([]);

// Selected & variants
const selectedMauSac = ref([]);
const selectedKichThuoc = ref([]);
const bienTheList = ref([]);

// Cloudinary
const CLOUDINARY_URL = "https://api.cloudinary.com/v1_1/dsojjxs1n/upload";
const UPLOAD_PRESET = "unsigned_preset";

// Modal & add form states
const showColorModal = ref(false);
const showSizeModal = ref(false);
const showAddColorForm = ref(false);
const showAddSizeForm = ref(false);

const newColorName = ref("");
const newColorCode = ref("#000000");
const newSizeName = ref("");

// ----------------- Logic Biến Thể (Thông minh hơn) -----------------
const generateVariants = () => {
  if (!selectedMauSac.value.length || !selectedKichThuoc.value.length) {
    // Nếu bỏ chọn hết, có thể xóa hoặc giữ tùy ý. Ở đây xóa cho an toàn logic.
    bienTheList.value = [];
    return;
  }

  // 1. Tạo danh sách biến thể mới dựa trên lựa chọn
  const newVariants = [];
  
  for (let mau of selectedMauSac.value) {
    for (let size of selectedKichThuoc.value) {
      // 2. Kiểm tra xem biến thể này đã tồn tại trong danh sách cũ chưa
      // (Để giữ lại giá, số lượng, ảnh đã nhập)
      const existing = bienTheList.value.find(
        old => old.mauSac.id === mau.id && old.kichThuoc.id === size.id
      );

      if (existing) {
        // Nếu có rồi -> Dùng lại
        newVariants.push(existing);
      } else {
        // Nếu chưa có -> Tạo mới
        newVariants.push({
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
  }
  
  // 3. Cập nhật danh sách
  bienTheList.value = newVariants;
};

// ----------------- Cloudinary upload (Dùng fetch cho Cloudinary là ok vì ko cần token) -----------------
const onImageChange = async (event, bt) => {
  const file = event.target.files[0];
  if (!file) return;
  if (bt.imagePreviews.length >= 3) return alert("Chỉ được tối đa 3 ảnh");

  try {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", UPLOAD_PRESET);

    const res = await fetch(CLOUDINARY_URL, {
      method: "POST",
      body: formData,
    });
    const data = await res.json();

    bt.imagePreviews.push(data.secure_url);
    bt.images.push(data.secure_url);
  } catch (err) {
    console.error("Upload Cloudinary thất bại:", err);
    alert("Upload ảnh thất bại!");
  }
};

const removeImage = (bt, index) => {
  bt.imagePreviews.splice(index, 1);
  bt.images.splice(index, 1);
};

const removeBienThe = (index) => {
  bienTheList.value.splice(index, 1);
};

// ----------------- Load data (Đã sửa dùng AXIOS) -----------------
const loadData = async () => {
  try {
    const [
      mdRes, dmRes, thRes, msRes, ktRes, dgRes, dyRes, clRes, xxRes
    ] = await Promise.all([
      axios.get("http://localhost:8080/admin/muc-dich-su-dung"),
      axios.get("http://localhost:8080/admin/danh-muc"),
      axios.get("http://localhost:8080/admin/thuong-hieu"),
      axios.get("http://localhost:8080/admin/mau-sac"),
      axios.get("http://localhost:8080/admin/kich-thuoc"),
      axios.get("http://localhost:8080/admin/de-giay"),
      axios.get("http://localhost:8080/admin/day-giay"),
      axios.get("http://localhost:8080/admin/chat-lieu"),
      axios.get("http://localhost:8080/admin/xuat-xu"),
    ]);

    // Axios trả data trong .data
    mucDichList.value = mdRes.data;
    danhMucList.value = dmRes.data;
    thuongHieuList.value = thRes.data;
    mauSacList.value = msRes.data;
    kichThuocList.value = ktRes.data;
    deGiayList.value = dgRes.data;
    dayGiayList.value = dyRes.data;
    chatLieuList.value = clRes.data;
    xuatXuList.value = xxRes.data;
  } catch (err) {
    console.error("Lỗi load dữ liệu:", err);
    notify.error("Lỗi tải danh mục, vui lòng thử lại.");
  }
};

// ----------------- Thêm sản phẩm (Đã sửa dùng AXIOS) -----------------
const addSanPham = async () => {
  try {
    const payload = {
      ten: form.tenSanPham,
      idDanhMuc: form.danhMucId,
      idThuongHieu: form.thuongHieuId,
      idMucDichSuDung: form.mucDichSuDungId,
      idDeGiay: form.deGiayId,
      idDayGiay: form.dayGiayId,
      idXuatXu: form.xuatXuId,
      moTa: form.moTa,
      chiTietSanPham: bienTheList.value.map(bt => ({
        ten: bt.ten,
        idMauSac: bt.mauSac.id,
        idKichThuoc: bt.kichThuoc.id,
        idChatLieu: form.chatLieuId,
        giaBan: bt.gia,
        soLuongTon: bt.soLuong,
        images: bt.images
      }))
    };

    // Dùng axios.post
    await axios.post("http://localhost:8080/admin/san-pham", payload);

    notify.success("Thêm sản phẩm thành công!");
    router.push("/admin/san-pham");
  } catch (err) {
    console.error(err);
    const msg = err.response?.data?.message || "Thêm thất bại, vui lòng thử lại!";
    notify.error(msg);
  }
};

// ----------------- Validate & confirm -----------------
const validateForm = () => {
  if (!form.tenSanPham.trim()) { notify.error("Tên sản phẩm không được để trống!"); return false; }
  if (!form.danhMucId) { notify.error("Vui lòng chọn danh mục!"); return false; }
  if (!form.thuongHieuId) { notify.error("Vui lòng chọn thương hiệu!"); return false; }
  if (!bienTheList.value.length) { notify.error("Vui lòng chọn ít nhất 1 màu sắc và kích thước!"); return false; }
  
  for (const bt of bienTheList.value) {
    if (bt.gia <= 0) { notify.error(`Giá sản phẩm "${bt.ten}" phải lớn hơn 0!`); return false; }
    if (bt.soLuong <= 0) { notify.error(`Số lượng sản phẩm "${bt.ten}" phải lớn hơn 0!`); return false; }
  }
  return true;
};

const confirmSave = async () => {
  if (!validateForm()) return;
  const result = await Swal.fire({
    title: "Xác nhận thêm sản phẩm?",
    text: "Bạn có chắc chắn muốn thêm sản phẩm này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    await addSanPham();
  }
};

// ----------------- Thêm màu/kích thước (Sửa dùng AXIOS) -----------------
const addNewColor = async () => {
  if (!newColorName.value.trim()) return alert("Vui lòng nhập tên màu!");
  try {
    await axios.post("http://localhost:8080/admin/mau-sac", {
      ten: newColorName.value,
      ma: newColorCode.value,
    });
    await loadData();
    newColorName.value = "";
    newColorCode.value = "#000000";
    showAddColorForm.value = false;
  } catch (err) {
    console.error(err);
    alert("Không thể thêm màu mới!");
  }
};

const addNewSize = async () => {
  if (!newSizeName.value.trim()) return alert("Vui lòng nhập tên kích thước!");
  try {
    await axios.post("http://localhost:8080/admin/kich-thuoc", {
      ten: newSizeName.value,
    });
    await loadData();
    newSizeName.value = "";
    showAddSizeForm.value = false;
  } catch (err) {
    console.error(err);
    alert("Không thể thêm kích thước mới!");
  }
};

// ----------------- Xóa màu/kích thước (Sửa dùng AXIOS) -----------------
const deleteColor = async (mau) => {
  if (!confirm(`Bạn có chắc muốn xóa màu "${mau.ten}"?`)) return;
  try {
    await axios.delete(`http://localhost:8080/admin/mau-sac/${mau.id}`);
    await loadData();
    selectedMauSac.value = selectedMauSac.value.filter(c => c.id !== mau.id);
    generateVariants();
  } catch (err) {
    console.error(err);
    alert("Không thể xóa màu!");
  }
};

const deleteSize = async (size) => {
  if (!confirm(`Bạn có chắc muốn xóa kích thước "${size.ten}"?`)) return;
  try {
    await axios.delete(`http://localhost:8080/admin/kich-thuoc/${size.id}`);
    await loadData();
    selectedKichThuoc.value = selectedKichThuoc.value.filter(s => s.id !== size.id);
    generateVariants();
  } catch (err) {
    console.error(err);
    alert("Không thể xóa kích thước!");
  }
};

// ----------------- Logic chọn màu/size -----------------
const selectColor = (mau) => {
  const exists = selectedMauSac.value.find(c => c.id === mau.id);
  if (exists) {
    selectedMauSac.value = selectedMauSac.value.filter(c => c.id !== mau.id);
  } else {
    selectedMauSac.value.push(mau);
  }
  generateVariants();
};

const selectSize = (size) => {
  const exists = selectedKichThuoc.value.find(k => k.id === size.id);
  if (exists) {
    selectedKichThuoc.value = selectedKichThuoc.value.filter(k => k.id !== size.id);
  } else {
    selectedKichThuoc.value.push(size);
  }
  generateVariants();
};

const removeSelectedColor = (i) => {
  selectedMauSac.value.splice(i, 1);
  generateVariants();
};
const removeSelectedSize = (i) => {
  selectedKichThuoc.value.splice(i, 1);
  generateVariants();
};

const closeColorModal = () => { showColorModal.value = false; showAddColorForm.value = false; };
const closeSizeModal = () => { showSizeModal.value = false; showAddSizeForm.value = false; };

// ----------------- Khởi tạo -----------------
onMounted(loadData);

</script>

<style scoped>
.form-label { font-weight: 600; }
.card { border-radius: 12px; }
/* Fix lỗi modal đè nhau nếu dùng Bootstrap */
/* .modal { display: block; } <-- XÓA DÒNG NÀY ĐI NẾU DÙNG v-if */
</style>