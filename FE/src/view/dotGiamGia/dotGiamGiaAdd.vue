<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
import Swal from "sweetalert2";
import {
  createDotGiamGia,
  getAllSanPham,
  updateSanPham,
} from "@/service/dotGiamGiaService";

const router = useRouter();
const notify = useNotify();
const errors = reactive({});
const currentPage = ref(1);
const pageSize = ref(5);
const totalSanPham = ref([]);
const sanPhamList = ref([]);
const form = reactive({
  ma: "",
  ten: null,
  giaTriGiam: 0,
  soTienGiamToiDa: null,
  ngayBatDau: "",
  ngayKetThuc: "",
  trangThai: true,
});
const selectedSanPham = ref([]);

// Lưu sản phẩm (GIỮ NGUYÊN)
// Nhận ID đợt giảm giá làm tham số
const saveSanPham = async (idDotGiamGiaMoi) => {
  try {
    // 1. Lấy danh sách sản phẩm đã chọn (checkbox)
    // Giả sử bạn có biến sanPhamList chứa toàn bộ SP và thuộc tính .selected
    const listCanUpdate = sanPhamList.value.filter((sp) => sp.selected);

    if (listCanUpdate.length === 0) return; // Không có gì để lưu thì thoát

    // 2. Tạo danh sách các Promise để chạy song song (tối ưu tốc độ)
    const updatePromises = listCanUpdate.map((sp) => {
      // Copy đối tượng cũ và gán đợt giảm giá mới
      const payload = {
        ...sp,
        dotGiamGia: { id: idDotGiamGiaMoi }, // Gán ID vừa tạo
      };
      // Gọi API update từng sản phẩm
      return updateSanPham(sp.id, payload);
    });

    // 3. Chờ tất cả cập nhật xong
    await Promise.all(updatePromises);

    console.log(
      `Đã cập nhật ${listCanUpdate.length} sản phẩm vào đợt giảm giá.`
    );
  } catch (err) {
    // Ném lỗi ra ngoài để hàm cha (addDotGiamGia) biết là có lỗi
    console.error("❌ Lỗi khi cập nhật sản phẩm:", err);
    throw new Error("Tạo đợt thành công nhưng lỗi khi thêm sản phẩm vào đợt.");
  }
};

const addDotGiamGia = async () => {
  try {
    // 1. Reset lỗi cũ
    Object.keys(errors).forEach((key) => (errors[key] = ""));

    const payload = {
      ...form,
      trangThai: form.trangThai ? 1 : 0,
    };

    console.log("Dữ liệu gửi đi:", payload);

    // 2. TẠO ĐỢT GIẢM GIÁ TRƯỚC (QUAN TRỌNG)
    const res = await createDotGiamGia(payload);

    // Kiểm tra kết quả trả về
    if (!res || !res.data) {
      throw new Error("Không nhận được phản hồi từ server");
    }

    // 3. Lấy ID từ đợt giảm giá vừa tạo
    const newId = res.data.id;
    console.log("ID đợt giảm giá mới:", newId);

    // 4. GỌI HÀM UPDATE SẢN PHẨM VỚI ID MỚI
    // Lưu ý: Phải dùng await để đợi nó lưu xong mới báo thành công
    if (newId) {
      await saveSanPham(newId);
    }

    // 5. Thông báo & Chuyển trang
    notify.success("Thêm đợt giảm giá và cập nhật sản phẩm thành công!");
    router.push("/admin/dot-giam-gia");
  } catch (error) {
    console.error("❌ Lỗi chi tiết:", error);

    if (error.response) {
      // Log toàn bộ data lỗi từ Backend trả về
      console.log("👉 Data lỗi từ Server:", error.response.data);
      console.log("👉 Status code:", error.response.status);

      if (error.response.status === 400) {
        // Backend thường trả về object lỗi, ví dụ: { ten: "Trống", ngay: "Sai" }
        const errorData = error.response.data;

        // Nếu lỗi là một object chứa nhiều lỗi validation
        if (typeof errorData === "object" && errorData !== null) {
          // Lấy tin nhắn lỗi đầu tiên tìm thấy
          const firstMessage = Object.values(errorData)[0];
          // Hoặc nếu backend trả về field "message"
          const msg =
            errorData.message ||
            errorData.defaultMessage ||
            firstMessage ||
            "Dữ liệu không hợp lệ";
          notify.error("Lỗi: " + msg);
        } else {
          notify.error("Dữ liệu nhập vào không hợp lệ!");
        }
      } else {
        notify.error("Lỗi Server: " + error.response.status);
      }
    } else {
      notify.error("Lỗi kết nối hoặc lỗi code JS: " + error.message);
    }
  }
};

const confirmSave = async () => {
  const result = await Swal.fire({
    title: "Xác nhận thêm đợt giảm giá?",
    text: "Bạn có chắc chắn muốn thêm đợt giảm giá này?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Có, lưu lại",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#ffc107",
    cancelButtonColor: "#6c757d",
  });

  if (result.isConfirmed) {
    await addDotGiamGia();
  }
};

const loadSanPham = async () => {
  try {
    const res = await getAllSanPham();
    totalSanPham.value = res;
    console.log("Danh sách đợt giảm giá:", totalSanPham);
  } catch (err) {
    console.error("Lỗi khi tải danh sách đợt giảm giá:", err);
  }
};

onMounted(async () => {
  await loadSanPham();
});
</script>

<template>
  <div class="container-fluid mt-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-2 px-3">
        <div
          class="page-header d-flex align-items-center justify-content-between"
        >
          <div>
            <h3 class="fw-bold text-warning mb-1">Đợt giảm giá add</h3>
            <Breadcrumb class="mt-1 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow p-4 mt-3">
      <form @submit.prevent="confirmSave">
        <div class="row g-3">
          <!-- Họ tên -->
          <div class="col-md-6">
            <label class="form-label">Tên đợt giảm giá</label>
            <input
              v-model="form.ten"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': errors.ten }"
              placeholder="Nhập họ tên đợt giảm giá"
            />
            <div class="invalid-feedback">{{ errors.ten }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Giá trị giảm</label>
            <div class="input-group">
              <input
                v-model="form.giaTriGiam"
                type="number"
                class="form-control"
                :class="{ 'is-invalid': errors.giaTriGiam }"
              />
              <span class="input-group-text" id="basic-addon2">%</span>
            </div>
            <div class="invalid-feedback">{{ errors.giaTriGiam }}</div>
          </div>

          <div class="col-md-6">
            <label class="form-label">Số tiền giảm tối đa</label>
            <div class="input-group">
              <input
                v-model="form.soTienGiamToiDa"
                type="number"
                class="form-control"
                :class="{ 'is-invalid': errors.soTienGiamToiDa }"
              />
              <span class="input-group-text" id="basic-addon2">VNĐ</span>
            </div>
            <div class="invalid-feedback">{{ errors.soTienGiamToiDa }}</div>
          </div>

          <!-- Ngày bắt đầu -->
          <div class="col-md-6">
            <label class="form-label">Ngày bắt đầu</label>
            <input
              v-model="form.ngayBatDau"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': errors.ngayBatDau }"
            />
            <div class="invalid-feedback">{{ errors.ngayBatDau }}</div>
          </div>

          <!-- Ngày kết thúc -->
          <div class="col-md-6">
            <label class="form-label">Ngày kết thúc</label>
            <input
              v-model="form.ngayKetThuc"
              type="date"
              class="form-control"
              :class="{ 'is-invalid': errors.ngayKetThuc }"
            />
            <div class="invalid-feedback">{{ errors.ngayKetThuc }}</div>
          </div>
        </div>

        <!-- Nút hành động -->
        <div class="mt-4 text-end">
          <router-link to="/admin/dot-giam-gia" class="btn btn-secondary me-2">
            <i class="fa fa-arrow-left me-1"></i> Quay lại
          </router-link>
          <button type="submit" class="btn btn-warning text-white">
            <i class="fa fa-save me-1"></i> Thêm đợt giảm giá
          </button>
        </div>
      </form>
    </div>

    <!-- Danh sách sản phẩm -->
    <div class="card shadow p-4 mt-3">
      <div class="table-responsive">
        <table class="table table-hover align-middle text-center">
          <thead class="table-light">
            <tr>
              <th><input type="checkbox" /></th>
              <th>STT</th>
              <th>Mã</th>
              <th>Tên sản phẩm</th>
              <th>Thương hiệu</th>
              <th>Ngày tạo</th>
              <th>Số lượng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(sp, index) in totalSanPham" :key="sp.id">
              <td>
                <input type="checkbox" :value="sp" v-model="sanPhamSelected" />
              </td>
              <td>{{ index }}</td>
              <td>{{ sp.ma }}</td>
              <td>{{ sp.ten }}</td>
              <td>{{ sp.thuongHieu || "-" }}</td>
              <td>{{ new Date(sp.ngayTao).toLocaleDateString("vi-VN") }}</td>
              <td>{{ sp.soLuongChiTiet }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <nav v-if="totalPages >= 1" aria-label="Page navigation">
        <ul class="pagination justify-content-end mt-3 mb-0">
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
</template>

<style scoped>
.form-label {
  font-weight: 600;
}
.card {
  border-radius: 12px;
}
</style>
