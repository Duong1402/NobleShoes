import instance from "./axios";

const API_URL = "http://localhost:8080/admin/ban-hang";

export const taoHoaDon = (idNhanVien) => {
  return instance.post(`${API_URL}/tao-hoa-don/${idNhanVien}`);
};

export const huyHoaDon = (idHoaDon) => {
  return instance.put(`${API_URL}/huy-hoa-don/${idHoaDon}`);
};

export const themSanPhamVaoHoaDon = (idHoaDon, idChiTietSanPham, soLuong) => {
  return instance.post(`${API_URL}/them-san-pham`, null, {
    params: { idHoaDon, idChiTietSanPham, soLuong },
  });
};

export const capNhatKhachHang = (idHoaDon, idKhachHang) => {
  return instance.put(
    `${API_URL}/hoa-don/${idHoaDon}/cap-nhat-khach-hang/${idKhachHang}`,
    null
  );
};

export const apDungGiamGia = (idHoaDon, idPhieuGiamGia) => {
  return instance.put(
    `${API_URL}/hoa-don/${idHoaDon}/giam-gia/${idPhieuGiamGia}`,
    null,
    { params: { idHoaDon, idPhieuGiamGia } }
  );
};

export const thanhToan = (idHoaDon, requestData) => {
  return instance.post(`${API_URL}/hoa-don/${idHoaDon}/thanh-toan`, requestData, {
    headers: {
      "Content-Type": "application/json",
    },
  });
};

export const getChiTietHoaDon = (idHoaDon) => {
  return instance.get(`${API_URL}/${idHoaDon}/chi-tiet`);
};

// Lấy danh sách sản phẩm
export async function getDanhSachSanPham() {
  try {
    const res = await instance.get(
      "http://localhost:8080/admin/chi-tiet-san-pham"
    );
    return res;
  } catch (err) {
    console.error("❌ Lỗi khi lấy danh sách sản phẩm:", err);
    throw err;
  }
}

export const apDungKhuyenMaiTuDong = (idHoaDon) => {
  return instance.post(`${API_URL}/ap-dung-khuyen-mai-tu-dong/${idHoaDon}`);
};

// Xóa sản phẩm khỏi hóa đơn
export const xoaSanPhamKhoiHoaDon = (idHoaDon, idChiTietSanPham) => {
  return instance.delete(`${API_URL}/${idHoaDon}/chi-tiet/${idChiTietSanPham}`);
};

export const timKhachHangBySdt = (sdt) => {
  return instance.get(`${API_URL}/khach-hang/sdt/${sdt}`);
};

export const themKhachHangMoi = (khachHangData) => {
  return instance.post(`${API_URL}/khach-hang/them-nhanh`, khachHangData);
};

export const timKhachHangDaDangKy = (keyword) => {
  return instance.get(`${API_URL}/khach-hang/tim-kiem/${keyword}`);
};


export const getHoaDonById = (id) => {
  return instance.get(`${API_URL}/hoa-don/${id}`); 
};