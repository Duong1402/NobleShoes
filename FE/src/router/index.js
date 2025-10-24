import { createRouter, createWebHistory } from "vue-router";

import HeaderLayout from "@/components/layout/headerLayout.vue";
import TrangChu from "@/components/trangChu.vue";

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

const listRouter = [
  {
    path: "/admin",
    component: HeaderLayout,
    meta: { title: "Admin" },
    children: [
      { path: "", name: "home", component: TrangChu },
      { path: "trang-chu", name: "TrangChu", component: TrangChu },

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
        name: "NhanVien",
        component: NhanVien,
        meta: { title: "Nhân viên" },
      },
      {
        path: "nhan-vien/them",
        name: "NhanVienAdd",
        component: NhanVienAdd,
        meta: { title: "Thêm nhân viên" },
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
