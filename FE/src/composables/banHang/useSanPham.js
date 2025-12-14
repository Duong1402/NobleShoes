// src/composables/banHang/useSanPham.js
import { ref, computed, watch, nextTick } from "vue";
import { getDanhSachSanPham } from "@/service/BanHangService";

export function useSanPham(notify) {
  const danhSachSanPham = ref([]);
  const filteredSanPham = ref([]);
  const searchSanPham = ref("");

  const filterMauSac = ref("");
  const filterKichThuoc = ref("");
  const filterXuatXu = ref("");

  const currentPage = ref(1);
  const itemsPerPage = 5;

  const loadSanPham = async () => {
    try {
      const res = await getDanhSachSanPham();

      console.log("🔥 Dữ liệu từ API:", res.data);

      if (res.data && res.data.length > 0) {
        const rawData = JSON.parse(JSON.stringify(res.data));

        danhSachSanPham.value = rawData.map((item) => {
          return {
            ...item,
            soLuong: 1,
          };
        });

        console.log("🔥 Dữ liệu sau khi Vue lưu:", danhSachSanPham.value[0]);
      } else {
        danhSachSanPham.value = [];
      }

      filterSanPham();
    } catch (err) {
      console.error(err);
    }
  };

  const filterSanPham = () => {
    if (!Array.isArray(danhSachSanPham.value)) {
      filteredSanPham.value = [];
      return;
    }
    const keyword = searchSanPham.value.trim().toLowerCase();

    filteredSanPham.value = danhSachSanPham.value.filter((sp) => {
      const ten = sp.tenSanPham?.toLowerCase() || sp.ten?.toLowerCase() || "";
      const ma = sp.ma?.toLowerCase() || "";
      const matchesSearch = ten.includes(keyword) || ma.includes(keyword);

      const valMauSac = sp.mauSac || "";
      const valKichThuoc = sp.kichThuoc || "";
      const valXuatXu = sp.tenXuatXu || "";

      const matchesMauSac =
        !filterMauSac.value || valMauSac === filterMauSac.value;
      const matchesKichThuoc =
        !filterKichThuoc.value || valKichThuoc === filterKichThuoc.value;
      const matchesXuatXu =
        !filterXuatXu.value || valXuatXu === filterXuatXu.value;

      return (
        sp.soLuongTon > 0 &&
        matchesSearch &&
        matchesMauSac &&
        matchesKichThuoc &&
        matchesXuatXu
      );
    });
  };

  const listMauSac = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.mauSac).filter(Boolean)),
  ]);
  const listKichThuoc = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.kichThuoc).filter(Boolean)),
  ]);
  const listXuatXu = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.tenXuatXu).filter(Boolean)),
  ]);

  const totalPages = computed(() =>
    Math.ceil(filteredSanPham.value.length / itemsPerPage)
  );
  const paginatedSanPham = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage;
    return filteredSanPham.value.slice(start, start + itemsPerPage);
  });

  const goToPage = (page) => {
    if (page >= 1 && page <= totalPages.value) currentPage.value = page;
  };

  watch([searchSanPham, filterMauSac, filterKichThuoc, filterXuatXu], () => {
    currentPage.value = 1;
    filterSanPham();
  });

  const handleValidateSoLuong = async (event, sanPhamHoacRef) => {
    const inputValue = event.target.value;
    let soLuongMoi = parseInt(inputValue);

    if (isNaN(soLuongMoi) || soLuongMoi <= 0) {
      notify.warning("Số lượng tối thiểu là 1!");
      soLuongMoi = 1;

      event.target.value = 1;
    }
    if (sanPhamHoacRef?.soLuongTon && soLuongMoi > sanPhamHoacRef.soLuongTon) {
      notify.warning(`Chỉ còn ${sanPhamHoacRef.soLuongTon} sản phẩm!`);
      soLuongMoi = sanPhamHoacRef.soLuongTon;
      event.target.value = soLuongMoi;
    }
    if (
      sanPhamHoacRef &&
      typeof sanPhamHoacRef === "object" &&
      "value" in sanPhamHoacRef
    ) {
      sanPhamHoacRef.value = soLuongMoi;
    } else if (sanPhamHoacRef && typeof sanPhamHoacRef === "object") {
      sanPhamHoacRef.soLuong = soLuongMoi;
    }
    await nextTick();
  };

  return {
    danhSachSanPham,
    filteredSanPham,
    searchSanPham,
    filterMauSac,
    filterKichThuoc,
    filterXuatXu,
    listMauSac,
    listKichThuoc,
    listXuatXu,
    currentPage,
    itemsPerPage,
    totalPages,
    paginatedSanPham,
    goToPage,
    loadSanPham,
    handleValidateSoLuong,
  };
}
