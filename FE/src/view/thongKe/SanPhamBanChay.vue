<script setup>
const sanPham = [
  { img: "https://via.placeholder.com/50", name: "Da bò - Đen", quantity: 18, price: "550,000 ₫", size: "40,41,42" },
  { img: "https://via.placeholder.com/50", name: "Da cá sấu - Trắng", quantity: 18, price: "950,000 ₫", size: "41" },
  { img: "https://via.placeholder.com/50", name: "Da - Nâu", quantity: 15, price: "490,000 ₫", size: "40,42" },
];
</script>

<template>
  <div class="san-pham">
    <h3>Danh sách sản phẩm bán chạy theo tùy chỉnh</h3>
    <table>
      <thead>
        <tr>
          <th>Ảnh</th>
          <th>Tên sản phẩm</th>
          <th>Số lượng</th>
          <th>Giá tiền</th>
          <th>Kích cỡ</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(sp, i) in sanPham" :key="i">
          <td><img :src="sp.img" alt="" /></td>
          <td>{{ sp.name }}</td>
          <td>{{ sp.quantity }}</td>
          <td>{{ sp.price }}</td>
          <td>{{ sp.size }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.san-pham h3 {
  color: #f97316;
  margin-bottom: 10px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
thead {
  background-color: #f97316;
  color: white;
}
th, td {
  padding: 8px;
  text-align: center;
  border: 1px solid #ddd;
}
img {
  width: 45px;
  height: 45px;
  border-radius: 6px;
}
</style>

