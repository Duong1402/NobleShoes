import { ref, watch } from "vue";
import {
  taoHoaDon,
  huyHoaDon as apiHuyHoaDon,
  apDungKhuyenMaiTuDong, // Đảm bảo đã import API này
} from "@/service/BanHangService";
import Swal from "sweetalert2";

export function useHoaDon(notify, idNhanVien, resetGiaoHangCallback) {
  const hoaDonChoList = ref([]);
  const selectedHoaDonId = ref(null);
  const hoaDon = ref(null);

  // ID Khách lẻ mặc định (Constant)
  const KHACH_LE_ID = "0F773ECB-16F4-4DE2-96F1-115BECAE963E";

  // 1. Load danh sách hóa đơn chờ từ LocalStorage
  const savedListJson = localStorage.getItem("hoaDonChoList");
  if (savedListJson) {
    try {
      hoaDonChoList.value = JSON.parse(savedListJson) || [];
    } catch (e) {
      console.error("Lỗi parse hoaDonChoList:", e);
      hoaDonChoList.value = [];
    }
  }

  // 2. Load ID đang chọn từ LocalStorage
  const savedIdStr = localStorage.getItem("selectedHoaDonId");
  const savedSelectedId = ref(savedIdStr || null);

  // --- HÀM CHỌN HÓA ĐƠN ---
  const selectHoaDon = (id) => {
    const found = hoaDonChoList.value.find((h) => h.id === id);
    if (found) {
      selectedHoaDonId.value = id;
      hoaDon.value = found;
      localStorage.setItem("selectedHoaDonId", id);
    } else {
      selectedHoaDonId.value = null;
      hoaDon.value = null;
      localStorage.removeItem("selectedHoaDonId");
    }
  };

  // --- HÀM TẠO HÓA ĐƠN MỚI ---
  const handleTaoHoaDon = async () => {
    if (hoaDonChoList.value.length >= 5) {
      Swal.fire("Giới hạn 5 hóa đơn chờ!", "", "warning");
      return;
    }

    try {
      const res = await taoHoaDon(idNhanVien);
      const newHoaDon = {
        ...res.data,
        tongSoLuong: 0,
        sanPhamList: [],
        khachHang: {
          id: KHACH_LE_ID,
          hoTen: "Khách lẻ",
          sdt: "0000000000",
        },
      };

      hoaDonChoList.value.push(newHoaDon);
      selectHoaDon(newHoaDon.id);

      if (resetGiaoHangCallback) resetGiaoHangCallback();

      notify.success("Tạo hóa đơn mới thành công!");
    } catch (err) {
      console.error(err);
      notify.error("Tạo hóa đơn thất bại!");
    }
  };

  // --- HÀM HỦY HÓA ĐƠN ---
  const handleHuyHoaDon = async (id) => {
    const confirm = await Swal.fire({
      title: "Hủy hóa đơn này?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Hủy",
      confirmButtonColor: "#d33",
    });

    if (!confirm.isConfirmed) return;

    try {
      await apiHuyHoaDon(id);
      hoaDonChoList.value = hoaDonChoList.value.filter((hd) => hd.id !== id);

      if (selectedHoaDonId.value === id) {
        selectedHoaDonId.value = null;
        hoaDon.value = null;
        localStorage.removeItem("selectedHoaDonId");
      }

      notify.success("Đã hủy hóa đơn!");
    } catch (err) {
      console.error(err);
      notify.error("Không thể hủy hóa đơn!");
    }
  };

  // --- HÀM ÁP DỤNG KHUYẾN MÃI (ĐÃ SỬA) ---
  const handleApDungKhuyenMai = async () => {
    if (!selectedHoaDonId.value) {
      return notify.warning("Chưa chọn hóa đơn!");
    }

    // Kiểm tra khách lẻ
    if (!hoaDon.value?.khachHang || hoaDon.value.khachHang.id === KHACH_LE_ID) {
      return notify.warning(
        "Vui lòng chọn khách hàng thành viên để dùng mã giảm giá!"
      );
    }

    try {
      // Gọi API
      const res = await apDungKhuyenMaiTuDong(selectedHoaDonId.value);

      console.log("🔥 API Trả về:", res.data);
      console.log("🔥 Có phiếu giảm giá không?", res.data?.phieuGiamGia);

      // Xử lý kết quả trả về
      // TH1: Backend trả về String (Ví dụ: "Không có mã phù hợp")
      if (typeof res.data === "string") {
        notify.info(res.data);
        // Nếu BE reset phiếu về null, ta cũng nên reset UI thủ công hoặc reload lại hóa đơn
        // Nhưng tốt nhất là BE nên trả về Object HoaDon kể cả khi không tìm thấy (trả về HD đã reset)
      }
      // TH2: Backend trả về Object Hóa Đơn (Đã cập nhật tiền giảm) - KHUYÊN DÙNG
      else if (typeof res.data === "object" && res.data.id) {
        const updatedHoaDon = res.data;

        // 1. Cập nhật vào view chi tiết
        // Giữ lại danh sách sản phẩm ở FE để tránh bị mất thông tin hiển thị (size/màu) nếu BE trả thiếu
        hoaDon.value = {
          ...hoaDon.value, // Giữ cái cũ
          ...updatedHoaDon, // Đè cái mới (tongTienSauGiam, phieuGiamGia)
          sanPhamList: hoaDon.value.sanPhamList, // Ưu tiên giữ list SP đang hiển thị tốt ở FE
        };

        // 2. Cập nhật vào danh sách chờ (để sidebar cập nhật số tiền)
        const index = hoaDonChoList.value.findIndex(
          (h) => h.id === updatedHoaDon.id
        );
        if (index !== -1) {
          hoaDonChoList.value[index] = {
            ...hoaDonChoList.value[index],
            ...updatedHoaDon,
            sanPhamList: hoaDonChoList.value[index].sanPhamList, // Giữ list SP
          };
        }

        notify.success(
          `Đã áp dụng mã: ${updatedHoaDon.phieuGiamGia?.ten || "Ưu đãi"}`
        );
      }
    } catch (err) {
      console.error(err);
      const msg =
        err.response?.data?.message ||
        err.response?.data ||
        "Lỗi khi áp dụng khuyến mãi";
      notify.error(msg);
    }
  };

  // --- WATCHERS ---
  watch(
    hoaDonChoList,
    (val) => {
      localStorage.setItem("hoaDonChoList", JSON.stringify(val || []));
    },
    { deep: true }
  );

  watch(selectedHoaDonId, (newId) => {
    if (newId) {
      localStorage.setItem("selectedHoaDonId", newId);
    } else {
      localStorage.removeItem("selectedHoaDonId");
    }
  });

  return {
    hoaDonChoList,
    selectedHoaDonId,
    hoaDon,
    savedSelectedId,
    handleTaoHoaDon,
    selectHoaDon,
    handleHuyHoaDon,
    handleApDungKhuyenMai,
  };
}
