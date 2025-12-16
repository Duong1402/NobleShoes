import axios from "axios";

const instance = axios.create({
  baseURL: "http://localhost:8080", // URL cơ sở của Backend
  headers: {
    "Content-Type": "application/json",
  },
});

// Thêm Interceptor cho Request
instance.interceptors.request.use(
  (config) => {
    // 1. Lấy Token từ Local Storage
    const token = localStorage.getItem('user_token'); 

    // 2. Nếu có Token, đính kèm vào Header Authorization
    if (token) {
      // Chuẩn bị Header theo định dạng JWT Bearer Token
      config.headers.Authorization = `Bearer ${token}`; 
    }
    
    // 3. Trả về cấu hình đã cập nhật
    return config;
  },
  (error) => {
    // Xử lý lỗi request
    return Promise.reject(error);
  }
);

export default instance;