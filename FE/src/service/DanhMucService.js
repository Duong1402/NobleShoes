import axios from "axios";
const API_BASE = "http://localhost:8080/admin/danh-muc";

export const getAllDanhMuc = () => axios.get(API_BASE);
export const createDanhMuc = (data) => axios.post(API_BASE, data);
export const updateDanhMuc = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteDanhMuc = (id) => axios.delete(`${API_BASE}/${id}`);
