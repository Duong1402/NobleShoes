<script setup>
import { Pie } from "vue-chartjs";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from "chart.js";
ChartJS.register(Title, Tooltip, Legend, ArcElement);

const data = {
  labels: ["Đã hủy", "Chờ xác nhận", "Chờ giao hàng", "Hoàn thành", "Trả hàng"],
  datasets: [
    {
      data: [7.14, 14.29, 14.29, 42.86, 14.29],
      backgroundColor: ["#ef4444", "#facc15", "#3b82f6", "#22c55e", "#a855f7"],
    },
  ],
};

const options = {
  responsive: true,
  plugins: { legend: { position: "right" } },
};
</script>

<template>
  <div class="bieu-do">
    <h3>Biểu đồ trạng thái tùy chỉnh</h3>
    <Pie :data="data" :options="options" />
  </div>
</template>

<style scoped>
.bieu-do {
  background: white;
  padding: 16px;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}
h3 {
  margin-bottom: 10px;
  color: #f97316;
}
</style>

