import { createRouter, createWebHistory } from "vue-router";

// Layout
import HeaderLayout from "@/components/layout/headerLayout.vue";

// Lazy-load views để tối ưu bundle
const TrangChu          = () => import("@/components/trangChu.vue");
const TableData         = () => import("@/components/table/tableData.vue");
const KhachHang         = () => import("@/view/khachHang/khachHang.vue");
const KhachHangAdd      = () => import("@/view/khachHang/khachHangAdd.vue");
const KhachHangDetail   = () => import("@/view/khachHang/khachHangDetail.vue");

const routes = [
  {
    path: "/admin",
    component: HeaderLayout,
    meta: { title: "Admin" },
    children: [
      { path: "",            name: "Home",           component: TrangChu,        meta: { title: "Trang chủ" } },
      { path: "trang-chu",   name: "TrangChu",       component: TrangChu,        meta: { title: "Trang chủ" } },
      { path: "san-pham",    name: "SanPham",        component: TableData,       meta: { title: "Sản phẩm" } },
      { path: "khach-hang",  name: "KhachHang",      component: KhachHang,       meta: { title: "Khách hàng" } },
      { path: "khach-hang/them",
                            name: "KhachHangAdd",    component: KhachHangAdd,    meta: { title: "Thêm khách hàng" } },
      { path: "khach-hang/sua/:id",
                            name: "KhachHangDetail", component: KhachHangDetail, props: true, meta: { title: "Sửa khách hàng" } },
    ],
  },

  // Trang chủ -> /admin
  { path: "/", redirect: "/admin" },

  // Fallback -> danh sách khách hàng
  { path: "/:pathMatch(.*)*", redirect: "/admin/khach-hang" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0, left: 0 };
  },
});

// Tự động set document.title theo meta.title (nếu có)
router.beforeEach((to, _from, next) => {
  const base = "Admin";
  const page = to.meta?.title ? ` | ${to.meta.title}` : "";
  document.title = `${base}${page}`;
  next();
});

export default router;
