<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import QRCode from "qrcode";
import { getHoaDonById } from "@/service/HoaDonService";

const route = useRoute();
const hoaDon = ref(null);
const qrCodeUrl = ref("");

const formatDateTime = (str) => {
  if (!str) return "";
  return new Date(str).toLocaleString("vi-VN");
};

const generateQr = async (id) => {
  const url = encodeURI(`${window.location.origin}/admin/hoa-don/${id}`);

  qrCodeUrl.value = await QRCode.toDataURL(url, {
    width: 120,
    margin: 1,
  });
};

onMounted(async () => {
  const id = route.params.id;
  if (!id) return;

  const res = await getHoaDonById(id);
  hoaDon.value = res.data;

  await generateQr(id);

  setTimeout(() => {
    window.print();
    window.onafterprint = () => window.close();
  }, 500);
});
</script>
<template>
  <div class="invoice-wrapper" v-if="hoaDon">
    <!-- QR góc phải -->
    <img :src="qrCodeUrl" class="qr" />

    <!-- Header -->
    <div class="header">
      <div class="shop-info">
        <img src="@/assets/img/logoo-Photoroom.png" class="logo" />
        <p class="hotline">Hotline: 0329871234</p>
        <p class="address">Địa chỉ: Trường cao đẳng FPT Polytechnic Hà Nội</p>
      </div>
    </div>

    <h2 class="invoice-title">HÓA ĐƠN BÁN HÀNG</h2>

    <hr />

    <!-- Thông tin -->
    <div class="info">
      <p><b>Mã hóa đơn:</b> {{ hoaDon.ma }}</p>
      <p><b>Ngày:</b> {{ formatDateTime(hoaDon.ngayTao) }}</p>
      <p><b>Khách hàng:</b> {{ hoaDon.tenKhachHang }}</p>
      <p><b>SĐT:</b> {{ hoaDon.sdt }}</p>
      <p><b>Địa chỉ:</b> {{ hoaDon.diaChiGiaoHang }}</p>
    </div>

    <!-- Bảng SP -->
    <table>
      <thead>
        <tr>
          <th>STT</th>
          <th>Tên sản phẩm</th>
          <th>Kích thước</th>
          <th>Màu sắc</th>
          <th>Số lượng</th>
          <th>Đơn giá</th>
          <th>Thành tiền</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(sp, i) in hoaDon.chiTietSanPham" :key="i">
          <td>{{ i + 1 }}</td>
          <td>{{ sp.tenSanPham }}</td>
          <td>{{ sp.size }}</td>
          <td>{{ sp.mauSac }}</td>
          <td class="center">{{ sp.soLuong }}</td>
          <td class="right">{{ sp.donGia.toLocaleString() }}</td>
          <td class="right">{{ sp.thanhTien.toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>

    <!-- Tổng -->
    <div class="total">
      <div class="row">
        <span>Tổng tiền hàng</span>
        <span>{{ hoaDon.tongTien.toLocaleString() }} ₫</span>
      </div>

      <div class="row" v-if="hoaDon.phiVanChuyen">
        <span>Phí ship</span>
        <span>{{ hoaDon.phiVanChuyen.toLocaleString() }} ₫</span>
      </div>

      <div class="row discount" v-if="hoaDon.phieuGiamGiaResponse?.giaTriGiam">
        <span>Giảm giá</span>
        <span>
          -{{ hoaDon.phieuGiamGiaResponse.giaTriGiam.toLocaleString() }} ₫
        </span>
      </div>

      <div class="row total-pay">
        <span>Thanh toán</span>
        <span>{{ hoaDon.tongTienSauGiam.toLocaleString() }} ₫</span>
      </div>
    </div>

    <p class="thanks">❤️ Cảm ơn quý khách ❤️</p>
  </div>
</template>
<style scoped>
/* ===== WRAPPER ===== */
.invoice-wrapper {
  width: 210mm;
  padding: 20px;
  position: relative;
  font-family: "Segoe UI", Arial, sans-serif;
  font-size: 14px;
  color: #000;
}

/* ===== HEADER ===== */
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  gap: 16px;
}

.logo {
  width: 140px;
  height: auto;
  object-fit: contain;
  margin-bottom: 6px;
}

.shop-info {
  max-width: 70%;
}

.hotline,
.address {
  margin: 0;
  font-size: 13px;
  line-height: 1.4;
}

.invoice-title {
  text-align: center;
  font-weight: bold;
  margin: 20px 0 10px;
  letter-spacing: 1px;
}

/* ===== QR ===== */
.qr {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 100px;
  height: 100px;
}

/* ===== INFO ===== */
.info {
  margin: 12px 0;
}

.info p {
  margin: 0;
  line-height: 1.3;
}

/* ===== TABLE ===== */
table {
  width: 100%;
  margin-top: 10px;
  border-collapse: collapse;
}

th,
td {
  padding: 6px;
  border: 1px solid #000;
}

.center {
  text-align: center;
}

.right {
  text-align: right;
}

/* ===== TOTAL ===== */
.total {
  margin-top: 12px;
  width: 320px;
  margin-left: auto;
}

.total .row {
  display: grid;
  grid-template-columns: 140px 1fr;
  padding: 4px 0;
  align-items: center;
}

.total .row span:first-child {
  color: #333;
}

.total .row span:last-child {
  min-width: 120px;
  text-align: right;
}

.total .discount span:last-child {
  color: #000;
}

.total-pay {
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px dashed #000;
  font-size: 16px;
  font-weight: bold;
}

/* ===== FOOTER ===== */
.thanks {
  margin-top: 20px;
  text-align: center;
  font-style: italic;
}

/* ===== PRINT ===== */
@media print {
  body {
    margin: 0;
  }
}
</style>
