import axios from "axios";

// Định nghĩa URL gốc
const BASE_URL = "http://localhost:8080/admin/khach-hang";

// Sử dụng trực tiếp 'axios' để tận dụng Interceptor đã config bên main.js
export const getAllKhachHang = (params) => axios.get(BASE_URL, { params });

export const getKhachHangById = (id) => axios.get(`${BASE_URL}/${id}`);

export const createKhachHang = (data) => axios.post(BASE_URL, data);

export const updateKhachHang = (id, data) => axios.put(`${BASE_URL}/${id}`, data);

export const deleteKhachHang = (id) => axios.delete(`${BASE_URL}/${id}`);

export default {
  getAllKhachHang,
  getKhachHangById,
  createKhachHang,
  updateKhachHang,
  deleteKhachHang,
};