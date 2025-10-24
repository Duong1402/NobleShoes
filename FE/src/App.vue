<script setup>
import { RouterLink } from "vue-router";
import { ref, onMounted, getCurrentInstance } from "vue";
import Notify from "@/components/common/Notify.vue";

const notifyRef = ref();
const app = getCurrentInstance();

onMounted(() => {
  app.appContext.config.globalProperties.$notifyRef = notifyRef.value;
});
</script>

<template>
  <RouterView></RouterView>
  <Notify ref="notifyRef" />
</template>

<style scoped></style>
