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
      // Gọi song song 2 API: Lấy chi tiết sp & Lấy thông tin hóa đơn (để lấy tiền giảm)
      const [resChiTiet, resHoaDon] = await Promise.all([
        getChiTietHoaDon(hoaDon.value.id),
        getHoaDonById(hoaDon.value.id),
      ]);

      const listMoi = resChiTiet.data;
      const hoaDonMoi = resHoaDon.data; // Dữ liệu hóa đơn chuẩn từ BE

      // 1. Cập nhật lại tiền cho từng sản phẩm (Giữ nguyên logic của bạn để không mất hiển thị)
      gioHang.value = gioHang.value.map((itemFE) => {
        const itemBE = listMoi.find((i) => i.id === itemFE.id);
        if (itemBE) {
          return {
            ...itemFE,
            donGia: itemBE.donGia,
            thanhTien: itemBE.thanhTien,
            soLuong: itemBE.soLuong,
          };
        }
        return itemFE;
      });

      // 2. CẬP NHẬT TIỀN TỔNG & GIẢM GIÁ (Đây là chỗ sửa lỗi của bạn)
      // Thay vì tự tính bằng reduce, ta lấy luôn số BE đã tính
      if (hoaDonMoi) {
        hoaDon.value.tongTien = hoaDonMoi.tongTien;
        hoaDon.value.tongTienSauGiam = hoaDonMoi.tongTienSauGiam; // 🔥 Số này đã được BE trừ khuyến mãi chuẩn
        hoaDon.value.phieuGiamGia = hoaDonMoi.phieuGiamGia; // Cập nhật lại phiếu (để nếu không đủ điều kiện BE tự gỡ thì FE cũng gỡ)
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

      // Backend trả về chi tiết SP vừa thêm (hoặc Hóa đơn tùy bạn sửa controller)
      // Giả sử Controller của bạn đang trả về HoaDonChiTiet
      const newItemBE = res.data;

      // B2: Tạo item hiển thị (Kết hợp dữ liệu BE và thông tin từ 'sp')
      const displayItem = {
        ...newItemBE, // ID, Giá, Thành tiền từ BE

        // 🔥 QUAN TRỌNG: Ghi đè thông tin hiển thị từ sản phẩm vừa click
        tenSanPham: sp.tenSanPham,
        mauSac: sp.mauSac?.ten || sp.mauSac,
        kichThuoc: sp.kichThuoc, // Lấy ngay cái size bạn vừa chọn
        tenXuatXu: sp.tenXuatXu,
        hinhAnhUrl: sp.hinhAnhUrl,
        idChiTietSP: sp.id, // Lưu lại ID gốc để sau này update
      };

      // B3: Cập nhật vào giỏ hàng ngay lập tức
      const idx = gioHang.value.findIndex((i) => i.id === displayItem.id); // So sánh ID chi tiết hóa đơn

      // Nếu Backend trả về ID mới (thêm mới)
      if (idx === -1) {
        gioHang.value.push(displayItem);
      } else {
        // Nếu Backend trả về ID cũ (cộng dồn)
        gioHang.value[idx] = { ...gioHang.value[idx], ...displayItem };
      }

      // Cập nhật lại list vào hóa đơn để đồng bộ
      hoaDon.value.sanPhamList = gioHang.value;

      notify.success("Đã thêm sản phẩm!");

      // B4: Gọi hàm đồng bộ tiền (chạy ngầm để update tổng tiền và khuyến mãi)
      // (Nếu bạn đã sửa Controller trả về HoaDon thì bước này có thể tối ưu hơn)
      await syncMoneyFromBackend();
    } catch (err) {
      let msg = "Không thể thêm sản phẩm!";
      const responseData = err.response?.data;

      if (typeof responseData === "string") {
        msg = responseData; // Lỗi là chuỗi thuần
      } else if (responseData?.message) {
        msg = responseData.message; // Lỗi có trường message
      } else if (responseData?.error) {
        msg = responseData.error; // Lỗi có trường error
      }
      // Trường hợp lỗi 500/RuntimeException, thông báo có thể nằm trong body
      else if (responseData && typeof responseData === "object") {
        // Thử tìm trong các trường chứa lỗi khác
        const errorContent =
          responseData.exception ||
          responseData.trace ||
          JSON.stringify(responseData);
        if (errorContent.includes("Số lượng tồn không đủ")) {
          msg = "Số lượng tồn không đủ!";
        }
      }

      if (msg.includes("Số lượng tồn không đủ")) {
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
    if (!hoaDon.value) return notify.warning("Chưa chọn hóa đơn!"); // 1. Validate và tìm item

    let newSoLuong = parseInt(newSoLuongRaw);
    const spGioHangIndex = gioHang.value.findIndex(
      (i) => i.id === idChiTietHoaDon
    );
    if (spGioHangIndex === -1) return;

    const currentSp = gioHang.value[spGioHangIndex]; // 🔥 Lấy tham chiếu trực tiếp
    const oldSoLuong = currentSp.soLuong; // Nếu nhập bậy bạ (chữ, số âm, số 0) -> Reset về 1

    if (isNaN(newSoLuong) || newSoLuong < 1) {
      notify.warning("Số lượng phải lớn hơn 0!"); // Đảm bảo UI reset về giá trị cũ (nếu có lỗi nhập)
      currentSp.soLuong = oldSoLuong; // Reset về số cũ
      return;
    }

    const delta = newSoLuong - oldSoLuong; // Nếu không thay đổi gì thì thôi

    if (delta === 0) return; // Optimistic Update: Cập nhật hiển thị ngay lập tức

    currentSp.soLuong = newSoLuong;
    currentSp.thanhTien = currentSp.donGia * newSoLuong; // Cần thiết lập lại list trên hoaDon để kích hoạt watcher/computed phụ thuộc
    hoaDon.value.sanPhamList = gioHang.value;

    const idKho = currentSp.idChiTietSP || currentSp.chiTietSanPham?.id;

    try {
      // Gọi API
      await themSanPhamVaoHoaDon(hoaDon.value.id, idKho, delta); // Đồng bộ tiền

      await syncMoneyFromBackend(); // Hàm này sẽ cập nhật lại soLuong và thanhTien từ BE

      notify.success("Cập nhật thành công!");
    } catch (err) {
      console.error(err); // 5. Xử lý lỗi (Rollback và Bắt lỗi) // 🔥 LOGIC BẮT LỖI
      let msg = "Không thể cập nhật số lượng!";
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
      } // 🔥 Khôi phục số lượng trên FE về giá trị cũ (Rollback trực tiếp) // currentSp vẫn là item trong gioHang.value

      currentSp.soLuong = oldSoLuong;
      currentSp.thanhTien = currentSp.donGia * oldSoLuong;
      // Force update (để UI chắc chắn cập nhật)
      gioHang.value[spGioHangIndex] = { ...currentSp };
      hoaDon.value.sanPhamList = gioHang.value; // Cập nhật tổng số lượng hóa đơn chờ
      updateHoaDonChoCount();

      if (msg.includes("Số lượng tồn không đủ")) {
        notify.warning(msg);
      } else {
        notify.error(msg);
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

      await syncMoneyFromBackend(); // Tính lại tiền
      notify.success("Đã xóa sản phẩm!");
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
  };
}
