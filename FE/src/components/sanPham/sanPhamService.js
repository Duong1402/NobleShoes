// sanPhamService.js
import { v4 as uuidv4 } from 'uuid';

// Lookup data với ID cố định
export const thuongHieuList = [
  { id: 'TH01', ten: 'Nike' },
  { id: 'TH02', ten: 'Adidas' },
];

export const danhMucList = [
  { id: 'DM01', ten: 'Giày thể thao' },
  { id: 'DM02', ten: 'Giày cao gót' },
];

export const xuatXuList = [
  { id: 'XX01', ten: 'Việt Nam' },
  { id: 'XX02', ten: 'USA' },
];

export const deGiayList = [
  { id: 'DG01', ten: 'Da' },
  { id: 'DG02', ten: 'Vải' },
];

export const dayGiayList = [
  { id: 'DY01', ten: 'Dây vải' },
  { id: 'DY02', ten: 'Dây da' },
];

export const mucDichSuDungList = [
  { id: 'MD01', ten: 'Thường ngày' },
  { id: 'MD02', ten: 'Thể thao' },
];

export const mauSacList = [
  { id: 'MS01', ten: 'Đỏ' },
  { id: 'MS02', ten: 'Xanh' },
];

export const kichThuocList = [
  { id: 'KT01', ten: '38' },
  { id: 'KT02', ten: '39' },
];

export const chatLieuList = [
  { id: 'CL01', ten: 'Da thật' },
  { id: 'CL02', ten: 'Vải' },
];

// Fake DB
let sanPhamList = [];
let chiTietSanPhamList = [];

// Lấy tất cả sản phẩm + chi tiết
export const getAllSanPham = async () => {
  return {
    data: sanPhamList.map(sp => ({
      ...sp,
      chi_tiet: chiTietSanPhamList.find(c => c.id_san_pham === sp.id)
    }))
  };
};

// Tạo sản phẩm + chi tiết
export const createSanPham = async (sp) => {
  const newSP = { ...sp, id: uuidv4(), ngay_tao: new Date(), ngay_cap_nhat: new Date() };
  sanPhamList.push(newSP);

  const chiTiet = {
    id: uuidv4(),
    id_san_pham: newSP.id,
    id_mau_sac: sp.id_mau_sac || null,
    id_kich_thuoc: sp.id_kich_thuoc || null,
    id_chat_lieu: sp.id_chat_lieu || null,
    gia_nhap: sp.gia_nhap || 0,
    gia_ban: sp.gia_ban || 0,
    so_luong_ton: sp.so_luong_ton || 0,
    ngay_tao: new Date(),
  };
  chiTietSanPhamList.push(chiTiet);

  return { data: { ...newSP, chi_tiet: chiTiet } };
};

// Cập nhật sản phẩm + chi tiết
export const updateSanPham = async (id, sp) => {
  const index = sanPhamList.findIndex(p => p.id === id);
  if (index !== -1) {
    sanPhamList[index] = { ...sanPhamList[index], ...sp, ngay_cap_nhat: new Date() };

    const chiIndex = chiTietSanPhamList.findIndex(c => c.id_san_pham === id);
    if (chiIndex !== -1) {
      chiTietSanPhamList[chiIndex] = { 
        ...chiTietSanPhamList[chiIndex],
        id_mau_sac: sp.id_mau_sac || chiTietSanPhamList[chiIndex].id_mau_sac,
        id_kich_thuoc: sp.id_kich_thuoc || chiTietSanPhamList[chiIndex].id_kich_thuoc,
        id_chat_lieu: sp.id_chat_lieu || chiTietSanPhamList[chiIndex].id_chat_lieu,
        gia_nhap: sp.gia_nhap || chiTietSanPhamList[chiIndex].gia_nhap,
        gia_ban: sp.gia_ban || chiTietSanPhamList[chiIndex].gia_ban,
        so_luong_ton: sp.so_luong_ton || chiTietSanPhamList[chiIndex].so_luong_ton,
      };
    }

    return { data: { ...sanPhamList[index], chi_tiet: chiTietSanPhamList[chiIndex] } };
  }
  return null;
};

// Xóa sản phẩm + chi tiết
export const deleteSanPhamById = async (id) => {
  sanPhamList = sanPhamList.filter(p => p.id !== id);
  chiTietSanPhamList = chiTietSanPhamList.filter(c => c.id_san_pham !== id);
  return { data: true };
};
