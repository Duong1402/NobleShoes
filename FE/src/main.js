import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { useAuthStore } from "./components/login/authStore";

// ✅ Bootstrap + Font Awesome
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "@fortawesome/fontawesome-free/css/all.css";

// ✅ CSS của Kaiadmin (chỉ CSS, KHÔNG import JS)
import "@/assets/css/fonts.min.css";
import "@/assets/css/plugins.min.css";
import "@/assets/css/kaiadmin.min.css";
import "@/assets/css/demo.css";

import { createPinia } from 'pinia'
import axios from "axios";

axios.interceptors.request.use(
    (config) => {
        const authStore = useAuthStore();
        const token = authStore.token; // Hoặc localStorage.getItem('user_token')

        // --- ĐÂY LÀ PHẦN SỬA LỖI ---
        // Kiểm tra xem URL có phải là URL đăng nhập/đăng ký không
        const isAuthUrl = config.url.includes('/api/auth/login') || config.url.includes('/api/auth/register');

        // Chỉ đính kèm token nếu CÓ token VÀ request NÀY KHÔNG PHẢI là request đăng nhập
        if (token && !isAuthUrl) { 
            config.headers['Authorization'] = 'Bearer ' + token;
            console.log("Đã đính kèm Token vào request:", config.url);
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);
app.use(router);
app.mount("#app");

