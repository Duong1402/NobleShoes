import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

// ✅ Bootstrap + Font Awesome
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "@fortawesome/fontawesome-free/css/all.css";

// ✅ CSS của Kaiadmin (chỉ CSS, KHÔNG import JS)
import "@/assets/css/fonts.min.css";
import "@/assets/css/plugins.min.css";
import "@/assets/css/kaiadmin.min.css";
import "@/assets/css/demo.css";

const app = createApp(App);
app.use(router);
app.mount("#app");
