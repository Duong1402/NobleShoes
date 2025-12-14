import { ref, onMounted } from "vue";
import {
  timKhachHangDaDangKy,
  capNhatKhachHang,
  themKhachHangMoi,
} from "@/service/BanHangService";
import Swal from "sweetalert2";

export const khachLeInfo = ref({
  id: null,
  hoTen: "Khách lẻ",
  sdt: "0000000000",
});

export function useKhachHang(
  notify,
  hoaDon,
  thongTinNguoiNhan,
  autoFillAddressFromNames,
  resetFormGiaoHang,
  calculateShippingFee,
  isBanGiaoHang,
  phiShip
) {
  const searchKeyword = ref("");
  const searchResults = ref([]);
  const isGuestEditable = ref(false);
  const showAddGuestButton = ref(false);

  const initKhachLe = async () => {
    if (khachLeInfo.value.id) {
      return;
    }

    try {
      const sdtMacDinh = "0000000000";
      const res = await timKhachHangDaDangKy(sdtMacDinh);

      if (res.data && res.data.length > 0) {
        khachLeInfo.value = res.data[0];
        console.log("✅ Đã tìm thấy Khách lẻ ID:", khachLeInfo.value.id);
      } else {
        console.log("⚠️ Không tìm thấy Khách lẻ -> Đang tạo mới...");
        const newRes = await themKhachHangMoi({
          hoTen: "Khách lẻ",
          sdt: sdtMacDinh,
        });
        khachLeInfo.value = newRes.data;
        console.log("✅ Đã tạo mới Khách lẻ ID:", khachLeInfo.value.id);
      }
    } catch (err) {
      console.error("Lỗi khởi tạo khách lẻ:", err);
    }
  };

  onMounted(() => {
    initKhachLe();
  });

const assignKhachHang = async (khachHang, keepInputEmpty = false) => {
    try {
      if (
        (!khachLeInfo.value || !khachLeInfo.value.id) &&
        khachHang === "KHACH_LE"
      ) {
        await initKhachLe();
        if (!khachLeInfo.value) return notify.error("Lỗi dữ liệu khách lẻ");
        khachHang = khachLeInfo.value;
      }

      console.log("Chọn khách hàng:", khachHang);
      const selectedId = khachHang.id; 
    
      hoaDon.value.khachHang = khachHang; 
      
      isGuestEditable.value = true; 
      
      searchResults.value = []; 
      
      searchKeyword.value = khachHang.hoTen; 
      const res = await capNhatKhachHang(hoaDon.value.id, selectedId);
      if (res.data) {
        const dataMoi = res.data;

        hoaDon.value.tongTien = dataMoi.tongTien;
        hoaDon.value.tongTienSauGiam = dataMoi.tongTienSauGiam;
        hoaDon.value.soTienGiamGia = dataMoi.soTienGiamGia;
        hoaDon.value.phieuGiamGia = dataMoi.phieuGiamGia;
        if (dataMoi.phiVanChuyen !== undefined && dataMoi.phiVanChuyen !== null) {
          hoaDon.value.phiVanChuyen = dataMoi.phiVanChuyen;
        }

        if (dataMoi.khachHang && dataMoi.khachHang.id) {
          hoaDon.value.khachHang = dataMoi.khachHang;
        } else {
          hoaDon.value.khachHang = {
            ...khachHang,
            id: selectedId, 
            hoTen: dataMoi.tenKhachHang || khachHang.hoTen, 
            sdt: dataMoi.sdt || khachHang.sdt,
            diaChi: dataMoi.diaChiGiaoHang || khachHang.diaChi,
          };
          
          console.log("Fix Khách hàng sau API:", hoaDon.value.khachHang);
        }
      }

      const isKhachLe = khachLeInfo.value && selectedId === khachLeInfo.value.id;

      if (keepInputEmpty || isKhachLe) {
        searchKeyword.value = "";
      } else {
        searchKeyword.value = hoaDon.value.khachHang.hoTen; 
      }

      if (isKhachLe) {
        if (resetFormGiaoHang) resetFormGiaoHang(false);
        if (phiShip) phiShip.value = 0;
        notify.info("Đã chuyển về Khách lẻ.");
        return;
      }

      thongTinNguoiNhan.value.hoTen = hoaDon.value.khachHang.hoTen || "";
      thongTinNguoiNhan.value.sdt = hoaDon.value.khachHang.sdt || "";

      const listDiaChi = khachHang.danhSachDiaChi || khachHang.listDiaChi || [];
      if (listDiaChi.length > 0) {
         const dc = listDiaChi.find((d) => d.macDinh) || listDiaChi[0];
         const dbTinh = dc.thanhPho || dc.tinhThanh || "";
         const dbHuyen = dc.huyen || dc.quanHuyen || "";
         const dbXa = dc.xa || dc.phuongXa || "";
         const dbCuThe = dc.diaChiCuThe || "";

         thongTinNguoiNhan.value.tinhThanh = dbTinh;
         thongTinNguoiNhan.value.quanHuyen = dbHuyen;
         thongTinNguoiNhan.value.phuongXa = dbXa;
         thongTinNguoiNhan.value.diaChiCuThe = dbCuThe;

         if (autoFillAddressFromNames) {
           await autoFillAddressFromNames(dbTinh, dbHuyen, dbXa);
           if (isBanGiaoHang && isBanGiaoHang.value === true) {
             if (calculateShippingFee) await calculateShippingFee();
           }
         }
      } else {
         thongTinNguoiNhan.value.tinhThanh = "";
         thongTinNguoiNhan.value.quanHuyen = "";
         thongTinNguoiNhan.value.phuongXa = "";
         thongTinNguoiNhan.value.diaChiCuThe = "";
         if (phiShip) phiShip.value = 0;
      }

      notify.success("Đã cập nhật khách hàng!");
    } catch (error) {
      console.error("Lỗi assignKhachHang:", error);
      notify.error("Lỗi khi chọn khách hàng!");
    }
  };

  const handleTimKhachHang = async () => {
    const keyword = searchKeyword.value ? searchKeyword.value.trim() : "";

    if (!keyword) {
      if (
        khachLeInfo.value &&
        hoaDon.value?.khachHang?.id !== khachLeInfo.value.id
      ) {
        await assignKhachHang(khachLeInfo.value, true);
        notify.info("Đã chuyển về Khách lẻ.");
      }

      searchResults.value = [];
      showAddGuestButton.value = false;
      return;
    }

    if (keyword.length < 2) {
      searchResults.value = [];
      return;
    }

    try {
      const res = await timKhachHangDaDangKy(keyword);
      if (res.data && res.data.length > 0) {
        searchResults.value = res.data;
        showAddGuestButton.value = false;
      } else {
        searchResults.value = [];
        showAddGuestButton.value = true;
      }
    } catch (err) {
      console.error("Lỗi tìm kiếm KH:", err);
    }
  };

  const handleSelectKhachHang = (khachHang) => {
    searchResults.value = [];
    assignKhachHang(khachHang);
  };
  const handleThemNhanhKhachHang = async () => {
    await Swal.fire({
      title: `<span style="font-weight: bold; font-size: 1.3rem;">Thêm khách hàng mới</span>`,
      html: `
        <div style="display: flex; gap: 16px; width: 100%;">
          <div style="flex: 1; display: flex; flex-direction: column; text-align: left;">
            <label style="font-weight: bold; margin-bottom: 6px;">Tên khách hàng *</label>
            <input id="swalHoTen" class="swal2-input" placeholder="Tên khách hàng" style="margin:0;">
            <small id="errHoTen" style="color:red; margin-top:4px; min-height:16px"></small>
          </div>
          <div style="flex: 1; display: flex; flex-direction: column; text-align: left;">
            <label style="font-weight: bold; margin-bottom: 6px;">Số điện thoại *</label>
            <input id="swalSdt" class="swal2-input" placeholder="Sđt khách hàng" style="margin:0;">
            <small id="errSdt" style="color:red; margin-top:4px; min-height:16px"></small>
          </div>
        </div>
      `,
      showCancelButton: true,
      confirmButtonText: "Thêm mới",
      confirmButtonColor: "#ffc107",
      width: "620px",
      preConfirm: async () => {
        const hoTen = document.getElementById("swalHoTen").value.trim();
        const sdt = document.getElementById("swalSdt").value.trim();
        const errHoTen = document.getElementById("errHoTen");
        const errSdt = document.getElementById("errSdt");

        errHoTen.innerText = "";
        errSdt.innerText = "";
        let hasError = false;

        if (!hoTen) {
          errHoTen.innerText = "Vui lòng nhập tên.";
          hasError = true;
        }
        if (!sdt) {
          errSdt.innerText = "Vui lòng nhập SĐT.";
          hasError = true;
        } else if (sdt.length !== 10 || !/^\d+$/.test(sdt)) {
          errSdt.innerText = "SĐT phải 10 số.";
          hasError = true;
        }

        if (hasError) return false;

        try {
          const res = await themKhachHangMoi({ hoTen, sdt });
          const newKH = res.data;
          await assignKhachHang(newKH);
          notify.success("Thêm khách hàng thành công!");
          return true;
        } catch (error) {
          const errors = error.response?.data?.errors;
          if (errors) {
            if (errors.hoTen) errHoTen.innerText = errors.hoTen;
            if (errors.sdt) errSdt.innerText = errors.sdt;
          } else {
            errHoTen.innerText = "Lỗi hệ thống!";
          }
          return false;
        }
      },
    });
  };

  const resetKhachHangState = () => {
    searchKeyword.value = "";
    searchResults.value = [];
  };

  return {
    searchKeyword,
    searchResults,
    isGuestEditable,
    showAddGuestButton,
    khachLeInfo,
    assignKhachHang,
    handleTimKhachHang,
    handleSelectKhachHang,
    handleThemNhanhKhachHang,
    resetKhachHangState,
  };
}
