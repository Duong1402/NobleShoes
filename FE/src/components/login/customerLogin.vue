<script setup>
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import AuthService from "@/components/login/loginService";
import Swal from "sweetalert2";

const username = ref("");
const password = ref("");
const loading = ref(false);
const message = ref("");
const router = useRouter();

const form = reactive({
  hoTen: "",
  email: "",
  sdt: "",
  taiKhoan: "",
  matKhau: "",
});

const handleLogin = () => {
  loading.value = true;
  message.value = "";

  AuthService.loginCustomer(username.value, password.value) // <-- Chỉ gọi hàm của Employee
    .then(
      () => {
        // Đăng nhập thành công, chuyển hướng đến trang dashboard admin
        router.replace("/shop");
      },
      (error) => {
        // Xử lý lỗi (ví dụ: sai mật khẩu)
        message.value = error.response?.data?.message || "Đăng nhập thất bại";
        loading.value = false;
      }
    );
};

const isValidEmail = (email) => {
  const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(String(email).toLowerCase());
};

const emailError = computed(() => {
    if (form.email.length > 0 && !isValidEmail(form.email)) {
        return true; // Có lỗi
    }
    return false; // Không lỗi
});

const handleRegister = async () => {
  // 1. Validate dữ liệu trống
  if (!form.hoTen || !form.taiKhoan || !form.matKhau || !form.email || !form.sdt) {
      Swal.fire("Lỗi", "Vui lòng điền đầy đủ thông tin!", "warning");
      return;
  }

  // 2. THÊM: Validate định dạng Email
  if (!isValidEmail(form.email)) {
      Swal.fire("Lỗi", "Email không đúng định dạng! (Ví dụ: abc@gmail.com)", "warning");
      return;
  }

  // 3. THÊM: Validate số điện thoại (10 số)
  if (form.sdt.length < 10) {
      Swal.fire("Lỗi", "Số điện thoại phải đủ 10 số!", "warning");
      return;
  }

  loading.value = true;
  try {
    // Gọi API đăng ký
    await AuthService.register(form);

    Swal.fire(
      "Thành công",
      "Đăng ký thành công! Vui lòng đăng nhập.",
      "success"
    );
    
    // Reset form và quay lại tab đăng nhập
    // (Nếu dùng checkbox lật thẻ 3D)
    const toggle = document.getElementById('reg-log');
    if(toggle) toggle.checked = false;

  } catch (error) {
    console.error(error);
    // Hiển thị lỗi từ Backend (Ví dụ: "Email này đã được sử dụng!")
    const msg = error.response?.data || "Đăng ký thất bại";
    Swal.fire(
      "Lỗi",
      typeof msg === "string" ? msg : "Có lỗi xảy ra",
      "error"
    );
  } finally {
    loading.value = false;
  }
};
</script>
<template>
  <div class="login-page">
    <a href="https://front.codes/" class="logo" target="_blank">
      <img
        src="/src/assets/img/logoo-Photoroom.png"
        alt="navbar brand"
        class="navbar-brand"
        height="140"
      />
    </a>

    <div class="section">
      <div class="container">
        <div class="row full-height justify-content-center">
          <div class="col-12 text-center align-self-center py-5">
            <div class="section pb-5 pt-5 pt-sm-2 text-center">
              <h6 class="mb-0 pb-3">
                <span>Log In </span><span>Sign Up</span>
              </h6>
              <input
                class="checkbox"
                type="checkbox"
                id="reg-log"
                name="reg-log"
              />
              <label for="reg-log"></label>
              <div class="card-3d-wrap mx-auto">
                <div class="card-3d-wrapper">
                  <div class="card-front">
                    <div class="center-wrap">
                      <div class="section text-center">
                        <form @submit.prevent="handleLogin">
                          <h4 class="mb-4 pb-3">Đăng nhập</h4>
                          <div class="form-group">
                            <input
                              type="text"
                              class="form-style"
                              placeholder="Tài khoản"
                              autocomplete="off"
                              v-model="username"
                            />
                            <i class="input-icon uil uil-user"></i>
                          </div>
                          <div class="form-group mt-2">
                            <input
                              type="password"
                              class="form-style"
                              placeholder="Mật khẩu"
                              autocomplete="off"
                              v-model="password"
                            />
                            <i class="input-icon uil uil-lock-alt"></i>
                          </div>
                          <button
                            type="submit"
                            class="btn mt-4"
                            :disabled="loading"
                          >
                            <span v-if="loading">Đang xử lý...</span>
                            <span v-else>Gửi</span>
                          </button>
                          <div v-if="message" class="error">{{ message }}</div>
                          <p class="mb-0 mt-4 text-center">
                            <a href="#0" class="link">Quên mật khẩu</a>
                          </p>
                        </form>
                      </div>
                    </div>
                  </div>
                  <div class="card-back">
                    <form @submit.prevent="handleRegister">
                      <div class="center-wrap">
                        <div class="section text-center">
                          <h4 class="mb-4 pb-3">Đăng ký</h4>
                          <div class="form-group">
                            <input
                              type="text"
                              class="form-style"
                              placeholder="Họ tên"
                              autocomplete="off"
                              v-model="form.hoTen"
                            />
                            <i class="input-icon uil uil-user"></i>
                          </div>
                          <div class="form-group mt-2">
                            <input
                              type="text"
                              class="form-style"
                              placeholder="Tài khoản"
                              autocomplete="off"
                              v-model="form.taiKhoan"
                            />
                            <i class="input-icon uil uil-at"></i>
                          </div>
                          <div class="form-group mt-2">
                            <input
                              type="password"
                              class="form-style"
                              placeholder="Mật khẩu"
                              autocomplete="new-password"
                              v-model="form.matKhau"
                            />
                            <i class="input-icon uil uil-lock-alt"></i>
                          </div>
                          <div class="form-group mt-2">
                            <input
                              type="text"
                              class="form-style"
                              placeholder="Email"
                              autocomplete="new-password"
                              v-model="form.email"
                              :class="{ 'input-error': emailError }"
                            />
                            <i class="input-icon uil uil-envelope"></i>
                          </div>
                          <p v-if="emailError" style="color: #ffeba7; font-size: 12px; margin-top: 5px;">Email không hợp lệ</p>
                          <div class="form-group mt-2">
                            <input
                              type="tel"
                              class="form-style"
                              placeholder="SĐT"
                              autocomplete="off"
                              v-model="form.sdt"
                              maxlength="10"
                              @input="form.sdt = form.sdt.replace(/\D/g, '').slice(0, 10)"
                            />
                            <i class="input-icon uil uil-phone"></i>
                          </div>
                          <button
                            type="submit"
                            class="btn mt-4"
                            :disabled="loading"
                          >
                            <span v-if="loading">Đang xử lý...</span>
                            <span v-else>Đăng ký</span>
                          </button>
                        </div>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
@import url("https://fonts.googleapis.com/css?family=Poppins:400,500,600,700,800,900");

.login-page {
  font-family: 'Poppins', sans-serif;
	font-weight: 300;
	font-size: 15px;
	line-height: 1.7;
	color: #c4c3ca;
	background-color: #1f2029;
	overflow-x: hidden;
}
a {
	cursor: pointer;
  transition: all 200ms linear;
}
a:hover {
	text-decoration: none;
}
.link {
  color: #c4c3ca;
}
.link:hover {
  color: #ffeba7;
}
p {
  font-weight: 500;
  font-size: 14px;
  line-height: 1.7;
}
h4 {
  font-weight: 600;
}
h6 span{
  padding: 0 20px;
  text-transform: uppercase;
  font-weight: 700;
}
.section{
  position: relative;
  width: 100%;
  display: block;
}
.full-height{
  min-height: 100vh;
}
[type="checkbox"]:checked,
[type="checkbox"]:not(:checked){
  position: absolute;
  left: -9999px;
}
.checkbox:checked + label,
.checkbox:not(:checked) + label{
  position: relative;
  display: block;
  text-align: center;
  width: 60px;
  height: 16px;
  border-radius: 8px;
  padding: 0;
  margin: 10px auto;
  cursor: pointer;
  background-color: #ffeba7;
}
.checkbox:checked + label:before,
.checkbox:not(:checked) + label:before{
  position: absolute;
  display: block;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  color: #ffeba7;
  background-color: #102770;
  font-family: 'unicons';
  content: '\eb4f';
  z-index: 20;
  top: -10px;
  left: -10px;
  line-height: 36px;
  text-align: center;
  font-size: 24px;
  transition: all 0.5s ease;
}
.checkbox:checked + label:before {
  transform: translateX(44px) rotate(-270deg);
}


.card-3d-wrap {
  position: relative;
  width: 440px;
  max-width: 100%;
  height: 400px;
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  perspective: 800px;
  margin-top: 60px;
}
.card-3d-wrapper {
  width: 100%;
  height: 100%;
  position:absolute;    
  top: 0;
  left: 0;  
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  transition: all 600ms ease-out; 
}
.card-front, .card-back {
  width: 100%;
  height: 100%;
  background-color: #2a2b38;
  background-image: url('https://s3-us-west-2.amazonaws.com/s.cdpn.io/1462889/pat.svg');
  background-position: bottom center;
  background-repeat: no-repeat;
  background-size: 300%;
  position: absolute;
  border-radius: 6px;
  left: 0;
  top: 0;
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
  -webkit-backface-visibility: hidden;
  -moz-backface-visibility: hidden;
  -o-backface-visibility: hidden;
  backface-visibility: hidden;
}
.card-back {
  transform: rotateY(180deg);
}
.checkbox:checked ~ .card-3d-wrap .card-3d-wrapper {
  transform: rotateY(180deg);
}
.center-wrap{
  position: absolute;
  width: 100%;
  padding: 0 35px;
  top: 50%;
  left: 0;
  transform: translate3d(0, -50%, 35px) perspective(100px);
  z-index: 20;
  display: block;
}


.form-group{ 
  position: relative;
  display: block;
    margin: 0;
    padding: 0;
}
.form-style {
  padding: 13px 20px;
  padding-left: 55px;
  height: 48px;
  width: 100%;
  font-weight: 500;
  border-radius: 4px;
  font-size: 14px;
  line-height: 22px;
  letter-spacing: 0.5px;
  outline: none;
  color: #c4c3ca;
  background-color: #1f2029;
  border: none;
  -webkit-transition: all 200ms linear;
  transition: all 200ms linear;
  box-shadow: 0 4px 8px 0 rgba(21,21,21,.2);
}
.form-style:focus,
.form-style:active {
  border: none;
  outline: none;
  box-shadow: 0 4px 8px 0 rgba(21,21,21,.2);
}
.input-icon {
  position: absolute;
  top: 0;
  left: 18px;
  height: 48px;
  font-size: 24px;
  line-height: 48px;
  text-align: left;
  color: #ffeba7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}

.form-group input:-ms-input-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input::-moz-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:-moz-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input::-webkit-input-placeholder  {
  color: #c4c3ca;
  opacity: 0.7;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus:-ms-input-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus::-moz-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus:-moz-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}
.form-group input:focus::-webkit-input-placeholder  {
  opacity: 0;
  -webkit-transition: all 200ms linear;
    transition: all 200ms linear;
}

.btn{  
  border-radius: 4px;
  height: 44px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  -webkit-transition : all 200ms linear;
  transition: all 200ms linear;
  padding: 0 30px;
  letter-spacing: 1px;
  display: -webkit-inline-flex;
  display: -ms-inline-flexbox;
  display: inline-flex;
  -webkit-align-items: center;
  -moz-align-items: center;
  -ms-align-items: center;
  align-items: center;
  -webkit-justify-content: center;
  -moz-justify-content: center;
  -ms-justify-content: center;
  justify-content: center;
  -ms-flex-pack: center;
  text-align: center;
  border: none;
  background-color: #ffeba7;
  color: #102770;
  box-shadow: 0 8px 24px 0 rgba(255,235,167,.2);
}
.btn:active,
.btn:focus{  
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16,39,112,.2);
}
.btn:hover{  
  background-color: #102770;
  color: #ffeba7;
  box-shadow: 0 8px 24px 0 rgba(16,39,112,.2);
}




.logo {
	position: absolute;
	top: 30px;
	right: 30px;
	display: block;
	z-index: 100;
	transition: all 250ms linear;
}
.logo img {
	height: 26px;
	width: auto;
	display: block;
}
input:-webkit-autofill,
input:-webkit-autofill:hover, 
input:-webkit-autofill:focus, 
input:-webkit-autofill:active {
    /* Đè màu nền bằng box-shadow (Màu tím của bạn: #5a63b0) */
    -webkit-box-shadow: 0 0 0 1000px #5a63b0 inset !important;
    
    /* Đổi màu chữ thành xám sáng */
    -webkit-text-fill-color: #c4c3ca !important;
    
    /* Giữ nguyên bo góc */
    border-radius: 4px !important;
    
    /* Mẹo nhỏ: Làm chậm quá trình đổi màu nền của trình duyệt */
    transition: background-color 9999s ease-in-out 0s !important;
    
    /* Đảm bảo chữ không bị mất */
    caret-color: #c4c3ca !important;
}
</style>
