import axios from "axios";

const API_BASE_URL = "http://localhost:8080/admin/san-pham";

export const getAllSanPham = () => axios.get(API_BASE_URL);

export const getSanPhamById = (id) => axios.get(`${API_BASE_URL}/${id}`);

export const createSanPham = (data) => axios.post(API_BASE_URL, data);

export const updateSanPham = (id, data) =>
  axios.put(`${API_BASE_URL}/${id}`, data);

export const deleteSanPham = (id) => axios.delete(`${API_BASE_URL}/${id}`);
