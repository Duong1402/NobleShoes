import axios from "axios";

const API_URL = "http://localhost:8080/admin/chi-tiet-san-pham";

// 🟢 Lấy tất cả chi tiết sản phẩm
export const getAllChiTietSanPham = async () => {
  const res = await axios.get(API_URL);
  return res.data;
};

// 🟢 Lấy chi tiết sản phẩm theo ID chi tiết
export const getChiTietSanPhamById = async (id) => {
  const res = await axios.get(`${API_URL}/${id}`);
  return res.data;
};

// 🟢 Lấy chi tiết sản phẩm theo ID sản phẩm (khi click icon 👁)
export const getChiTietSanPhamBySanPhamId = async (idSanPham) => {
  const res = await axios.get(`${API_URL}/san-pham/${idSanPham}`);
  return res.data;
};

// 🟢 Cập nhật chi tiết sản phẩm
export const updateChiTietSanPham = async (id, data) => {
  const res = await axios.put(`${API_URL}/${id}`, data);
  return res.data;
};

// 🟢 Xóa chi tiết sản phẩm
export const deleteChiTietSanPham = async (id) => {
  const res = await axios.delete(`${API_URL}/${id}`);
  return res.data;
};

// Danh mục
export const getAllDanhMuc = async () => {
  const res = await axios.get(`${BASE_URL}/danh-muc`);
  return res.data;
};

// Thương hiệu
export const getAllThuongHieu = async () => {
  const res = await axios.get(`${BASE_URL}/thuong-hieu`);
  return res.data;
};

// Xuất xứ
export const getAllXuatXu = async () => {
  const res = await axios.get(`${BASE_URL}/xuat-xu`);
  return res.data;
};

// Dây giầy
export const getAllDayGiay = async () => {
  const res = await axios.get(`${BASE_URL}/day-giay`);
  return res.data;
};

// Đế giầy
export const getAllDeGiay = async () => {
  const res = await axios.get(`${BASE_URL}/de-giay`);
  return res.data;
};

// Mục đích sử dụng
export const getAllMucDichSuDung = async () => {
  const res = await axios.get(`${BASE_URL}/muc-dich-su-dung`);
  return res.data;
};

// Màu sắc
export const getAllMauSac = async () => {
  const res = await axios.get(`${BASE_URL}/mau-sac`);
  return res.data;
};

// Kích thước
export const getAllKichThuoc = async () => {
  const res = await axios.get(`${BASE_URL}/kich-thuoc`);
  return res.data;
};

// Chất liệu
export const getAllChatLieu = async () => {
  const res = await axios.get(`${BASE_URL}/chat-lieu`);
  return res.data;
};
