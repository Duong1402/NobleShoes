<!-- Setup đường dẫn điều hướng -->

<script setup>
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";

const route = useRoute();
const router = useRouter();

//Lấy danh sách route cha/con có meta.title để hiển thị breadcrumb
const breadcrumbs = computed(() => {
  const matched = route.matched.filter((r) => r.meta && r.meta.title);
  const breadcrumbList = [];

  // build full path
  let fullPath = "";
  for (const m of matched) {
    fullPath += m.path.startsWith("/") ? m.path : "/" + m.path;
    breadcrumbList.push({
      name: m.meta.title,
      path: fullPath,
    });
  }

  // Nếu có breadcrumbParent, thêm thủ công route cha
  const current = route.meta;
  if (current.breadcrumbParent) {
    breadcrumbList.splice(breadcrumbList.length - 1, 0, {
      name: "Nhân viên",
      path: current.breadcrumbParent,
    });
  }

  return breadcrumbList;
});

// 🏠 Khi click Admin, quay về trang chính admin
const goHome = () => router.push({ name: "home"});
</script>

<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb mb-1">
      <li class="breadcrumb-item">
        <a
          href="#"
          @click.prevent="goHome"
          class="text-decoration-none text-warning"
        >
          <i class="fa fa-home me-1"></i> Trang chủ
        </a>
      </li>

      <li
        v-for="(item, index) in breadcrumbs"
        :key="index"
        class="breadcrumb-item"
        :class="{ active: index === breadcrumbs.length - 1 }"
        aria-current="page"
      >
        <template v-if="index !== breadcrumbs.length - 1">
          <router-link
            :to="item.path"
            class="text-decoration-none text-warning"
          >
            {{ item.name }}
          </router-link>
        </template>
        <template v-else>
          {{ item.name }}
        </template>
      </li>
    </ol>
  </nav>
</template>

<style scoped>
.breadcrumb {
  background-color: transparent;
  padding: 0;
  margin-bottom: 0;
  font-size: 0.95rem;
}

.breadcrumb-item + .breadcrumb-item::before {
  content: "›";
  color: #6c757d;
}

.breadcrumb-item a:hover {
  color: var(--custom-orange);
}
</style>
