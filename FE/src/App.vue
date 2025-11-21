<script setup>
import { RouterLink } from "vue-router";
import { ref, onMounted, getCurrentInstance } from "vue";
import Notify from "@/components/common/Notify.vue";
import { useAuthStore } from "@/components/login/authStore";
import Test from "./viewOnlineShop/test.vue";

const notifyRef = ref();
const app = getCurrentInstance();
const authStore = useAuthStore();
onMounted(() => {
  app.appContext.config.globalProperties.$notifyRef = notifyRef.value;
});

const handleLogout = () => {
  AuthService.logout();
  // Chuyển hướng về trang chủ
  router.push('/'); 
};

</script>

<template>
  <RouterView></RouterView>
  <Notify ref="notifyRef" />
</template>

<style scoped></style>
