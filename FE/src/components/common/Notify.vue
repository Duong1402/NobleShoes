<!-- Custom thông báo -->
 
<template>
  <div
    class="toast-container position-fixed top-0 end-0 p-3"
    style="z-index: 1055"
  >
    <transition-group name="toast" tag="div">
      <div
        v-for="(toast, index) in toasts"
        :key="toast.id"
        class="toast align-items-center text-white border-0 shadow show mb-2"
        :class="{
          'bg-success': toast.type === 'success',
          'bg-danger': toast.type === 'error',
          'bg-warning text-dark': toast.type === 'warning',
          'bg-info': toast.type === 'info',
        }"
        role="alert"
      >
        <div class="d-flex align-items-center">
          <div class="toast-body d-flex align-items-center">
            <i :class="toast.icon" class="me-2 fs-5"></i>
            <span>{{ toast.message }}</span>
          </div>
          <button
            type="button"
            class="btn-close btn-close-white me-2 m-auto"
            @click="removeToast(index)"
          ></button>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { ref } from "vue";

const toasts = ref([]);

const addToast = (message, type = "info") => {
  const icons = {
    success: "fa fa-check-circle",
    error: "fa fa-times-circle",
    warning: "fa fa-exclamation-triangle",
    info: "fa fa-info-circle",
  };

  const id = Date.now(); // unique key
  const toast = { id, message, type, icon: icons[type] };

  toasts.value.push(toast);

  // Tự ẩn sau 3s
  setTimeout(() => {
    toasts.value = toasts.value.filter((t) => t.id !== id);
  }, 3000);
};

const removeToast = (index) => {
  toasts.value.splice(index, 1);
};

// Cho phép component khác gọi qua expose
defineExpose({ addToast });
</script>

<style scoped>
.toast {
  min-width: 280px;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  opacity: 0.95;
  transition: all 0.3s ease;
}

/* ✨ Hiệu ứng Transition */
.toast-enter-from {
  opacity: 0;
  transform: translateX(50px);
}
.toast-enter-active {
  transition: all 0.4s ease-out;
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(50px);
}
.toast-leave-active {
  transition: all 0.4s ease-in;
}
</style>
