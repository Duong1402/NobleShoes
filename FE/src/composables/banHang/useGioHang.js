import { ref, computed, watch } from "vue";
import {
  themSanPhamVaoHoaDon,
  xoaSanPhamKhoiHoaDon,
  getChiTietHoaDon,
  getHoaDonById,
} from "@/service/BanHangService";

export function useGioHang(notify, hoaDon, hoaDonChoList) {
  const gioHang = ref([]);

  // 1. WATCH: Chỉ nạp lại giỏ hàng khi ID hóa đơn thay đổi
  // (Tránh việc đang thao tác mà giỏ hàng bị reset mất dữ liệu)
  watch(
    () => hoaDon.value?.id,
    async (newId, oldId) => {
      if (newId && newId !== oldId) {
        // Khi chuyển sang hóa đơn khác, lấy dữ liệu từ hóa đơn đó
        gioHang.value =
          hoaDon.value.sanPhamList || hoaDon.value.hoaDonChiTiets || [];
      } else if (!newId) {
        gioHang.value = [];
      }
    },
    { immediate: true }
  );

  // 2. TÍNH TOÁN
  const tongTienHang = computed(() => {
    return gioHang.value.reduce((sum, item) => sum + (item.thanhTien || 0), 0);
  });

  const tongTienSauGiam = computed(() => {
    // Ưu tiên lấy từ Hóa Đơn BE trả về. Nếu chưa có thì lấy tổng tiền hàng.
    return hoaDon.value?.tongTienSauGiam ?? tongTienHang.value;
  });

  const updateHoaDonChoCount = () => {
    const totalQty = gioHang.value.reduce(
      (sum, item) => sum + (item.soLuong || 0),
      0
    );
    const idx = hoaDonChoList.value.findIndex((h) => h.id === hoaDon.value?.id);
    if (idx !== -1) {
      hoaDonChoList.value[idx].soLuong = totalQty;
      // Cập nhật tiền ra ngoài list chờ
      if (hoaDon.value.tongTienSauGiam !== undefined) {
        hoaDonChoList.value[idx].tongTienSauGiam = hoaDon.value.tongTienSauGiam;
      }
    }
  };

  // Helper: Đồng bộ lại tiền nong từ Backend mà KHÔNG làm mất thông tin hiển thị
  const syncMoneyFromBackend = async () => {
    try {
      const [resChiTiet, resHoaDon] = await Promise.all([
        getChiTietHoaDon(hoaDon.value.id),
        getHoaDonById(hoaDon.value.id),
      ]);

      const rawList = resChiTiet.data || [];
      const hoaDonMoi = resHoaDon.data;

      // 1. LỌC SƠ BỘ (Chỉ lọc trạng thái xóa)
      // Giữ lại tất cả dòng khác để hiển thị
      const listMoi = rawList.filter((item) => item.trangThai !== 0);

      // 2. MAP DỮ LIỆU
      if (listMoi) {
        const mappedList = listMoi.map((itemBE) => {
          // Tìm dữ liệu cũ đang hiển thị trên UI để lấy thông tin ảnh/tên/tồn kho bù đắp
          const oldItem = gioHang.value.find((g) => g.id === itemBE.id);

          // Lấy thông tin từ BE
          const ctsp = itemBE.chiTietSanPham;

          // 🔥 LOGIC TÍNH TỒN KHO "BẤT TỬ" (Quan trọng nhất)
          // Ưu tiên 1: Lấy từ BE trả về (ctsp.soLuongTon)
          // Ưu tiên 2: Lấy từ dữ liệu cũ trên FE (oldItem.soLuongTon)
          // Mặc định: 0
          let realStock = 0;

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
            ...itemBE, // Giữ ID, đơn giá, số lượng gốc

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

            // Quan trọng: Phải lấy được ID sản phẩm gốc
            idChiTietSP:
              ctsp?.id || itemBE.idChiTietSanPham || oldItem?.idChiTietSP,

            // 🔥 GÁN SỐ LƯỢNG TỒN ĐÃ TÍNH TOÁN VÀO ĐÂY
            soLuongTon: realStock,
          };
        });

        // 3. 🔥 LỌC LẦN CUỐI (CHỐT CHẶN AN TOÀN)
        // Loại bỏ những dòng lỗi không tìm thấy ID sản phẩm gốc (tránh hiện N/A)
        gioHang.value = mappedList.filter((item) => item.idChiTietSP);

        hoaDon.value.sanPhamList = gioHang.value;
      }

      // 4. Cập nhật tiền
      if (hoaDonMoi) {
        hoaDon.value.tongTien = hoaDonMoi.tongTien;
        hoaDon.value.tongTienSauGiam = hoaDonMoi.tongTienSauGiam;
        hoaDon.value.phieuGiamGia = hoaDonMoi.phieuGiamGia;
        hoaDon.value.soTienGiamGia = hoaDonMoi.soTienGiamGia;
      }

      updateHoaDonChoCount();
    } catch (e) {
      console.error("Lỗi đồng bộ tiền:", e);
    }
  };

  // =================================================================
  // 3. HÀM THÊM SẢN PHẨM (GIỮ NGUYÊN HIỂN THỊ - CẬP NHẬT TIỀN)
  // =================================================================
  const handleThemSanPham = async (sp) => {
    if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");
    try {
      // B1: Gọi API thêm
      const res = await themSanPhamVaoHoaDon(hoaDon.value.id, sp.id, 1);

      // 1: Lấy data từ cấu trúc mới { hoaDonChiTiet, message }
      const { hoaDonChiTiet, message } = res.data;

      if (message) {
        notify.info(message); // Màu xanh dương nếu có thay đổi giá
      } else {
        notify.success("Đã thêm sản phẩm!"); // Màu xanh lá nếu bình thường
      }

      // Gán đối tượng chi tiết để dùng cho logic phía dưới
      const newItemBE = hoaDonChiTiet;
      // B2: Tạo item hiển thị (Kết hợp dữ liệu BE và thông tin từ 'sp')
      const displayItem = {
        ...newItemBE, // ID, Giá, Thành tiền từ BE

        // 🔥 QUAN TRỌNG: Ghi đè thông tin hiển thị từ sản phẩm vừa click
        tenSanPham: sp.tenSanPham,
        mauSac: sp.mauSac?.ten || sp.mauSac,
        kichThuoc: sp.kichThuoc, // Lấy ngay cái size bạn vừa chọn
        tenXuatXu: sp.tenXuatXu,
        hinhAnhUrl: sp.hinhAnhUrl,
        soLuongTon: sp.soLuongTon,
        idChiTietSP: sp.id, // Lưu lại ID gốc để sau này update
      };

      // B3: Cập nhật vào giỏ hàng ngay lập tức
      const idx = gioHang.value.findIndex((i) => i.id === displayItem.id); // So sánh ID chi tiết hóa đơn

      // Nếu Backend trả về ID mới (thêm mới)
      if (idx === -1) {
        // Nếu là dòng mới (hoặc dòng mới được tách ra do đổi giá) -> Thêm vào cuối
        gioHang.value.push(displayItem);
      } else {
        // Nếu là cộng dồn -> Cập nhật dòng cũ
        gioHang.value[idx] = { ...gioHang.value[idx], ...displayItem };
      }

      // Cập nhật lại list vào hóa đơn để đồng bộ
      hoaDon.value.sanPhamList = gioHang.value;

      await syncMoneyFromBackend();

      //Thông báo thay đổi giá
      if (message) {
        // Tìm lại đúng dòng sản phẩm vừa thêm/update trong giỏ hàng mới
        const targetItem = gioHang.value.find(
          (item) => item.id === hoaDonChiTiet.id
        );

        if (targetItem) {
          // Gán message vào một thuộc tính riêng để hiển thị ở UI
          targetItem.warningMessage = message;

          // Tùy chọn: Tự động tắt thông báo sau 5 giây nếu muốn
          // setTimeout(() => { targetItem.warningMessage = null; }, 5000);
        }
      }
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
    let newSoLuong = parseInt(valueRaw); // 1. Chuẩn hóa giá trị nhập
    if (isNaN(newSoLuong) || newSoLuong < 1) {
      newSoLuong = 1;
    }

    const item = gioHang.value.find((i) => i.id === idChiTietHoaDon);
    if (!item) return; // 2. Cập nhật state Vue ngay lập tức (Optimistic Update)

    item.soLuong = newSoLuong;
    item.thanhTien = item.donGia * newSoLuong; // 🔥 Đồng bộ Thành tiền // 3. Đồng bộ lại tổng số lượng/tiền trên list hóa đơn chờ
    updateHoaDonChoCount(); // 🔥 Hàm này đã có sẵn trong file của bạn

    // 4. Đồng bộ tổng tiền hóa đơn (chỉ cần tính lại từ giỏ hàng)
    hoaDon.value.tongTien = tongTienHang.value;

    // Trả về giá trị đã chuẩn hóa để dùng cho API call (nếu cần)
    return newSoLuong;
  };

  const handleCapNhatSoLuong = async (idChiTietHoaDon, newSoLuongRaw) => {
    if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!");

    const idx = gioHang.value.findIndex((i) => i.id === idChiTietHoaDon);
    if (idx === -1) return;

    const currentSp = gioHang.value[idx];
    const oldSoLuong = currentSp.soLuong;

    // Lấy tồn kho (quan trọng: phải lấy từ syncMoney đã map, mặc định 0 nếu lỗi)
    const soLuongTonKhaDung =
      currentSp.soLuongTon !== undefined ? currentSp.soLuongTon : 0;
    // const soLuongTonKhaDung = 100;

    let newSoLuong = parseInt(newSoLuongRaw);

    // =================================================================
    // 1. HÀM HELPER NỘI BỘ: Cập nhật UI & Ép Vue vẽ lại (Reactivity)
    // =================================================================
    const updateUI = (val) => {
      const item = gioHang.value[idx];
      item.soLuong = val;
      item.thanhTien = item.donGia * val;
      // Gán đè object mới để Vue phát hiện thay đổi trên ô input
      gioHang.value[idx] = { ...item };
      hoaDon.value.sanPhamList = gioHang.value;
    };

    // =================================================================
    // 2. VALIDATE DỮ LIỆU
    // =================================================================

    // Check 1: Số lượng không hợp lệ hoặc <= 0
    if (isNaN(newSoLuong) || newSoLuong < 1) {
      notify.warning("Số lượng tối thiểu là 1!");
      // Reset về 1 ngay lập tức
      updateUI(1);

      // Nếu số cũ khác 1 thì mới gọi API sửa về 1, còn không thì dừng
      if (oldSoLuong === 1) return;
      newSoLuong = 1; // Gán lại để chạy logic delta bên dưới
    }

    // Tính lượng thay đổi
    const delta = newSoLuong - oldSoLuong;

    if (delta === 0) return; // Không đổi thì thôi

    // Check 2: Kiểm tra tồn kho (Chỉ check khi TĂNG số lượng)
    if (delta > 0) {
      // console.log(`[Check Kho] Muốn thêm: ${delta}, Kho còn: ${soLuongTonKhaDung}`);

      if (delta > soLuongTonKhaDung) {
        notify.warning(`Kho chỉ còn thêm được ${soLuongTonKhaDung} sản phẩm!`);
        // Reset về số cũ (Rollback UI)
        updateUI(oldSoLuong);
        return; // Dừng ngay, không gọi API
      }
    }

    // =================================================================
    // 3. CẬP NHẬT & GỌI API (INLINE)
    // =================================================================

    // Cập nhật UI tạm thời (Optimistic Update)
    updateUI(newSoLuong);

    const idKho = currentSp.idChiTietSP;

    try {
      // Gọi API thêm/bớt
      await themSanPhamVaoHoaDon(hoaDon.value.id, idKho, delta);

      // Đồng bộ lại tiền & tồn kho mới nhất
      await syncMoneyFromBackend();

      notify.success("Cập nhật thành công!");
    } catch (err) {
      console.error(err);

      // 🔥 ROLLBACK NẾU LỖI: Quay về số lượng cũ & Sync lại
      updateUI(oldSoLuong);
      await syncMoneyFromBackend();

      const msg = err.response?.data?.message || "Lỗi cập nhật";
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

  // =================================================================
  // 5. XÓA SẢN PHẨM
  // =================================================================
  const handleXoaSanPham = async (idChiTietHoaDon) => {
    if (!hoaDon.value) return;
    try {
      await xoaSanPhamKhoiHoaDon(hoaDon.value.id, idChiTietHoaDon);

      // Xóa trên UI
      gioHang.value = gioHang.value.filter((i) => i.id !== idChiTietHoaDon);
      hoaDon.value.sanPhamList = gioHang.value;

      notify.success("Đã xóa sản phẩm!");

      await syncMoneyFromBackend(); // Tính lại tiền
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
