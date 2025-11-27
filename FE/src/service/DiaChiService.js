import axios from "axios";

const API_BASE_URL = "http://localhost:8080/admin/dia-chi";

export const getAllDiaChi = () => axios.get(API_BASE_URL);

export const getDiaChiById = (id) => axios.get(`${API_BASE_URL}/${id}`);

export const getDiaChiByKhachHangId = (idKhachHang) =>
  axios.get(`${API_BASE_URL}/khach-hang/${idKhachHang}`);

export const createDiaChi = (payload) => axios.post(`${API_BASE_URL}`, payload);

export const updateDiaChi = (id, payload) =>
  axios.put(`${API_BASE_URL}/${id}`, payload);

export const deleteDiaChi = (id) => axios.delete(`${API_BASE_URL}/${id}`);
