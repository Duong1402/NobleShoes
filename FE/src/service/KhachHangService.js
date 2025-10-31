// src/service/KhachHangService.js
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  withCredentials: false,
});

const RESOURCE = "/khach-hang";

export const getAllKhachHang = (params) => api.get(RESOURCE, { params });
export const getKhachHangById = (id) => api.get(`${RESOURCE}/${id}`);
export const createKhachHang = (data) => api.post(RESOURCE, data);
export const updateKhachHang = (id, data) => api.put(`${RESOURCE}/${id}`, data);
export const deleteKhachHang = (id) => api.delete(`${RESOURCE}/${id}`);

export default {
  getAllKhachHang,
  getKhachHangById,
  createKhachHang,
  updateKhachHang,
  deleteKhachHang,
};
