<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import banner1 from "@/assets/img/default-home/1.jpg";
import banner2 from "@/assets/img/default-home/2.jpg";
import banner3 from "@/assets/img/default-home/3.jpg";
import banner4 from "@/assets/img/default-home/4.jpg";

const images = [banner1, banner2, banner3, banner4];

const currentIndex = ref(0);
let timer = null;

onMounted(() => {
  timer = setInterval(() => {
    currentIndex.value = (currentIndex.value + 1) % images.length;
  }, 3000);
});

onBeforeUnmount(() => {
  clearInterval(timer);
});
</script>

<template>
  <div class="container-fluid mt-4">
    <div class="welcome-wrapper">
      <div class="welcome-marquee">
        <span>
          👞 Chào mừng bạn đến với hệ thống quản trị của cửa hàng giày da
          <strong>NobleShoes</strong> 👞
        </span>
      </div>
    </div>

    <div class="slider mt-3">
      <transition name="fade" mode="out-in">
        <img
          :key="currentIndex"
          :src="images[currentIndex]"
          class="slider-img"
        />
      </transition>
    </div>
  </div>
</template>
<style scoped>
.welcome-wrapper {
  background: linear-gradient(90deg, #1c1c1c, #ffc107);
  padding: 12px 0;
  overflow: hidden;
  border-radius: 8px;
}

.welcome-marquee {
  white-space: nowrap;
  overflow: hidden;
  position: relative;
}

.welcome-marquee span {
  display: inline-block;
  padding-left: 100%;
  font-size: 18px;
  font-weight: 500;
  color: #f5f5f5;
  animation: marquee 22s linear infinite;
}

.welcome-marquee strong {
  color: #FFD700;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-100%);
  }
}

/* Slider */
.slider {
  width: 100%;
  height: 780px;
  overflow: hidden;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
}

.slider-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center bottom;
}

/* Fade animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.8s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
