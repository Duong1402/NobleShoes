<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import AuthService from "@/components/login/loginService";

const username = ref("");
const password = ref("");
const loading = ref(false);
const message = ref("");
const router = useRouter();

const handleLogin = () => {
  loading.value = true;
  message.value = "";

  AuthService.loginEmployee(username.value, password.value) // <-- Chỉ gọi hàm của Employee
    .then(
      () => {
        // Đăng nhập thành công, chuyển hướng đến trang dashboard admin
        router.push("/admin");
      },
      (error) => {
        // Xử lý lỗi (ví dụ: sai mật khẩu)
        message.value = error.response?.data?.message || "Đăng nhập thất bại";
        loading.value = false;
      }
    );
};
</script>

<template>
  <div class="container">
    <div class="row">
      <div class="col-md-6 offset-md-3">
        <h2 class="text-center text-dark mt-5">Đăng nhập</h2>
        <div class="card my-5">
          <form class="card-body cardbody-color p-lg-5" @submit.prevent="handleLogin">
            <div class="text-center">
              <img
                src="https://cdn.pixabay.com/photo/2016/03/31/19/56/avatar-1295397__340.png"
                class="img-fluid profile-image-pic img-thumbnail rounded-circle my-3"
                width="200px"
                alt="profile"
              />
            </div>

            <div class="mb-3">
              <input
                type="text"
                class="form-control"
                id="Username"
                placeholder="Tên đăng nhập"
                v-model="username"
              />
            </div>
            <div class="mb-3">
              <input
                type="password"
                class="form-control"
                id="password"
                placeholder="Mật khẩu"
                v-model="password"
              />
            </div>
            <div class="text-center">
              <button type="submit" class="btn btn-color px-5 mb-5 w-100">
                Đăng nhập
              </button>
              <div v-if="message" class="error">{{ message }}</div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-color {
  background-color: #0e1c36;
  color: #fff;
}

.profile-image-pic {
  height: 200px;
  width: 200px;
  object-fit: cover;
}

.cardbody-color {
  background-color: #ebf2fa;
}

a {
  text-decoration: none;
}
</style>
