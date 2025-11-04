<template>
  <div class="sidebar" data-background-color="yellow">
    <div class="sidebar-logo">
      <div class="logo-header" data-background-color="yellow">
        <router-link :to="{ name: 'home' }" class="logo">
          <img
            src="/src/assets/img/logoo-Photoroom.png"
            alt="navbar brand"
            class="navbar-brand"
            height="140"
          />
        </router-link>
      </div>
    </div>

    <div class="sidebar-wrapper scrollbar scrollbar-inner">
      <div class="sidebar-content">
        <ul class="nav nav-warning">
          <!-- Trang chủ -->
          <li class="nav-item" :class="{ active: activeRoute === 'home' }">
            <router-link :to="{ name: 'home' }" class="nav-link link-with-icon">
              <div class="icon-title">
                <i class="fas fa-home"></i>
                <p class="mb-0">Trang chủ</p>
              </div>
            </router-link>
          </li>

          <!-- Tiêu đề nhóm -->
          <li class="nav-section">
            <span class="sidebar-mini-icon">
              <i class="fa fa-ellipsis-h"></i>
            </span>
            <h4 class="text-section">Components</h4>
          </li>

          <!-- Mẫu menu có submenu -->
         
          <li
            v-for="item in menus"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeRoute === item.routeName }"
          >
            <a
              href="#"
              class="nav-link link-with-icon d-flex align-items-center"
              @click.prevent="toggleCollapse(item.key)"
            >
              <div
                class="icon-title d-flex align-items-center gap-2 flex-grow-1"
              >
                <i :class="item.icon"></i>
                <span class="title-text">{{ item.title }}</span>
              </div>
              <div
                class="caret-wrapper d-flex align-items-center justify-content-center"
              >
                <i
                  class="fa-solid fa-caret-down caret-icon"
                  :class="{ rotated: isOpen(item.key) }"
                ></i>
              </div>
            </a>

            <!-- smoother slide (use max-height transition + fade) -->
            <transition name="submenu">
              <div v-show="isOpen(item.key)" class="submenu-wrapper">
                <ul class="nav nav-collapse">
                  <li v-for="sub in item.children" :key="sub.text">
                    <router-link
                      v-if="sub.route"
                      :to="{ name: sub.route }"
                      class="submenu-link"
                    >
                      <span class="sub-item">{{ sub.text }}</span>
                    </router-link>
                    <a v-else href="#" class="submenu-link">
                      <span class="sub-item">{{ sub.text }}</span>
                    </a>
                  </li>
                </ul>
              </div>
            </transition>
          </li>

          <!-- Quản lý nhân viên -->
          <!-- <li class="nav-item" :class="{ active: activeRoute === 'nhanVien' }">
            <router-link
              :to="{ name: 'nhanVien' }"
              class="nav-link link-with-icon"
            >
              <div class="icon-title">
                <i class="fas fa-user-alt"></i>
                <p class="mb-0">Quản lý nhân viên</p>
              </div>
            </router-link>
          </li> -->
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRoute } from "vue-router";

const openMenu = ref("");
const route = useRoute();
const activeRoute = ref(route.name);

const toggleCollapse = (menu) => {
  openMenu.value = openMenu.value === menu ? "" : menu;
};
const isOpen = (menu) => openMenu.value === menu;

// Theo dõi route thay đổi
watch(
  () => route.name,
  (newName) => {
    activeRoute.value = newName;
  },
  { immediate: true }
);

const menus = [
  {
    key: "hoaDon",
    title: "Quản lý hóa đơn",
    icon: "fas fa-file",
    routeName: "HoaDon",
    children: [{ text: "Hóa Đơn",route: "HoaDon" }],
  },
  {
    key: "giamGia",
    title: "Quản lý giảm giá",
    icon: "fas fa-tag",
    routeName: "PhieuGiamGia",
    children: [
      { text: "Phiếu giảm giá", route: "PhieuGiamGia" },
      { text: "Đợt giảm giá", route: "DotGiamGia" },
    ],
  },
  {
    key: "SanPham",
    title: "Quản lý sản phẩm",
    icon: "fas fa-cube",
    routeName: "SanPham",
    children: [
      { text: "Sản phẩm", route: "SanPham" },
      { text: "Danh mục", route: "DanhMuc" },
      { text: "Mục đích sử dụng", route: "MucDichSuDung" },
      { text: "Dây giầy", route: "DayGiay" },
      { text: "Đế giày", route: "DeGiay" },
      { text: "Thương hiệu", route: "ThuongHieu" },
      { text: "Xuất xứ", route: "XuatXu" },
    ],
  },
  {
    key: "taiKhoan",
    title: "Tài khoản",
    icon: "fa-solid fa-users",
    routeName: "nhanVien",
    children: [
      { text: "Nhân viên",  route: "nhanVien"}, 
      { text: "Khách hàng" , route: "khachHang"}],
  },
  
];
</script>

<style scoped>
/* base sidebar */
.sidebar {
  width: 260px;
  background-color: #ffffff;
  min-height: 100vh;
  transition: all 0.28s ease;
}

/* link layout */
.link-with-icon {
  color: #212529;
  font-weight: 500;
  display: flex;
  align-items: center;
  padding: 0.6rem 0.9rem;
  border-radius: 8px;
  transition: transform 0.18s ease, background-color 0.18s ease,
    box-shadow 0.18s;
}

/* icon + text group */
.icon-title {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

/* hover effect: subtle lift + bg */
.link-with-icon:hover {
  transform: translateX(6px);
  background-color: rgba(255, 193, 7, 0.12);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

/* active bg */
.nav-item.active .link-with-icon {
  background-color: rgba(255, 193, 7, 0.22);
}

/* icon sizing */
.icon-title i,
.nav-link i {
  font-size: 16px;
  width: 22px;
  text-align: center;
  color: #2d2d2d;
}

/* caret */
.caret {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.28s cubic-bezier(0.2, 0.9, 0.3, 1);
  color: #555;
}
.caret.rotated {
  transform: rotate(180deg);
}

/* submenu wrapper uses max-height trick for smooth slide */
.submenu-wrapper {
  overflow: hidden;
  will-change: max-height, opacity;
}

/* submenu list */
.nav-collapse {
  padding-left: 1.5rem;
}
.submenu-link {
  display: block;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.18s, transform 0.18s;
  color: #343a40;
}

/* hover for submenu items */
.submenu-link:hover {
  background-color: rgba(0, 0, 0, 0.04);
  transform: translateX(6px);
  color: #000;
}

/* sub-item text */
.sub-item {
  font-size: 14px;
}

/* Transition classes for the <transition name="submenu"> */
.submenu-enter-active,
.submenu-leave-active {
  transition: all 280ms cubic-bezier(0.2, 0.9, 0.3, 1);
}
.submenu-enter-from,
.submenu-leave-to {
  opacity: 0;
  max-height: 0;
  transform: translateY(-6px);
}
.submenu-enter-to,
.submenu-leave-from {
  opacity: 1;
  max-height: 400px; /* large enough to show items */
  transform: translateY(0);
}

/* small polish */
.nav-section {
  padding: 0.6rem 0.9rem;
  color: #6c6c6c;
}
.text-section {
  margin: 0;
}

/* ensure p tags don't add extra margin */
.icon-title p,
.icon-title .title-text {
  margin: 0;
  line-height: 1;
}

/* responsive minor: reduce translate on small screens */
@media (max-width: 576px) {
  .link-with-icon:hover {
    transform: none;
  }
  .submenu-enter-active,
  .submenu-leave-active {
    transition-duration: 220ms;
  }
}
.caret-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.28s cubic-bezier(0.2, 0.9, 0.3, 1);
  color: #555;
}
.caret-icon.rotated {
  transform: rotate(180deg);
}
.caret-wrapper {
  width: 20px; /* giữ 1 cột cố định */
  display: flex;
  justify-content: center;
  align-items: center;
}

.caret-icon {
  font-size: 12px;
  color: #888;
  transition: transform 0.25s ease;
}

.caret-icon.rotated {
  transform: rotate(180deg);
}

.nav-item.active .nav-link {
  position: relative;
  background-color: rgba(255, 193, 7, 0.1);
  font-weight: 600;
  color: #000;
}

.nav-item.active .nav-link::before {
  content: "";
  position: absolute;
  left: 0;
  top: 8%;
  height: 80%;
  width: 3px;
  background-color: #ffc107;
  border-radius: 3px;
  transition: all 0.3s ease;
}
.nav-collapse .submenu-link.router-link-active {
  color: #000;
  font-weight: 600;
  background-color: rgba(255, 193, 7, 0.15);
}
</style>