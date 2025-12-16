<template>
  <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
    <div
      v-for="(sp, index) in sanPhamList"
      :key="index"
      class="bg-white rounded-2xl shadow-lg overflow-hidden hover:shadow-2xl transition-all"
    >
      <img
        :src="sp.hinhAnh"
        :alt="sp.ten"
        class="w-full h-60 object-cover"
      />
      <div class="p-4 flex flex-col justify-between h-48">
        <h3 class="text-lg font-semibold text-gray-900">{{ sp.ten }}</h3>
        <p class="text-gray-600 text-sm">{{ sp.moTa }}</p>
        <div class="flex justify-between items-center mt-3">
          <span class="text-yellow-700 font-bold text-lg">{{ sp.gia }}₫</span>
          <button
            class="bg-yellow-600 hover:bg-yellow-700 text-white px-3 py-1 rounded-xl"
          >
            Mua ngay
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const sanPhamList = ref([
  {
    ten: "Giày thể thao Nike Air",
    gia: "2.490.000",
    moTa: "Thoải mái, năng động, phù hợp mọi hoạt động.",
    hinhAnh: "https://i.imgur.com/dtV1H4N.jpg",
  },
  {
    ten: "Giày Adidas Ultraboost",
    gia: "3.190.000",
    moTa: "Phong cách hiện đại, đàn hồi cao.",
    hinhAnh: "https://i.imgur.com/z1Vb7uN.jpg",
  },
  {
    ten: "Giày Vans Classic",
    gia: "1.290.000",
    moTa: "Phong cách đường phố, trẻ trung.",
    hinhAnh: "https://i.imgur.com/wYFhPQS.jpg",
  },
  {
    ten: "Giày Converse All Star",
    gia: "1.590.000",
    moTa: "Biểu tượng vượt thời gian, phối đồ dễ dàng.",
    hinhAnh: "https://i.imgur.com/AmvZDhO.jpg",
  },
  {
    ten: "Giày Gucci Sneaker",
    gia: "12.900.000",
    moTa: "Đẳng cấp, tinh tế, mang phong cách châu Âu.",
    hinhAnh: "https://i.imgur.com/jv0qWZ2.jpg",
  },
]);
</script>
