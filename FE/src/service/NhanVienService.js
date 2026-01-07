import instance from "./axios";

const API_NHAN_VIEN = "/admin/nhan-vien";
const API_CHUC_VU = "/admin/chuc-vu";

export const getAllNhanVien = () => instance.get(API_NHAN_VIEN);

export const getNhanVienById = (id) => instance.get(`${API_NHAN_VIEN}/${id}`);

export const createNhanVien = (data) => instance.post(API_NHAN_VIEN, data);

export const updateNhanVien = (id, data) =>
  instance.put(`${API_NHAN_VIEN}/${id}`, data);

export const deleteNhanVien = (id) => instance.delete(`${API_NHAN_VIEN}/${id}`);

export const checkEmail = (email) =>
  instance.get(`${API_NHAN_VIEN}/check-email`, { params: { email } });

export const uploadImage = (formData) =>
  instance.post("/admin/upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

export const getAllChucVu = () => instance.get(API_CHUC_VU);
