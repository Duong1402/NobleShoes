// src/composables/banHang/useSanPham.js
import { ref, computed, watch, nextTick } from "vue";
import { getDanhSachSanPham } from "@/service/BanHangService";

export function useSanPham(notify) {
  const danhSachSanPham = ref([]);
  const filteredSanPham = ref([]);
  const searchSanPham = ref("");

  // Filter state
  const filterMauSac = ref("");
  const filterKichThuoc = ref("");
  const filterXuatXu = ref("");

  // Pagination
  const currentPage = ref(1);
  const itemsPerPage = 5;

  // 1. Load sản phẩm
  const loadSanPham = async () => {
    try {
      const res = await getDanhSachSanPham();

      // IN RA ĐỂ KIỂM TRA NGAY LẬP TỨC
      console.log("🔥 Dữ liệu từ API:", res.data);

      if (res.data && res.data.length > 0) {
        // Cách copy an toàn nhất: JSON parse/stringify để xóa sạch Proxy
        const rawData = JSON.parse(JSON.stringify(res.data));

        danhSachSanPham.value = rawData.map((item) => {
          // Copy tất cả mọi thứ có trong item
          return {
            ...item,
            soLuong: 1, // Chỉ thêm cái này, còn lại giữ nguyên
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

  // 2. Logic Lọc
  const filterSanPham = () => {
    if (!Array.isArray(danhSachSanPham.value)) {
      filteredSanPham.value = [];
      return;
    }
    const keyword = searchSanPham.value.trim().toLowerCase();

    filteredSanPham.value = danhSachSanPham.value.filter((sp) => {
      const ten = sp.tenSanPham?.toLowerCase() || sp.ten?.toLowerCase() || "";
      const ma = sp.ma?.toLowerCase() || "";
      // ... (các điều kiện lọc khác giữ nguyên như code cũ)
      const matchesSearch = ten.includes(keyword) || ma.includes(keyword); // Rút gọn ví dụ

      // Logic màu sắc, kích thước...
      const valMauSac = sp.mauSac || "";
      const valKichThuoc = sp.kichThuoc || "";
      const valXuatXu = sp.tenXuatXu || ""; // Ưu tiên tenXuatXu

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

  // 3. Computed cho Filter Options
  const listMauSac = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.mauSac).filter(Boolean)),
  ]);
  const listKichThuoc = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.kichThuoc).filter(Boolean)),
  ]);
  const listXuatXu = computed(() => [
    ...new Set(danhSachSanPham.value.map((s) => s.tenXuatXu).filter(Boolean)),
  ]);

  // 4. Pagination Computed
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

  // Watchers
  watch([searchSanPham, filterMauSac, filterKichThuoc, filterXuatXu], () => {
    currentPage.value = 1;
    filterSanPham();
  });

  const handleValidateSoLuong = async (event, sanPhamHoacRef) => {
    // Lấy giá trị từ sự kiện input
    const inputValue = event.target.value;
    let soLuongMoi = parseInt(inputValue);

    // Trường hợp 1: Nhập ký tự không phải số, số âm, hoặc số 0
    if (isNaN(soLuongMoi) || soLuongMoi <= 0) {
      notify.warning("Số lượng tối thiểu là 1!");

      // Reset về 1
      soLuongMoi = 1;

      event.target.value = 1;
    }

    // Trường hợp 2: Nhập quá số lượng tồn (nếu có check tồn kho)
    // Giả sử sanPhamHoacRef là object sản phẩm có thuộc tính soLuongTon
    if (sanPhamHoacRef?.soLuongTon && soLuongMoi > sanPhamHoacRef.soLuongTon) {
      notify.warning(`Chỉ còn ${sanPhamHoacRef.soLuongTon} sản phẩm!`);
      soLuongMoi = sanPhamHoacRef.soLuongTon;
      event.target.value = soLuongMoi;
    }

    // Cập nhật lại model (nếu bạn truyền vào Ref hoặc Object)
    // Nếu sanPhamHoacRef là ref (dùng cho ô input đơn lẻ)
    if (
      sanPhamHoacRef &&
      typeof sanPhamHoacRef === "object" &&
      "value" in sanPhamHoacRef
    ) {
      sanPhamHoacRef.value = soLuongMoi;
    }
    // Nếu sanPhamHoacRef là object sản phẩm (dùng trong v-for danh sách)
    else if (sanPhamHoacRef && typeof sanPhamHoacRef === "object") {
      // Giả sử bạn bind vào biến 'soLuongMua' của sản phẩm
      sanPhamHoacRef.soLuong = soLuongMoi;
    }

    // Đợi Vue render xong để đảm bảo mọi thứ đồng bộ
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
