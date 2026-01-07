import instance from "./axios";

const API_BASE_URL = "/admin/san-pham";

export const getAllSanPham = () => instance.get(`${API_BASE_URL}/all`);

export const getSanPhamById = (id) => instance.get(`${API_BASE_URL}/${id}`);

export const createSanPham = (data) => instance.post(API_BASE_URL, data);

export const updateSanPham = (id, data) =>
  instance.put(`${API_BASE_URL}/${id}`, data);

export const deleteSanPham = (id) => instance.delete(`${API_BASE_URL}/${id}`);

export const updateTrangThai = (id, value) =>
  instance.patch(`${API_BASE_URL}/${id}/trang-thai-body`, { value });

export const updateTrangThaiParam = (id, value) =>
  instance.patch(`${API_BASE_URL}/${id}/trang-thai`, null, {
    params: { value },
  });

export const getMucDichSuDung = () => instance.get("/admin/muc-dich-su-dung");
export const getDanhMuc = () => instance.get("/admin/danh-muc");
export const getThuongHieu = () => instance.get("/admin/thuong-hieu");
export const getDeGiay = () => instance.get("/admin/de-giay");
export const getDayGiay = () => instance.get("/admin/day-giay");
export const getChatLieu = () => instance.get("/admin/chat-lieu");
export const getXuatXu = () => instance.get("/admin/xuat-xu");

export const getMauSac = () => instance.get("/admin/mau-sac");
export const createMauSac = (data) => instance.post("/admin/mau-sac", data);
export const deleteMauSac = (id) => instance.delete(`/admin/mau-sac/${id}`);

export const getKichThuoc = () => instance.get("/admin/kich-thuoc");
export const createKichThuoc = (data) =>
  instance.post("/admin/kich-thuoc", data);
export const deleteKichThuoc = (id) =>
  instance.delete(`/admin/kich-thuoc/${id}`);

export default {
  getAllSanPham,
  getSanPhamById,
  createSanPham,
  updateSanPham,
  deleteSanPham,
  updateTrangThai,
  getMucDichSuDung,
  getDanhMuc,
  getThuongHieu,
  getMauSac,
  createMauSac,
  deleteMauSac,
  getKichThuoc,
  createKichThuoc,
  deleteKichThuoc,
  getDeGiay,
  getDayGiay,
  getChatLieu,
  getXuatXu,
};
