import { createRouter, createWebHistory } from "vue-router";

import HeaderLayout from "@/components/layout/headerLayout.vue";
import TrangChu from "@/components/trangChu.vue";
import NhanVien from "@/view/nhanVien/nhanVien.vue";
import NhanVienAdd from "@/view/nhanVien/nhanVienAdd.vue";
import TableData from "@/view/table/tableData.vue";

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
      {
        path: "san-pham",
        name: "sanPham",
        component: TableData,
        meta: { title: "Sản phẩm" },
      },
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
      { name: "banHang", path: "/ban-hang" },
      { name: "hoaDon", path: "/hoa-don" },
      { name: "giamGia", path: "/giam-gia" },
      { name: "thongKe", path: "/thong-ke" },
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
