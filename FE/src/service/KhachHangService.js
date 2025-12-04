import instance from "./axios"; 

const API_BASE_URL = "/admin/khach-hang"; 


export const getAllKhachHang = (params) => instance.get(API_BASE_URL, { params });
export const getKhachHangById = (id) => instance.get(`${API_BASE_URL}/${id}`);
export const createKhachHang = (data) => instance.post(API_BASE_URL, data);
export const updateKhachHang = (id, data) => instance.put(`${API_BASE_URL}/${id}`, data);
export const deleteKhachHang = (id) => instance.delete(`${API_BASE_URL}/${id}`);

export default {
  getAllKhachHang,
  getKhachHangById,
  createKhachHang,
  updateKhachHang,
  deleteKhachHang,
};