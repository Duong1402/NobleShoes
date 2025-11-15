<script setup>
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { useNotify } from "@/composables/useNotify";
const notify = useNotify();

import TongQuanThongKe from "./TongQuanThongKe.vue";
import SanPhamBanChay from "./SanPhamBanChay.vue";
import BieuDoTrangThai from "./BieuDoTrangThai.vue";
import TocDoTangTruong from "./TocDoTangTruong.vue";
</script>

<template>
  <div class="thong-ke-container">
    <!-- Tổng quan -->
    <TongQuanThongKe />

    <!-- Bộ lọc -->
    <div class="bo-loc">
      <div class="filter-buttons">
        <button class="active">NGÀY</button>
        <button>TUẦN</button>
        <button>THÁNG</button>
        <button>NĂM</button>
        <button>TÙY CHỈNH</button>
      </div>
      <div class="date-picker">
        <input type="date" />
        <input type="date" />
        <button class="export-btn">EXPORT TO EXCEL</button>
      </div>
    </div>

    <div class="noi-dung">
      <div class="noi-dung-left">
        <SanPhamBanChay />
        <BieuDoTrangThai />
      </div>
      <div class="noi-dung-right">
        <TocDoTangTruong />
      </div>
    </div>
  </div>
</template>

<style scoped>
.thong-ke-container {
  padding: 20px;
}

.bo-loc {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 25px 0;
  flex-wrap: wrap;
}

.filter-buttons button {
  background-color: #f97316;
  color: #fff;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  margin-right: 6px;
  cursor: pointer;
}

.filter-buttons button.active {
  background-color: #ff7f27;
}

.date-picker {
  display: flex;
  align-items: center;
  gap: 10px;
}

.export-btn {
  background: #22c55e;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
}

.noi-dung {
  display: flex;
  gap: 20px;
}

.noi-dung-left {
  flex: 3;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.noi-dung-right {
  flex: 1;
}
</style>
