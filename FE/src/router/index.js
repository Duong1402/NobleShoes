import { createRouter, createWebHistory } from "vue-router";

import HeaderLayout from "@/components/layout/headerLayout.vue";

// Sản phẩm
import SanPham from "@/view/sanPham/sanPham.vue";
import SanPhamAdd from "@/view/sanPham/sanPhamAdd.vue";
import ChiTietSanPham from "@/view/chiTietSanPham/chiTietSanPham.vue";

// Danh mục & các loại khác
import XuatXu from "@/view/xuatXu/xuatXu.vue";
import DanhMuc from "@/view/danhMuc/danhMuc.vue";
import DayGiay from "@/view/dayGiay/dayGiay.vue";
import DeGiay from "@/view/deGiay/deGiay.vue";
import MucDichSuDung from "@/view/mucDichSuDung/mucDichSuDung.vue";
import ThuongHieu from "@/view/thuongHieu/thuongHieu.vue";

// Nhân viên
import NhanVien from "@/view/nhanVien/nhanVien.vue";
import NhanVienAdd from "@/view/nhanVien/nhanVienAdd.vue";

// Nhân viên
import KhachHang from "@/view/khachHang/khachHang.vue";
import KhachHangAdd from "@/view/khachHang/khachHangAdd.vue";
import KhachHangDetail from "@/view/khachHang/khachHangDetail.vue";

//Giảm Giá
import PhieuGiamGia from "@/view/phieuGiamGia/phieuGiamGia.vue";
import PhieuGiamGiaAdd from "@/view/phieuGiamGia/phieuGiamGiaAdd.vue";
import DotGiamGia from "@/view/dotGiamGia/dotGiamGia.vue";
import DotGiamGiaAdd from "@/view/dotGiamGia/dotGiamGiaAdd.vue";
import NhanVienDetail from "@/view/nhanVien/nhanVienDetail.vue";

//Hóa Đơn

import TrangChu from "@/components/trangChu.vue";
import QuanLyHoaDon from "@/view/hoaDon/QuanLyHoaDon.vue";
import ChiTietHD from "@/view/hoaDon/ChiTietHD.vue";


const listRouter = [
  {
    path: "/admin",
    component: HeaderLayout,
    children: [
      {
        path: "",
        name: "home",
        component: TrangChu,
        meta: { title: "Trang chủ" },
      },

      // Sản phẩm
      {
        path: "san-pham",
        name: "SanPham",
        component: SanPham,
        meta: { title: "Sản phẩm" },
      },
      {
        path: "san-pham/them",
        name: "SanPhamAdd",
        component: SanPhamAdd,
        meta: { title: "Thêm sản phẩm" },
      },
      {
        path: "san-pham/chi-tiet/:id",
        name: "ChiTietSanPham",
        component: ChiTietSanPham,
        props: true,
        meta: { title: "Chi tiết sản phẩm" },
      },

      // Các loại khác
      {
        path: "xuat-xu",
        name: "XuatXu",
        component: XuatXu,
        meta: { title: "Xuất Xứ" },
      },
      {
        path: "danh-muc",
        name: "DanhMuc",
        component: DanhMuc,
        meta: { title: "Danh Mục" },
      },
      {
        path: "day-giay",
        name: "DayGiay",
        component: DayGiay,
        meta: { title: "Dây Giầy" },
      },
      {
        path: "de-giay",
        name: "DeGiay",
        component: DeGiay,
        meta: { title: "Đế Giầy" },
      },
      {
        path: "muc-dich-su-dung",
        name: "MucDichSuDung",
        component: MucDichSuDung,
        meta: { title: "Mục đích sử dụng" },
      },
      {
        path: "thuong-hieu",
        name: "ThuongHieu",
        component: ThuongHieu,
        meta: { title: "Thương hiệu" },
      },

      // Nhân viên
      {
        path: "nhan-vien",
        name: "nhanVien",
        component: NhanVien,
        meta: { title: "Nhân viên" },
      },
      {
        path: "nhan-vien/them-nhan-vien",
        name: "nhanVienAdd",
        component: NhanVienAdd,
        meta: { title: "Thêm nhân viên", breadcrumbParent: "/admin/nhan-vien" },
      },
      {
        path: "/admin/nhan-vien/:id",
        name: "chiTietNhanVien",
        component: NhanVienDetail,
        props: true,
        meta: {
          title: "Chi tiết nhân viên",
          breadcrumbParent: "/admin/nhan-vien",
        },
        meta: { title: "Thêm nhân viên", breadcrumbParent: "/admin/nhan-vien" },
      },
      {
        path: "/admin/nhan-vien/:id",
        name: "chiTietNhanVien",
        component: NhanVienDetail,
        props: true,
        meta: {
          title: "Chi tiết nhân viên",
          breadcrumbParent: "/admin/nhan-vien",
        },
      },

      //Khách hàng
      {
        path: "khach-hang",
        name: "khachHang",
        component: KhachHang,
        meta: { title: "Khách hàng" },
      },
      {
        path: "khach-hang/them",
        name: "KhachHangAdd",
        component: KhachHangAdd,
        meta: { title: "Thêm khách hàng" },
      },
      {
        path: "khach-hang/sua/:id",
        name: "KhachHangDetail",
        component: KhachHangDetail,
        props: true,
        meta: { title: "Sửa khách hàng" },
      },

      {
        path: "phieu-giam-gia",
        name: "PhieuGiamGia",
        component: PhieuGiamGia,
        meta: { title: "Phiếu giảm giá" },
      },
      {
        path: "phieu-giam-gia/add",
        name: "PhieuGiamGiaAdd",
        component: PhieuGiamGiaAdd,
        meta: { title: "Thêm phiếu giảm giá" },
      },
      {
        path: "dot-giam-gia",
        name: "DotGiamGia",
        component: DotGiamGia,
        meta: { title: "Thêm đợt giảm giá" },
      },
      {
        path: "dot-giam-gia/add",
        name: "DotGiamGiaAdd",
        component: DotGiamGiaAdd,
        meta: { title: "Thêm đợt giảm giá" },
      },
      {
        path: "hoa-don",
        name: "HoaDon",
        component: QuanLyHoaDon,
        meta: { title: "Quản lý Hóa đơn" },
      },
      {
        path: "hoa-don/:id",
        name: "ChiTietHD",
        component: ChiTietHD,
        meta: { title: "Chi tiết hóa đơn" },
      },

    ],
  },
  { path: "/", redirect: "/admin" },
];

const router = createRouter({
  history: createWebHistory(),
  routes: listRouter,
});

export default router;