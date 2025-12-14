import { ref, watch } from "vue";
import {
  taoHoaDon,
  huyHoaDon as apiHuyHoaDon,
  apDungKhuyenMaiTuDong,
} from "@/service/BanHangService";
import Swal from "sweetalert2";
import { khachLeInfo } from "./useKhachHang";

export function useHoaDon(notify, idNhanVien, resetGiaoHangCallback) {
  const hoaDonChoList = ref([]);
  const selectedHoaDonId = ref(null);
  const hoaDon = ref(null);

  const savedListJson = localStorage.getItem("hoaDonChoList");
  if (savedListJson) {
    try {
      hoaDonChoList.value = JSON.parse(savedListJson) || [];
    } catch (e) {
      console.error("Lỗi parse hoaDonChoList:", e);
      hoaDonChoList.value = [];
    }
  }

  const savedIdStr = localStorage.getItem("selectedHoaDonId");
  const savedSelectedId = ref(savedIdStr || null);

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
  const handleTaoHoaDon = async () => {
    if (hoaDonChoList.value.length >= 5) {
      Swal.fire("Giới hạn 5 hóa đơn chờ!", "", "warning");
      return;
    }

    try {
      const res = await taoHoaDon();
      const newHoaDon = {
        ...res.data,
        tongSoLuong: 0,
        sanPhamList: [],
        khachHang: res.data.khachHang || khachLeInfo.value,
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

const handleApDungKhuyenMai = async () => {
    if (!selectedHoaDonId.value) {
      return notify.warning("Chưa chọn hóa đơn!");
    }

    const currentKhachHangId = hoaDon.value?.khachHang?.id;
    if (khachLeInfo.value.id && currentKhachHangId === khachLeInfo.value.id) {
      return notify.warning(
        "Vui lòng chọn khách hàng thành viên để dùng mã giảm giá!"
      );
    }
    
    const khachHangSafe = { ...hoaDon.value.khachHang };

    try {
      const maPhieuCu = hoaDon.value.phieuGiamGia?.ma;
      const res = await apDungKhuyenMaiTuDong(selectedHoaDonId.value);

      console.log("🔥 API Trả về:", res.data);

      if (typeof res.data === "object" && res.data.id) {
        const updatedHoaDon = res.data; 
        hoaDon.value = {
          ...hoaDon.value,   
          ...updatedHoaDon,  
          
          khachHang: (updatedHoaDon.khachHang && updatedHoaDon.khachHang.id) 
                     ? updatedHoaDon.khachHang 
                     : khachHangSafe,
                     
          sanPhamList: hoaDon.value.sanPhamList, 
        };

        const index = hoaDonChoList.value.findIndex(
          (h) => h.id === updatedHoaDon.id
        );
        
        if (index !== -1) {
          const itemCu = hoaDonChoList.value[index];

          hoaDonChoList.value[index] = {
            ...itemCu, 
            tongTien: updatedHoaDon.tongTien,        
            tongTienSauGiam: updatedHoaDon.tongTienSauGiam,
            soTienGiamGia: updatedHoaDon.soTienGiamGia,
            phiVanChuyen: updatedHoaDon.phiVanChuyen,
            phieuGiamGia: updatedHoaDon.phieuGiamGia,

            khachHang: updatedHoaDon.khachHang || itemCu.khachHang,
            sanPhamList: itemCu.sanPhamList,
          };
        }

        const pggMoi = updatedHoaDon.phieuGiamGia;
        if (pggMoi) {
          if (pggMoi.ma !== maPhieuCu) {
            notify.success(`Đã áp dụng mã ưu đãi: ${pggMoi.ten}`);
          } else {
            notify.info(`Mã hiện tại (${pggMoi.ten}) đang là tốt nhất!`);
          }
        } else {
          notify.warning("Hiện không có mã giảm giá nào phù hợp.");
        }
        
      } else if (typeof res.data === "string") {
        notify.info(res.data);
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
