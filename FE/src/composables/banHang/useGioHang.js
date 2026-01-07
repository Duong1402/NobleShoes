import { ref, computed, watch } from "vue";
import {
  themSanPhamVaoHoaDon,
  xoaSanPhamKhoiHoaDon,
  getChiTietHoaDon,
  getHoaDonById,
} from "@/service/BanHangService";

export function useGioHang(notify, hoaDon, hoaDonChoList) {
  const gioHang = ref([]);

  watch(
    () => hoaDon.value?.id,
    async (newId, oldId) => {
      if (newId && newId !== oldId) {
        gioHang.value =
          hoaDon.value.sanPhamList || hoaDon.value.hoaDonChiTiets || [];
      } else if (!newId) {
        gioHang.value = [];
      }
    },
    { immediate: true }
  );

  const tongTienHang = computed(() => {
    return gioHang.value.reduce((sum, item) => sum + (item.thanhTien || 0), 0);
  });

  const tongTienSauGiam = computed(() => {
    return hoaDon.value?.tongTienSauGiam || 0;
  });

  const updateHoaDonChoCount = () => {
    const totalQty = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const idx = hoaDonChoList.value.findIndex((h) => h.id === hoaDon.value?.id);
    if (idx !== -1) {
      hoaDonChoList.value[idx].soLuong = totalQty;
      if (hoaDon.value.tongTienSauGiam !== undefined) {
        hoaDonChoList.value[idx].tongTienSauGiam = hoaDon.value.tongTienSauGiam;
      }
    }
  };

  const syncMoneyFromBackend = async () => {
    try {
      const [resChiTiet, resHoaDon] = await Promise.all([
        getChiTietHoaDon(hoaDon.value.id),
        getHoaDonById(hoaDon.value.id),
      ]);

      const rawList = resChiTiet.data || [];
      const hoaDonMoi = resHoaDon.data;

      const listMoi = rawList.filter((item) => item.trangThai !== 0);

      if (listMoi) {
        const mappedList = listMoi.map((itemBE) => {
          const oldItem = gioHang.value.find((g) => g.id === itemBE.id);

          const ctsp = itemBE.chiTietSanPham;
          let realStock = 0;
          const currentWarning = oldItem?.warningMessage || null;

          if (
            ctsp &&
            ctsp.soLuongTon !== undefined &&
            ctsp.soLuongTon !== null
          ) {
            realStock = ctsp.soLuongTon;
          } else if (oldItem && oldItem.soLuongTon !== undefined) {
            realStock = oldItem.soLuongTon;
          }

          return {
            ...itemBE,

            tenSanPham:
              itemBE.tenSanPham ||
              ctsp?.sanPham?.ten ||
              oldItem?.tenSanPham ||
              "Đang cập nhật...",

            mauSac: itemBE.mauSac || ctsp?.mauSac?.ten || oldItem?.mauSac,

            kichThuoc:
              itemBE.kichThuoc ||
              ctsp?.kichThuoc?.ten ||
              ctsp?.kichThuoc?.giaTri ||
              oldItem?.kichThuoc,

            hinhAnhUrl:
              itemBE.hinhAnhUrl || ctsp?.hinhAnh?.url || oldItem?.hinhAnhUrl,

            tenXuatXu:
              itemBE.tenXuatXu ||
              ctsp?.sanPham?.xuatXu?.ten ||
              oldItem?.tenXuatXu,

            idChiTietSP:
              ctsp?.id || itemBE.idChiTietSanPham || oldItem?.idChiTietSP,

            warningMessage: currentWarning,
            soLuongTon: realStock,
          };
        });

        gioHang.value = mappedList.filter((item) => item.idChiTietSP);

        hoaDon.value.sanPhamList = gioHang.value;
      }

      // 4. Cập nhật tiền
      if (hoaDonMoi) {
        hoaDon.value.tongTien = hoaDonMoi.tongTien;
        hoaDon.value.tongTienSauGiam = hoaDonMoi.tongTienSauGiam;
        hoaDon.value.phieuGiamGia = hoaDonMoi.phieuGiamGia;
        hoaDon.value.soTienGiamGia = hoaDonMoi.soTienGiamGia;

        hoaDon.value.phiVanChuyen = hoaDonMoi.phiVanChuyen || 0;
      }

      updateHoaDonChoCount();
    } catch (e) {
      console.error("Lỗi đồng bộ tiền:", e);
    }
  };
  const handleThemSanPham = async (sp) => {
    if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");
    try {
      const res = await themSanPhamVaoHoaDon(hoaDon.value.id, sp.id, 1);

      const { allRelatedHdct, message } = res.data;

      if (message) {
        notify.info(message);
      } else {
        notify.success("Đã thêm sản phẩm!");
      }

      const idChiTietSP = sp.id;
      const otherItems = gioHang.value.filter(
        (item) => item.idChiTietSP !== idChiTietSP
      );
      const mappedRelatedHdct = allRelatedHdct.map((itemBE) => {
        return {
          ...itemBE,
          tenSanPham: sp.tenSanPham,
          mauSac: sp.mauSac?.ten || sp.mauSac,
          kichThuoc: sp.kichThuoc,
          tenXuatXu: sp.tenXuatXu,
          hinhAnhUrl: sp.hinhAnhUrl,
          soLuongTon: sp.soLuongTon,
          idChiTietSP: sp.id,
          currentGiaBan: sp.giaBan,
          warningMessage: message ? message : null,
        };
      });
      gioHang.value = [...otherItems, ...mappedRelatedHdct];

      hoaDon.value.sanPhamList = gioHang.value;

      await syncMoneyFromBackend();

      // if (message) {
      //   const targetItem = gioHang.value.find(
      //     (item) => item.id === hoaDonChiTiet.id
      //   );

      //   if (targetItem) {
      //     targetItem.warningMessage = message;
      //   }
      // }
    } catch (err) {
      let msg = "Không thể thêm sản phẩm!";
      const responseData = err.response?.data;

      if (typeof responseData === "string") {
        msg = responseData;
      } else if (responseData?.message) {
        msg = responseData.message;
      } else if (responseData?.error) {
        msg = responseData.error;
      } else if (responseData && typeof responseData === "object") {
        const errorContent =
          responseData.exception ||
          responseData.trace ||
          JSON.stringify(responseData);
        if (errorContent.includes("Số lượng tồn không đủ")) {
          msg = "Số lượng tồn không đủ!";
        }
      }

      if (
        msg.toLowerCase().includes("tồn") ||
        msg.toLowerCase().includes("số lượng")
      ) {
        notify.warning(msg);
      } else {
        notify.error(msg);
      }
    }
  };

  const handleUpdateTempSoLuong = (idChiTietHoaDon, valueRaw) => {
    let newSoLuong = parseInt(valueRaw);
    if (isNaN(newSoLuong) || newSoLuong < 1) {
      newSoLuong = 1;
    }

    const item = gioHang.value.find((i) => i.id === idChiTietHoaDon);
    if (!item) return;

    item.soLuong = newSoLuong;
    item.thanhTien = item.donGia * newSoLuong;
    updateHoaDonChoCount();

    return newSoLuong;
  };

  const handleCapNhatSoLuong = async (idChiTietHoaDon, newSoLuongRaw) => {
    if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");

    const idx = gioHang.value.findIndex((i) => i.id === idChiTietHoaDon);
    if (idx === -1) return;

    const currentSp = gioHang.value[idx];
    const oldSoLuong = currentSp.soLuong;

    const soLuongTonKhaDung =
      currentSp.soLuongTon !== undefined ? currentSp.soLuongTon : 0;

    let newSoLuong = parseInt(newSoLuongRaw);

    const updateUI = (val) => {
      const item = gioHang.value[idx];
      item.soLuong = val;
      item.thanhTien = item.donGia * val;
      gioHang.value[idx] = { ...item };
      hoaDon.value.sanPhamList = gioHang.value;
    };
    if (isNaN(newSoLuong) || newSoLuong < 1) {
      notify.warning("Số lượng tối thiểu là 1!");
      updateUI(1);

      if (oldSoLuong === 1) return;
      newSoLuong = 1;
    }
    const delta = newSoLuong - oldSoLuong;

    if (delta === 0) return;

    if (delta > 0) {
      if (delta > soLuongTonKhaDung) {
        notify.warning(`Kho chỉ còn thêm được ${soLuongTonKhaDung} sản phẩm!`);
        updateUI(oldSoLuong);
      }
    }
    updateUI(newSoLuong);

    const idKho = currentSp.idChiTietSP;

    try {
      const res = await themSanPhamVaoHoaDon(hoaDon.value.id, idKho, delta);
      const { allRelatedHdct, message } = res.data;

      if (allRelatedHdct) {
        const otherItems = gioHang.value.filter(
          (item) => item.idChiTietSP !== idKho
        );
        const mappedRelatedHdct = allRelatedHdct.map((itemBE) => {
          const baseInfo = {
            tenSanPham: currentSp.tenSanPham,
            mauSac: currentSp.mauSac,
            kichThuoc: currentSp.kichThuoc,
            tenXuatXu: currentSp.tenXuatXu,
            hinhAnhUrl: currentSp.hinhAnhUrl,
            soLuongTon: currentSp.soLuongTon,
            idChiTietSP: currentSp.idChiTietSP,
            currentGiaBan: currentSp.currentGiaBan,
          };
          return {
            ...itemBE,
            ...baseInfo,
            warningMessage: message || null,
          };
        });
        gioHang.value = [...otherItems, ...mappedRelatedHdct];
        hoaDon.value.sanPhamList = gioHang.value;
      }

      await syncMoneyFromBackend();
      notify.success("Cập nhật thành công!");
    } catch (err) {
      console.error(err);
      updateUI(oldSoLuong);
      await syncMoneyFromBackend();

      const msg = err.response?.data?.message || "Lỗi cập nhật";
      const targetItem = gioHang.value.find((i) => i.id === idChiTietHoaDon);
      if (targetItem) {
        targetItem.warningMessage = msg;
      }

      if (
        typeof msg === "string" &&
        (msg.includes("tồn") || msg.includes("đủ"))
      ) {
        notify.warning(msg);
      } else {
        notify.error("Lỗi cập nhật số lượng");
      }
    }
  };
  const handleXoaSanPham = async (idChiTietHoaDon) => {
    if (!hoaDon.value) return;
    try {
      await xoaSanPhamKhoiHoaDon(hoaDon.value.id, idChiTietHoaDon);

      gioHang.value = gioHang.value.filter((i) => i.id !== idChiTietHoaDon);
      hoaDon.value.sanPhamList = gioHang.value;

      notify.success("Đã xóa sản phẩm!");

      await syncMoneyFromBackend();
    } catch (e) {
      notify.error("Xóa thất bại");
    }
  };

  return {
    gioHang,
    tongTienHang,
    tongTienSauGiam,
    handleThemSanPham,
    handleCapNhatSoLuong,
    handleUpdateTempSoLuong,
    handleXoaSanPham,
    syncMoneyFromBackend,
  };
}
