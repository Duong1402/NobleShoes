<script setup>
import { computed } from 'vue';

const props = defineProps({
  // truyền summary từ parent nếu có; mình để default mock giống trước
  summary: {
    type: Object,
    default: () => ({
      today: { revenue: 6040253, products: 21, success: 2, canceled: 0, returned: 1 },
      week: { revenue: 8834904, products: 37, success: 6, canceled: 1, returned: 2 },
      month: { revenue: 8834904, products: 37, success: 6, canceled: 1, returned: 2 },
      year: { revenue: 8834904, products: 37, success: 6, canceled: 1, returned: 2 },
    })
  }
});

const cards = computed(() => [
  { key: 'today', title: 'Hôm nay', colorFrom: '#075985', colorTo: '#0ea5a4', icon: '🗓️', data: props.summary.today },
  { key: 'week',  title: 'Tuần này', colorFrom: '#c2410c', colorTo: '#fb923c', icon: '📦', data: props.summary.week },
  { key: 'month', title: 'Tháng này', colorFrom: '#1e3a8a', colorTo: '#3b82f6', icon: '🗓️', data: props.summary.month },
  { key: 'year',  title: 'Năm nay', colorFrom: '#065f46', colorTo: '#10b981', icon: '📊', data: props.summary.year },
]);

function formatCurrency(v) {
  if (typeof v !== 'number') return v;
  return new Intl.NumberFormat('vi-VN').format(v) + ' đ';
}
</script>

<template>
  <div class="tong-quan-grid-2x2">
    <div
      v-for="card in cards"
      :key="card.key"
      class="tg-card"
      :style="{
        backgroundImage: `linear-gradient(135deg, ${card.colorFrom}, ${card.colorTo})`
      }"
    >
      <div class="tg-card-top">
        <div class="tg-left">
          <div class="icon-circle" aria-hidden="true">{{ card.icon }}</div>
          <div class="tg-title">{{ card.title }}</div>
        </div>
        <!-- optional badge / small note could go here -->
      </div>

      <div class="tg-amount">{{ formatCurrency(card.data.revenue) }}</div>

      <div class="tg-meta">
        <div class="meta-item">
          <div class="meta-num">{{ card.data.products }}</div>
          <div class="meta-label">Sản phẩm</div>
        </div>

        <div class="meta-item">
          <div class="meta-num">{{ card.data.success }}</div>
          <div class="meta-label">Đơn thành công</div>
        </div>

        <div class="meta-item">
          <div class="meta-num">{{ card.data.canceled }}</div>
          <div class="meta-label">Đơn hủy</div>
        </div>

        <div class="meta-item">
          <div class="meta-num">{{ card.data.returned }}</div>
          <div class="meta-label">Đơn trả</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Grid: 2 columns always on desktop, 1 column on small screens */
.tong-quan-grid-2x2 {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  align-items: stretch;
}

/* card */
.tg-card {
  color: #fff;
  border-radius: 12px;
  padding: 16px;
  position: relative;
  overflow: hidden;
  min-height: 160px;
  box-shadow: 0 6px 18px rgba(2,6,23,0.12);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* top row: left (icon + title) */
.tg-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* left group */
.tg-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* circular icon like in image */
.icon-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  background: rgba(255,255,255,0.16);
  border-radius: 50%;
  font-size: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
}

/* small title text */
.tg-title {
  font-size: 14px;
  font-weight: 700;
  opacity: 0.95;
  text-transform: uppercase;
  letter-spacing: 0.6px;
}

/* big amount */
.tg-amount {
  font-size: 22px;
  font-weight: 800;
  margin-top: 10px;
  margin-bottom: 8px;
  line-height: 1;
  text-shadow: 0 1px 2px rgba(0,0,0,0.18);
}

/* meta row (four small stats) */
.tg-meta {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: space-between;
  margin-top: 6px;
}

/* each small stat block */
.meta-item {
  background: rgba(255,255,255,0.08);
  padding: 8px 10px;
  border-radius: 8px;
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* number (big) and label (small) */
.meta-num {
  font-weight: 700;
  font-size: 14px;
  line-height: 1;
}
.meta-label {
  font-size: 11px;
  opacity: 0.95;
  margin-top: 4px;
  text-align: center;
}

/* responsive adjustments */
@media (max-width: 900px) {
  .tong-quan-grid-2x2 {
    grid-template-columns: 1fr;
  }
  .tg-card {
    min-height: 140px;
  }
  .tg-meta { gap: 8px; }
}
</style>
