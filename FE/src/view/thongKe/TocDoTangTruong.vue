<script setup>
const metrics = [
  { name: "Doanh thu ngày", value: "6,040,253 ₫", change: "+53.73%" },
  { name: "Doanh thu tuần", value: "8,834,904 ₫", change: "+100%" },
  { name: "Doanh thu tháng", value: "8,834,904 ₫", change: "+100%" },
  { name: "Đơn hàng ngày", value: "2", change: "-81.82%" },
];
</script>

<template>
  <div class="toc-do">
    <h3>Tốc độ tăng trưởng của cửa hàng</h3>
    <div v-for="(m, i) in metrics" :key="i" class="metric">
      <div>
        <strong>{{ m.name }}</strong>
        <span class="value">{{ m.value }}</span>
      </div>
      <span
        :class="{
          up: m.change.startsWith('+'),
          down: m.change.startsWith('-'),
        }"
      >
        {{ m.change }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.toc-do {
  background: #111827;
  color: white;
  border-radius: 10px;
  padding: 16px;
  height: 100%;
}
.metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #374151;
  padding: 8px 0;
}
.value {
  display: block;
  font-size: 14px;
}
.up {
  color: #22c55e;
}
.down {
  color: #ef4444;
}
</style>
