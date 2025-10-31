import axios from "axios";

const API_BASE_URL = "http://localhost:8080/admin/nhan-vien";

export const getAllNhanVien = () => axios.get(API_BASE_URL);

export const getNhanVienById = (id) => axios.get(`${API_BASE_URL}/${id}`);

export const createNhanVien = (data) => axios.post(API_BASE_URL, data);

export const updateNhanVien = (id, data) =>
  axios.put(`${API_BASE_URL}/${id}`, data);

export const deleteNhanVien = (id) => axios.delete(`${API_BASE_URL}/${id}`);