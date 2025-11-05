import { createRouter, createWebHistory } from "vue-router";

import HeaderLayout from "@/components/layout/headerLayout.vue";
import TableData from "@/components/table/tableData.vue";
import TrangChu from "@/components/trangChu.vue";
import NhanVien from "@/view/nhanVien/nhanVien.vue";
import NhanVienAdd from "@/view/nhanVien/nhanVienAdd.vue";
import QuanLyHoaDon from "@/view/hoaDon/QuanLyHoaDon.vue";
import ChiTietHD from "@/view/hoaDon/ChiTietHD.vue";

const listRouter = [
  {
    path: "/admin",
    component: HeaderLayout,
    meta: { title: "Admin" },
    children: [
      {
        path: "",
        name: "home",
        component: TrangChu,
      },
      {
        path: "trang-chu",
        name: "TrangChu",
        component: TrangChu,
      },
      {
        path: "san-pham",
        name: "SanPham",
        component: TableData,
        meta: { title: "Sản phẩm" },
      },
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
        meta: {title: "Chi tiết hóa đơn"}
      }
    ],
  },

  {
    path: "/",
    redirect: "/admin",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes: listRouter, // truyền vào 1 list danh sách router
});

export default router;
