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
import BanHangTaiQuay from "@/view/banHang/banHangTaiQuay.vue";
import { useAuthStore } from "@/components/login/authStore";

// Trang client (người mua)
import ClientLayout from "@/components/layout/ClientLayout.vue";
import TrangChuClient from "@/view/client/TrangChuClient.vue";
import ThongKe from "@/view/thongKe/ThongKe.vue";

const listRouter = [
  //Login customer
  {
    path: "/login-customer",
    name: "loginCustomer",
    component: () => import("@/components/login/customerLogin.vue"),
  },
  // Login employee
  {
    path: "/login-employee",
    name: "loginEmployee",
    component: () => import("@/components/login/employeeLogin.vue"),
  },

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

      // Sản phẩm
      {
        path: "san-pham",
        name: "SanPham",
        component: SanPham,
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
        name: "nhanVien",
        component: NhanVien,
        meta: { title: "Nhân viên" },
      },
      {
        path: "nhan-vien/them-nhan-vien",
        name: "nhanVienAdd",
        path: "nhan-vien/them-nhan-vien",
        name: "nhanVienAdd",
        component: NhanVienAdd,
        meta: { title: "Thêm nhân viên", breadcrumbParent: "/admin/nhan-vien" },
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
        component: QuanLyHoaDon,
        meta: { title: "Quản lý Hóa đơn" },
      },
      {
        path: "hoa-don/:id",
        name: "ChiTietHD",
        component: ChiTietHD,
        meta: { title: "Chi tiết hóa đơn" },
      },
      {
        path: "ban-hang",
        name: "BanHang",
        component: BanHangTaiQuay,
        meta: { title: "Bán hàng tại quầy" },
      },
      {
        path: "thong-ke",
        name: "ThongKe",
        component: ThongKe,
        meta: { title: "Thống kê" },
      },
    ],
    meta: { requiresAuth: true, role: "EMPLOYEE" },
  },
  {
    path: "/shop",
    component: ClientLayout,
    children: [
      {
        path: "",
        name: "TrangChuClient",
        component: TrangChuClient,
        meta: { title: "Trang chủ khách hàng" },
      },
    ],
    meta: { requiresAuth: true, role: "CUSTOMER" },
  },
  {
    path: "/",
    redirect: "/login-customer",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes: listRouter,
  routes: listRouter,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isLoggedIn = authStore.isLoggedIn;
  const isEmployee = authStore.isEmployee; // (Role ADMIN hoặc EMPLOYEE)
  const isCustomer = authStore.isCustomer;

  // 1. LOGIC ĐĂNG NHẬP CHÉO (Cross-Login)
  // Nếu Admin cố vào login Khách -> Logout Admin
  if (to.path === "/login-customer" && isLoggedIn && isEmployee) {
    authStore.logout();
    return next();
  }
  // Nếu Khách cố vào login Admin -> Logout Khách
  if (to.path === "/login-employee" && isLoggedIn && isCustomer) {
    authStore.logout();
    return next();
  }

  // 2. CHẶN NGƯỜI ĐÃ ĐĂNG NHẬP VÀO LẠI TRANG LOGIN (Redirect Forward)
  if (isLoggedIn) {
    if (to.path === "/login-customer" && isCustomer) {
      return next("/"); // Khách đã login -> Về trang chủ
    }
    if (to.path === "/login-employee" && isEmployee) {
      return next("/admin"); // NV đã login -> Về Admin
    }
  }

  // 3. KIỂM TRA XÁC THỰC VÀ QUYỀN

  // 3.1. CHƯA ĐĂNG NHẬP (UNAUTHENTICATED)
  if (to.meta.requiresAuth && !isLoggedIn) {
    // Xác định trang đăng nhập đích
    const targetLoginPath = to.path.startsWith("/admin")
      ? "/login-employee"
      : "/login-customer";

    // Nếu đã ở trang login rồi, cho phép load trang đó (thoát khỏi guard)
    if (to.path === targetLoginPath) {
      return next();
    }

    // Nếu đang cố gắng vào trang bảo mật khác -> Redirect về login đích
    return next(targetLoginPath);
  }

  // 3.2 ĐÃ ĐĂNG NHẬP, NHƯNG SAI QUYỀN (ROLE MISMATCH)
  if (isLoggedIn && to.meta.role) {
    // Chỉ kiểm tra nếu đã đăng nhập và có yêu cầu vai trò
    const requiredRole = to.meta.role;

    // Yêu cầu CUSTOMER mà là Employee
    if (requiredRole === "CUSTOMER" && !isCustomer) {
      // Redirect Employee về Admin Dashboard (vẫn là nhà của họ)
      return next("/admin");
    }

    // Yêu cầu ADMIN/EMPLOYEE mà là Customer
    if (
      (requiredRole === "EMPLOYEE" || requiredRole === "ADMIN") &&
      !isEmployee
    ) {
      // Redirect Customer về Trang chủ/Shop
      return next("/shop");
    }
  }

  // 4. Cho phép đi tiếp (Nếu không vi phạm gì)
  next();
});

export default router;
