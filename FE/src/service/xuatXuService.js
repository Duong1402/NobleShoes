import axios from "axios";

const API_URL = "http://localhost:8080/admin/xuat-xu";

export const getAllXuatXu = () => axios.get(API_URL);
export const updateXuatXu = (id, data) => axios.put(`${API_URL}/${id}`, data);
export const createXuatXu = (data) => axios.post(API_URL, data);
export const deleteXuatXu = (id) => axios.delete(`${API_URL}/${id}`);
