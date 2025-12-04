import instance from "./axios";

const API_BASE_URL = "/admin/nhan-vien";

export const getAllNhanVien = () => instance.get(API_BASE_URL);

export const getNhanVienById = (id) => instance.get(`${API_BASE_URL}/${id}`);

export const createNhanVien = (data) => instance.post(API_BASE_URL, data);

export const updateNhanVien = (id, data) =>
  instance.put(`${API_BASE_URL}/${id}`, data);

export const deleteNhanVien = (id) => instance.delete(`${API_BASE_URL}/${id}`);