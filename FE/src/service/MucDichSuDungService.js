import axios from "axios";
const API_BASE = "http://localhost:8080/admin/muc-dich-su-dung";

export const getAllMucDichSuDung = () => axios.get(API_BASE);
export const createMucDichSuDung = (data) => axios.post(API_BASE, data);
export const updateMucDichSuDung = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteMucDichSuDung = (id) => axios.delete(`${API_BASE}/${id}`);
