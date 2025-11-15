<script setup>
import { ref, computed } from 'vue';
import Breadcrumb from "@/components/common/Breadcrumb.vue";
import { Doughnut } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale,
} from 'chart.js';


ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale);


const selectedFilter = ref('NGÀY'); 
const showCustomCard = ref(false);
const customStartDate = ref('');
const customEndDate = ref('');
const itemsPerPage = ref(5); 
const itemsPerPageLowStock = ref(5);

const selectFilter = (filter) => {
  selectedFilter.value = filter;
  showCustomCard.value = filter === 'TÙY CHỈNH';
  
};

const overviewStats = ref({
  HOM_NAY: {
    title: "Hôm nay",
    total: "0 ₫",
    sanPham: 0,
    donThanhCong: 0,
    donHuy: 0,
    donTra: 0,
    colorClass: "bg-info",
    icon: "fa-regular fa-calendar-check"
  },
  TUAN_NAY: {
    title: "Tuần này",
    total: "2,794,651 ₫",
    sanPham: 16,
    donThanhCong: 4,
    donHuy: 1,
    donTra: 1,
    colorClass: "bg-warning",
    icon: "fa-regular fa-calendar-minus"
  },
  THANG_NAY: {
    title: "Tháng này",
    total: "8,834,904 ₫",
    sanPham: 37,
    donThanhCong: 6,
    donHuy: 1,
    donTra: 2,
    colorClass: "bg-primary",
    icon: "fa-calendar-alt"
  },
  NAM_NAY: {
    title: "Năm nay",
    total: "8,834,904 ₫",
    sanPham: 37,
    donThanhCong: 6,
    donHuy: 1,
    donTra: 2,
    colorClass: "bg-success",
    icon: "fa-calendar"
  },
  TUY_CHINH: {
    title: "Tùy chỉnh",
    total: "19,267,000 ₫",
    sanPham: 25,
    donThanhCong: 6,
    donHuy: 1,
    donTra: 2,
    colorClass: "bg-info", 
    icon: "fa-edit"
  }
});


const bestSellingProducts = ref([
  { id: 1, img: '/src/assets/img/logoWeb.png', name: 'Giày tây - Nâu - Da bò', sold: 18, price: '550,000 ₫' },
  { id: 2, img: '/src/assets/img/logoWeb.png', name: 'Giày boot - Đen - Da cá sấu', sold: 18, price: '950,000 ₫' },
  { id: 3, img: '/src/assets/img/logoWeb.png', name: 'Giày lười - Xanh đen - Da bò', sold: 15, price: '490,000 ₫' },
]);


const lowStockProducts = ref([
  { id: 1, img: '/src/assets/img/logoWeb.png', name: 'Giày tây - Đen - Da cá sấu', stock: 8, price: '500,000 ₫' },
  { id: 2, img: '/src/assets/img/logoWeb.png', name: 'Giày boot Vip - Vàng - Da cá sấu thượng hạng', stock: 5, price: '1,000,000 ₫' },
]);


const growthStats = ref([
  { label: 'Doanh thu ngày', value: '6,040,253 ₫', percentage: 53.73, trend: 'up' },
  { label: 'Doanh thu Tuần', value: '8,834,904 ₫', percentage: 100, trend: 'up' },
  { label: 'Doanh thu Tháng', value: '8,834,904 ₫', percentage: 100, trend: 'up' },
  { label: 'Doanh thu năm', value: '8,834,904 ₫', percentage: 100, trend: 'up' },
  { label: 'Đơn hàng ngày', value: '2', percentage: 81.82, trend: 'down' },
  { label: 'Đơn hàng tuần', value: '6', percentage: 100, trend: 'up' },
  { label: 'Đơn hàng tháng', value: '6', percentage: 100, trend: 'up' },
  { label: 'Đơn hàng năm', value: '6', percentage: 100, trend: 'up' },
  { label: 'Sản phẩm ngày', value: '21', percentage: 23.81, trend: 'up' },
  { label: 'Sản phẩm tuần', value: '37', percentage: 100, trend: 'up' },
  { label: 'Sản phẩm tháng', value: '37', percentage: 100, trend: 'up' },
  { label: 'Sản phẩm năm', value: '37', percentage: 100, trend: 'up' },
]);


const pieChartData = ref({
  labels: [
    'Đã hủy', 'Chờ xác nhận', 'Chờ giao hàng', 'Đang vận chuyển', 
    'Đã giao hàng', 'Đã thanh toán', 'Chờ thanh toán', 'Hoàn thành', 'Trả hàng'
  ],
  datasets: [
    {
      backgroundColor: [
        '#dc3545', // Đã hủy (Đỏ)
        '#fd7e14', // Chờ xác nhận (Cam)
        '#ffc107', // Chờ giao hàng (Vàng)
        '#0dcaf0', // Đang vận chuyển (Cyan)
        '#6f42c1', // Đã giao hàng (Tím)
        '#0d6efd', // Đã thanh toán (Xanh dương)
        '#adb5bd', // Chờ thanh toán (Xám)
        '#198754', // Hoàn thành (Xanh lá)
        '#d63384'  // Trả hàng (Hồng)
      ],
    
      data: [ 5, 10, 10, 0, 0, 5, 0, 30, 10 ],
    },
  ],
});

const pieChartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false, 
    },
  },
});


const pieChartLegend = computed(() => {
  const data = pieChartData.value.datasets[0].data;
  const labels = pieChartData.value.labels;
  const colors = pieChartData.value.datasets[0].backgroundColor;
  const total = data.reduce((a, b) => a + b, 0);

  if (total === 0) {
    return labels.map((label, i) => ({
      label,
      color: colors[i],
      percentage: '0.00'
    }));
  }

  return labels.map((label, i) => ({
    label,
    color: colors[i],
    percentage: ((data[i] / total) * 100).toFixed(2)
  }));
});


const formatCurrency = (value) => {
  if (!value) return "0 ₫";
  return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }).replace('₫', '₫');
};

</script>

<template>
  <div class="container-fluid mt-4 px-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body py-3 px-4">
        <div class="page-header d-flex align-items-center justify-content-between">
          <div>
            <h3 class="fw-bold text-dark mb-1">Thống kê</h3>
            <Breadcrumb class="mt-2 mb-0" />
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-md-6">
        <div class="card card-stats text-white" :class="overviewStats.HOM_NAY.colorClass">
          <div class="card-body text-center">
             <div class="card-icon mb-2">
                <i class="fas fa-3x text-white" :class="overviewStats.HOM_NAY.icon" style="opacity: 0.5;"></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">{{ overviewStats.HOM_NAY.title }}</h4>
            <h2 class="fw-bold text-white mb-2">{{ overviewStats.HOM_NAY.total }}</h2>
            <div class="row g-2 justify-content-center" style="font-size: 0.9rem;">
              <div class="col-auto">Sản phẩm: {{ overviewStats.HOM_NAY.sanPham }}</div>
              <div class="col-auto">Đơn thành công: {{ overviewStats.HOM_NAY.donThanhCong }}</div>
              <div class="col-auto">Đơn hủy: {{ overviewStats.HOM_NAY.donHuy }}</div>
              <div class="col-auto">Đơn trả: {{ overviewStats.HOM_NAY.donTra }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
         <div class="card card-stats text-white" :class="overviewStats.TUAN_NAY.colorClass">
          <div class="card-body text-center">
            <div class="card-icon mb-2">
                <i class="fas fa-3x text-white" :class="overviewStats.TUAN_NAY.icon" style="opacity: 0.5;"></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">{{ overviewStats.TUAN_NAY.title }}</h4>
            <h2 class="fw-bold text-white mb-2">{{ overviewStats.TUAN_NAY.total }}</h2>
            <div class="row g-2 justify-content-center" style="font-size: 0.9rem;">
              <div class="col-auto">Sản phẩm: {{ overviewStats.TUAN_NAY.sanPham }}</div>
              <div class="col-auto">Đơn thành công: {{ overviewStats.TUAN_NAY.donThanhCong }}</div>
              <div class="col-auto">Đơn hủy: {{ overviewStats.TUAN_NAY.donHuy }}</div>
              <div class="col-auto">Đơn trả: {{ overviewStats.TUAN_NAY.donTra }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
     <div class="row">
      <div class="col-md-6">
        <div class="card card-stats text-white" :class="overviewStats.THANG_NAY.colorClass">
          <div class="card-body text-center">
            <div class="card-icon mb-2">
                <i class="fas fa-3x text-white" :class="overviewStats.THANG_NAY.icon" style="opacity: 0.5;"></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">{{ overviewStats.THANG_NAY.title }}</h4>
            <h2 class="fw-bold text-white mb-2">{{ overviewStats.THANG_NAY.total }}</h2>
            <div class="row g-2 justify-content-center" style="font-size: 0.9rem;">
              <div class="col-auto">Sản phẩm: {{ overviewStats.THANG_NAY.sanPham }}</div>
              <div class="col-auto">Đơn thành công: {{ overviewStats.THANG_NAY.donThanhCong }}</div>
              <div class="col-auto">Đơn hủy: {{ overviewStats.THANG_NAY.donHuy }}</div>
              <div class="col-auto">Đơn trả: {{ overviewStats.THANG_NAY.donTra }}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-md-6">
         <div class="card card-stats text-white" :class="overviewStats.NAM_NAY.colorClass">
          <div class="card-body text-center">
            <div class="card-icon mb-2">
                <i class="fas fa-3x text-white" :class="overviewStats.NAM_NAY.icon" style="opacity: 0.5;"></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">{{ overviewStats.NAM_NAY.title }}</h4>
            <h2 class="fw-bold text-white mb-2">{{ overviewStats.NAM_NAY.total }}</h2>
            <div class="row g-2 justify-content-center" style="font-size: 0.9rem;">
              <div class="col-auto">Sản phẩm: {{ overviewStats.NAM_NAY.sanPham }}</div>
              <div class="col-auto">Đơn thành công: {{ overviewStats.NAM_NAY.donThanhCong }}</div>
              <div class="col-auto">Đơn hủy: {{ overviewStats.NAM_NAY.donHuy }}</div>
              <div class="col-auto">Đơn trả: {{ overviewStats.NAM_NAY.donTra }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="row" v-if="showCustomCard">
      <div class="col-12">
        <div class="card card-stats text-white" :class="overviewStats.TUY_CHINH.colorClass">
          <div class="card-body text-center">
            <div class="card-icon mb-2">
                <i class="fas fa-3x text-white" :class="overviewStats.TUY_CHINH.icon" style="opacity: 0.5;"></i>
            </div>
            <h4 class="card-title text-white fw-bold mb-1">{{ overviewStats.TUY_CHINH.title }}</h4>
            <h2 class="fw-bold text-white mb-2">{{ overviewStats.TUY_CHINH.total }}</h2>
            <div class="row g-2 justify-content-center" style="font-size: 0.9rem;">
              <div class="col-auto">Sản phẩm: {{ overviewStats.TUY_CHINH.sanPham }}</div>
              <div class="col-auto">Đơn thành công: {{ overviewStats.TUY_CHINH.donThanhCong }}</div>
              <div class="col-auto">Đơn hủy: {{ overviewStats.TUY_CHINH.donHuy }}</div>
              <div class="col-auto">Đơn trả: {{ overviewStats.TUY_CHINH.donTra }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm border-0 mb-4">
      <div class="card-body p-3">
        <h5 class="mb-2 fw-bold">Bộ lọc</h5>

        <div class="d-flex justify-content-between align-items-center">
          
          <div class="d-flex flex-wrap align-items-center gap-3">
            
            <div class="btn-group" role="group">
              <button 
                type="button" 
                class="btn" 
                :class="selectedFilter === 'NGÀY' ? 'btn-warning' : 'btn-outline-dark'" 
                @click="selectFilter('NGÀY')">
                NGÀY
              </button>
              <button 
                type="button" 
                class="btn" 
                :class="selectedFilter === 'TUẦN' ? 'btn-warning' : 'btn-outline-dark'" 
                @click="selectFilter('TUẦN')">
                TUẦN
              </button>
              <button 
                type="button" 
                class="btn" 
                :class="selectedFilter === 'THÁNG' ? 'btn-warning' : 'btn-outline-dark'" 
                @click="selectFilter('THÁNG')">
                THÁNG
              </button>
              <button 
                type="button" 
                class="btn" 
                :class="selectedFilter === 'NĂM' ? 'btn-warning' : 'btn-outline-dark'" 
                @click="selectFilter('NĂM')">
                NĂM
              </button>
              <button 
                type="button" 
                class="btn" 
                :class="selectedFilter === 'TÙY CHỈNH' ? 'btn-warning' : 'btn-outline-dark'" 
                @click="selectFilter('TÙY CHỈNH')">
                TÙY CHỈNH
              </button>
            </div>
            
            <div v-if="selectedFilter === 'TÙY CHỈNH'" class="d-flex flex-wrap gap-2">
              <div class="input-group input-group-sm" style="width: 180px;">
                <span class="input-group-text">Từ ngày</span>
                <input type="date" class="form-control" v-model="customStartDate">
              </div>
              <div class="input-group input-group-sm" style="width: 180px;">
                <span class="input-group-text">Đến ngày</span>
                <input type="date" class="form-control" v-model="customEndDate">
              </div>
            </div>
          </div>

          <div>
            <button class="btn btn-outline-success">
              <i class="fas fa-file-excel me-1"></i> EXPORT TO EXCEL
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-lg-8">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header" style="background-color: #f39c12; color: white;">
            <h5 class="mb-0 fw-bold">Danh sách sản phẩm bán chạy theo {{ selectedFilter.toLowerCase() }}</h5>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr style="background-color: #f39c12; color: white;">
                    <th class="text-center" style="width: 80px;">Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Giá tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="bestSellingProducts.length === 0">
                    <td colspan="4" class="text-center p-3">Không có dữ liệu</td>
                  </tr>
                  <tr v-for="product in bestSellingProducts" :key="product.id">
                    <td class="text-center">
                      <img :src="product.img" alt="product-img" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                    </td>
                    <td>{{ product.name }}</td>
                    <td class="text-center">{{ product.sold }}</td>
                    <td class="text-end fw-bold">{{ product.price }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="card-footer d-flex justify-content-between align-items-center">
              <div class="d-flex align-items-center gap-2">
                <span class="text-muted" style="font-size: 0.9rem;">Xem</span>
                <select class="form-select form-select-sm" v-model="itemsPerPage" style="width: 75px;">
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="25">25</option>
                </select>
                <span class="text-muted" style="font-size: 0.9rem;">sản phẩm</span>
              </div>
              <nav>
                <ul class="pagination pagination-sm mb-0">
                  <li class="page-item disabled"><a class="page-link" href="#">&laquo;</a></li>
                  <li class="page-item active"><a class="page-link" href="#">1</a></li>
                  <li class="page-item"><a class="page-link" href="#">2</a></li>
                  <li class="page-item"><a class="page-link" href="#">3</a></li>
                  <li class="page-item"><a class="page-link" href="#">&raquo;</a></li>
                </ul>
              </nav>
            </div>
          </div>
        </div>

        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header d-flex flex-wrap justify-content-between align-items-center" style="background-color: #f39c12; color: white;">
            <h5 class="mb-0 fw-bold">Danh sách sản phẩm sắp hết hàng</h5>
            <div class="d-flex align-items-center gap-2">
              <span class="text-nowrap">Số lượng dưới:</span>
              <input type="number" :value="10" class="form-control form-control-sm" style="width: 80px;">
            </div>
          </div>
          <div class="card-body p-0">
            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead>
                  <tr style="background-color: #f39c12; color: white;">
                    <th class="text-center" style="width: 80px;">Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-center">Số lượng</th>
                    <th class="text-end">Giá tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="lowStockProducts.length === 0">
                    <td colspan="4" class="text-center p-3">Không có dữ liệu</td>
                  </tr>
                  <tr v-for="product in lowStockProducts" :key="product.id">
                    <td class="text-center">
                      <img :src="product.img" alt="product-img" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
                    </td>
                    <td>{{ product.name }}</td>
                    <td class="text-center fw-bold text-danger">{{ product.stock }}</td>
                    <td class="text-end fw-bold">{{ product.price }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
             <div class="card-footer d-flex justify-content-between align-items-center">
              <div class="d-flex align-items-center gap-2">
                <span class="text-muted" style="font-size: 0.9rem;">Xem</span>
                <select class="form-select form-select-sm" v-model="itemsPerPageLowStock" style="width: 75px;">
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="25">25</option>
                </select>
                <span class="text-muted" style="font-size: 0.9rem;">sản phẩm</span>
              </div>
              <nav>
                <ul class="pagination pagination-sm mb-0">
                  <li class="page-item disabled"><a class="page-link" href="#">&laquo;</a></li>
                  <li class="page-item active"><a class="page-link" href="#">1</a></li>
                  <li class="page-item"><a class="page-link" href="#">2</a></li>
                  <li class="page-item"><a class="page-link" href="#">3</a></li>
                  <li class="page-item"><a class="page-link" href="#">&raquo;</a></li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>

      <div class="col-lg-4">
        <div class="card shadow-sm border-0 mb-4">
          <div class="card-header">
            <h5 class="mb-0 fw-bold">Biểu đồ trạng thái {{ selectedFilter.toLowerCase() }}</h5>
          </div>
          <div class="card-body">
            <div style="height: 250px">
              <Doughnut :data="pieChartData" :options="pieChartOptions" />
            </div>
            
            <div class="mt-3 row" style="font-size: 0.9rem;">
              <div v-for="item in pieChartLegend" :key="item.label" class="col-6 mb-1">
                <div class="d-flex align-items-center justify-content-between">
                  <div class="d-flex align-items-center" style="min-width: 0;"> <span :style="{ backgroundColor: item.color, width: '15px', height: '15px', display: 'inline-block', marginRight: '8px', borderRadius: '3px', flexShrink: 0 }"></span>
                    <span class="text-truncate" :title="item.label">{{ item.label }}</span>
                  </div>
                  <span class="fw-bold ms-2">{{ item.percentage }}%</span>
                </div>
              </div>
            </div>
            </div>
        </div>

        <div class="card shadow-sm border-0" style="background-color: #212529; color: white;">
          <div class="card-header" style="border-bottom: 1px solid #444;">
            <h5 class="mb-0 fw-bold">Tốc độ tăng trưởng của hàng</h5>
          </div>
          <div class="card-body p-0">
            <ul class="list-group list-group-flush">
              <li 
                v-for="stat in growthStats" 
                :key="stat.label" 
                class="list-group-item d-flex justify-content-between align-items-center px-3 py-2" 
                style="background-color: transparent; border-color: #444; font-size: 0.9rem;"
              >
                <span class="text-white"><i class="fas fa-chart-bar me-2"></i>{{ stat.label }}</span>
                <span class="fw-bold">{{ stat.value }}</span>
                <span :class="stat.trend === 'up' ? 'text-success' : 'text-danger'">
                  <i :class="stat.trend === 'up' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
                  {{ stat.percentage }}%
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.card.card-stats .card-body {
  padding: 1.25rem;
}


.btn-check:checked+.btn, .btn.active, .btn.show, .btn:first-child:active, :not(.btn-check)+.btn:active {
   
    background-color: #f39c12 !important;
    border-color: #f39c12 !important;
    color: white !important;
}
.btn-outline-dark {
  border-color: #ddd;
  color: #333;
}
.btn-outline-dark:hover {
  background-color: #f5f5ff;
  color: #333;
}
.input-group-text {
  background-color: #f8f9fa;
  border-color: #ddd;
}
.form-control {
  border-color: #ddd;
}

.pagination {
  align-items: center;
}

.pagination .page-item .page-link {
  border-radius: 50% !important; 
  width: 2.25rem; 
  height: 2.25rem; 
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 3px; 
  padding: 0;
  font-size: 0.9rem;
}

.pagination .page-item.active .page-link {
  background-color: #f39c12; 
  border-color: #f39c12;
  color: white;
}


.pagination .page-item.active .page-link {
  border-radius: 50% !important;
}


.card-footer .form-select-sm {
  font-size: 0.875rem;
  padding-top: 0.25rem;
  padding-bottom: 0.25rem;
  border-color: #ddd;
}
</style>