import axios from "axios";

const API_URL_BAN_HANG = "http://localhost:8080/admin/ban-hang";
const API_URL_THANH_TOAN = "http://localhost:8080/admin/phuong-thuc-thanh-toan";
const API_URL_SAN_PHAM = "http://localhost:8080/admin/chi-tiet-san-pham";

export const getAllPhuongThucThanhToan = () => axios.get(API_URL_THANH_TOAN);
export const themPhuongThucMoi = (ptttData) => {
  return axios.post(API_URL_THANH_TOAN, ptttData);
};

export const taoHoaDon = () => {
  return axios.post(`${API_URL_BAN_HANG}/tao-hoa-don`);
};

export const huyHoaDon = (idHoaDon) => {
  return axios.put(`${API_URL_BAN_HANG}/huy-hoa-don/${idHoaDon}`);
};

export const themSanPhamVaoHoaDon = (idHoaDon, idChiTietSanPham, soLuong) => {
  return axios.post(`${API_URL_BAN_HANG}/them-san-pham`, null, {
    params: { idHoaDon, idChiTietSanPham, soLuong },
  });
};

export const capNhatKhachHang = (idHoaDon, idKhachHang) => {
  return axios.put(
    `${API_URL_BAN_HANG}/hoa-don/${idHoaDon}/cap-nhat-khach-hang/${idKhachHang}`,
    null
  );
};

export const apDungGiamGia = (idHoaDon, idPhieuGiamGia) => {
  return axios.put(
    `${API_URL_BAN_HANG}/hoa-don/${idHoaDon}/giam-gia/${idPhieuGiamGia}`,
    null,
    { params: { idHoaDon, idPhieuGiamGia } }
  );
};

export const thanhToan = (idHoaDon, requestData) => {
  return axios.post(
    `${API_URL_BAN_HANG}/hoa-don/${idHoaDon}/thanh-toan`,
    requestData,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
};

export const getChiTietHoaDon = (idHoaDon) => {
  return axios.get(`${API_URL_BAN_HANG}/${idHoaDon}/chi-tiet`);
};

// Lấy danh sách sản phẩm
export async function getDanhSachSanPham() {
  try {
    const res = await axios.get(API_URL_SAN_PHAM);
    return res;
  } catch (err) {
    console.error("❌ Lỗi khi lấy danh sách sản phẩm:", err);
    throw err;
  }
}

export const apDungKhuyenMaiTuDong = (idHoaDon) => {
  return axios.post(
    `${API_URL_BAN_HANG}/ap-dung-khuyen-mai-tu-dong/${idHoaDon}`
  );
};

// Xóa sản phẩm khỏi hóa đơn
export const xoaSanPhamKhoiHoaDon = (idHoaDon, idChiTietSanPham) => {
  return axios.delete(
    `${API_URL_BAN_HANG}/${idHoaDon}/chi-tiet/${idChiTietSanPham}`
  );
};

export const timKhachHangBySdt = (sdt) => {
  return axios.get(`${API_URL_BAN_HANG}/khach-hang/sdt/${sdt}`);
};

export const themKhachHangMoi = (khachHangData) => {
  return axios.post(`${API_URL_BAN_HANG}/khach-hang/them-nhanh`, khachHangData);
};

export const timKhachHangDaDangKy = (keyword) => {
  return axios.get(`${API_URL_BAN_HANG}/khach-hang/tim-kiem/${keyword}`);
};

export const getHoaDonById = (idHoaDon) => {
  return axios.get(`${API_URL_BAN_HANG}/hoa-don/${idHoaDon}`);
};

export const capNhatThongTinHoaDon = (idHoaDon, requestData) => {
  return axios.put(`${API_URL_BAN_HANG}/hoa-don/${idHoaDon}`, requestData);
};