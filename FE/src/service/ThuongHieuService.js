import axios from "axios";
const API_BASE = "http://localhost:8080/admin/thuong-hieu";

export const getAllThuongHieu = () => axios.get(API_BASE);
export const createThuongHieu = (data) => axios.post(API_BASE, data);
export const updateThuongHieu = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteThuongHieu = (id) => axios.delete(`${API_BASE}/${id}`);
