<script setup>
import { RouterLink, RouterView, useRouter } from "vue-router";
import { ref, onMounted, onBeforeUnmount, computed } from "vue";
import FooterLayout from "./footerLayout.vue";
import Sidebar from "./sidebar.vue";
import ChatWidget from "@/components/ChatWidget.vue";
import Swal from "sweetalert2";
import { useAuthStore } from "../login/authStore";

const router = useRouter();
const showDropdown = ref(false);
const authStore = useAuthStore();
const userName = computed(() => authStore.username || "User");
const userEmail = computed(() => authStore.email || "");

const logout = () => {
  Swal.fire({
    title: "Đăng xuất?",
    text: "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: "Đăng xuất",
    cancelButtonText: "Hủy",
    reverseButtons: true,
    confirmButtonColor: "#d33",
    cancelButtonColor: "#3085d6",
  }).then((result) => {
    if (result.isConfirmed) {
      authStore.logout();

      Swal.fire({
        icon: "success",
        title: "Đã đăng xuất",
        timer: 1200,
        showConfirmButton: false,
      });

      setTimeout(() => {
        router.replace("/login-employee");
      }, 1200);
    }
  });
};

// Đóng dropdown khi click ngoài
const handleClickOutside = (e) => {
  if (!e.target.closest(".user-dropdown")) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside);
});
</script>
<template>
  <div class="wrapper">
    <!-- Sidebar -->
    <Sidebar />
    <!-- End Sidebar -->

    <div class="main-panel">
      <div class="main-header">
        <!-- Navbar Header -->
        <nav
          class="navbar navbar-header navbar-header-transparent navbar-expand-lg border-bottom"
        >
          <div class="container-fluid">
            <div class="user-dropdown position-relative ms-auto">
              <!-- Toggle -->
              <button
                class="btn profile-pic d-flex align-items-center"
                @click.stop="showDropdown = !showDropdown"
              >
                <div class="avatar-sm me-2">
                  <img
                    src="/src/assets/img/profile.jpg"
                    alt="avatar"
                    class="avatar-img rounded-circle"
                  />
                </div>

                <span class="profile-username d-none d-md-inline">
                  <span class="op-7">Hi,</span>
                  <span class="fw-bold">{{ userName }}</span>
                </span>

                <i class="fa fa-angle-down ms-2"></i>
              </button>

              <!-- Dropdown -->
              <ul
                class="dropdown-menu dropdown-user"
                :class="{ show: showDropdown }"
              >
                <li class="dropdown-item-text text-center">
                  <strong>{{ userName }}</strong
                  ><br />
                  <small v-if="userEmail" class="text-muted">{{
                    userEmail
                  }}</small>
                </li>

                <li><hr class="dropdown-divider" /></li>

                <li>
                  <button class="dropdown-item text-danger" @click="logout">
                    <i class="fa fa-sign-out-alt me-2"></i> Đăng xuất
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </nav>
        <!-- End Navbar -->
      </div>
      <div class="container-fluid py-3 mt-5">
        <RouterView></RouterView>
      </div>
      <FooterLayout></FooterLayout>
    </div>
    <ChatWidget />
  </div>
</template>
<style scoped>
.user-dropdown .dropdown-menu {
  right: 0;
  left: auto;
  margin-top: 8px;
  min-width: 220px;
}
.avatar-sm {
  width: 35px;
  height: 35px;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-pic {
  background: transparent;
  border: none;
  padding: 0;
}
.swal2-popup {
  border-radius: 12px;
  font-size: 15px;
}

.swal2-title {
  font-weight: 600;
}
</style>
