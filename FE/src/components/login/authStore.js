import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode"; // Đảm bảo đã npm install jwt-decode

export const useAuthStore = defineStore("auth", {
  state: () => {
    // 1. Lấy token từ LocalStorage
    const token = localStorage.getItem("user_token");
    let userType = localStorage.getItem("user_type");
    let username = null;

    // 2. Nếu có token, giải mã ngay lập tức để lấy userType
    if (token) {
      try {
        const decoded = jwtDecode(token);
        userType = decoded.userType; // Lấy userType từ payload token
        username = decoded.sub; // Lấy username
        console.log("Đã khôi phục trạng thái đăng nhập:", {
          userType,
          username,
        });
      } catch (error) {
        console.error("Token lỗi, đăng xuất...", error);
        localStorage.removeItem("user_token"); // Token rác, xóa đi
      }
    }

    // 3. Khởi tạo state với dữ liệu đã khôi phục
    return {
      token: token || null,
      userType: userType || null,
      username: null,
    };
  },

  getters: {
    isLoggedIn: (state) => !!state.token,
    // So sánh chính xác chuỗi trong token
    isEmployee: (state) =>
      state.userType === "EMPLOYEE" || state.userType === "ADMIN",
    isCustomer: (state) => state.userType === "CUSTOMER",
  },

  actions: {
    setLoginSuccess(token, userType) {
      this.token = token;
      this.userType = userType;

      // Giải mã thêm username nếu cần
      const decoded = jwtDecode(token);
      this.username = decoded.sub;

      localStorage.setItem("user_token", token);
      localStorage.setItem("user_type", userType);
    },

    logout() {
      this.token = null;
      this.userType = null;
      this.username = null;
      localStorage.removeItem("user_token");
      localStorage.removeItem("user_type");
      // Chuyển hướng về login sẽ do Router lo
    },
  },
});
