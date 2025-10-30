import { createRouter, createWebHistory } from "vue-router";

import HeaderLayout from "@/components/layout/headerLayout.vue";
import TableData from "@/components/table/tableData.vue";
import TrangChu from "@/components/trangChu.vue";
import NhanVien from "@/view/nhanVien/nhanVien.vue";
import NhanVienAdd from "@/view/nhanVien/nhanVienAdd.vue";
import PhieuGiamGia from "@/view/phieuGiamGia/phieuGiamGia.vue";
import PhieuGiamGiaCaNhan from "@/view/phieuGiamGiaCaNhan/phieuGiamGiaCaNhan.vue";
import PhieuGiamGiaAdd from "@/view/phieuGiamGia/phieuGiamGiaAdd.vue";
import PhieuGiamGiaCaNhanAdd from "@/view/phieuGiamGiaCaNhan/phieuGiamGiaCaNhanAdd.vue";
import DotGiamGia from "@/view/dotGiamGia/dotGiamGia.vue";
import DotGiamGiaAdd from "@/view/dotGiamGia/dotGiamGiaAdd.vue";

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
        path: "phieu-giam-gia-ca-nhan",
        name: "PhieuGiamGiaCaNhan",
        component: PhieuGiamGiaCaNhan,
        meta: { title: "Phiếu giảm giá cá nhân" },
      },
      {
        path: "phieu-giam-gia-ca-nhan/add",
        name: "PhieuGiamGiaCaNhanAdd",
        component: PhieuGiamGiaCaNhanAdd,
        meta: { title: "Thêm phiếu giảm giá cá nhân" },
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
