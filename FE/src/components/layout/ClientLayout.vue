<template>
  <div class="min-h-screen flex flex-col bg-[#f9f9f9] text-gray-800">
    <header class="bg-gradient-to-r from-yellow-700 to-yellow-500 text-white p-4 shadow-lg flex justify-between items-center">
      <div class="flex items-center space-x-3">
        <img src="@/assets/img/logoo-Photoroom.png" alt="Logo" class="h-10" />
        <h1 class="text-xl font-semibold">Golden Shoe</h1>
      </div>
      <nav class="space-x-6">
        <router-link to="/shop" class="hover:text-yellow-200">Trang chủ</router-link>
        <router-link to="/shop" class="hover:text-yellow-200">Sản phẩm</router-link>
        <router-link to="/shop" class="hover:text-yellow-200">Liên hệ</router-link>
      </nav>
    </header>

    <main class="flex-1 p-8">
      <router-view />
    </main>

    <footer class="bg-yellow-700 text-white text-center py-4">
      © 2025 Golden Shoe - Tất cả quyền được bảo lưu.
    </footer>
  </div>
</template>