// src/composables/banHang/useThanhToan.js
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { thanhToan } from "@/service/BanHangService";
import Swal from "sweetalert2";
import axios from "axios";

const PHUONG_THUC_ID_MAP = {
  TIEN_MAT: "145B12D7-25E0-4B1A-AC21-CD64328FD446",
  CHUYEN_KHOAN: "B6A1BBF4-E9DF-4C88-90F9-C89599679FDC",
  CA_HAI: "AF15E02B-80D8-41CA-9C8C-D3ECB0B290C7",
};

export function useThanhToan(
  notify,
  hoaDon,
  gioHang,
  hoaDonChoList,
  selectedHoaDonId,
  tongTienSauGiam,
  isBanGiaoHang,
  phiShip,
  thongTinNguoiNhan
) {
  const router = useRouter();
  const phuongThucThanhToan = ref("TIEN_MAT");
  const isVnpayProcessing = ref(false);

  // 1. Tính tổng tiền CẦN THANH TOÁN (Logic quan trọng nhất)
  const tongTienCanThanhToan = computed(() => {
    const tienHang = Number(tongTienSauGiam.value) || 0;
    const phi = Number(phiShip.value) || 0;

    // 👇 Debug: F12 lên xem dòng này có nhảy số khi chọn khách không
    console.log("Đang tính tiền:", {
      tienHang,
      phiShip: phi,
      isGiaoHang: isBanGiaoHang.value,
    });

    // Nếu ĐANG BẬT GIAO HÀNG thì cộng ship, không thì thôi
    if (isBanGiaoHang.value) {
      return tienHang + phi;
    }
    return tienHang;
  });

  // 2. Xử lý VNPay
  const handleVNPayPayment = async () => {
    if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
    if (tongTienCanThanhToan.value <= 0)
      return notify.warning("Tổng tiền phải > 0");

    isVnpayProcessing.value = true;
    let orderInfoRaw = `Thanh toan HD ${hoaDon.value.ma || hoaDon.value.id}`;
    let orderInfoClean = orderInfoRaw
      .replace(/[^a-zA-Z0-9_ ]/g, "")
      .replace(/\s/g, "_");

    try {
      const res = await axios.post(
        "http://localhost:8080/admin/vnpay/create-payment",
        {
          amount: tongTienCanThanhToan.value, // Đã bao gồm ship
          orderInfo: orderInfoClean,
          language: "vn",
        }
      );
      if (res.data.code === "00" && res.data.data) {
        window.location.href = res.data.data;
      } else {
        notify.error(res.data.message || "Lỗi tạo URL VNPay");
      }
    } catch (e) {
      console.error(e);
      notify.error("Lỗi kết nối Server VNPay");
    } finally {
      isVnpayProcessing.value = false;
    }
  };

  // 3. Hàm Thanh Toán
  const handleThanhToan = async () => {
    if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
    if (gioHang.value.length === 0) return notify.warning("Giỏ hàng rỗng!");

    const selectedPtttCode = phuongThucThanhToan.value;
    let thongTinGiaoHang = null;
    let loaiHoaDonQuyetDinh = "Tại cửa hàng"; // Mặc định

    // XỬ LÝ GIAO HÀNG
    if (isBanGiaoHang.value) {
      const nguoiNhan = thongTinNguoiNhan.value;
      const shipFee = Number(phiShip.value) || 0;

      if (!nguoiNhan.hoTen || !nguoiNhan.sdt) {
        return notify.warning("Vui lòng điền đủ Tên, SĐT ");
      }

      let diaChiCuTheFinal = nguoiNhan.diaChiCuThe;

      if (!diaChiCuTheFinal) {
        // Nếu khách lười không nhập số nhà, nhưng đã chọn dropdown Xã/Huyện/Tỉnh
        if (nguoiNhan.phuongXa && nguoiNhan.quanHuyen && nguoiNhan.tinhThanh) {
          // Cho phép đi tiếp, nhưng gán tạm địa chỉ cụ thể bằng tên Xã
          diaChiCuTheFinal = nguoiNhan.phuongXa;
        } else {
          // Nếu cả dropdown cũng chưa chọn thì mới báo lỗi
          return notify.warning("Vui lòng nhập địa chỉ nhận hàng!");
        }
      }

      loaiHoaDonQuyetDinh = "Online"; // Hoặc "Giao hàng" tùy DB của bạn

      const diaChiDayDu = [
        diaChiCuTheFinal,
        nguoiNhan.phuongXa,
        nguoiNhan.quanHuyen,
        nguoiNhan.tinhThanh,
      ]
        .filter(Boolean)
        .filter((item, index, self) => self.indexOf(item) === index)
        .join(", ");

      thongTinGiaoHang = {
        tenNguoiNhan: nguoiNhan.hoTen,
        sdt: nguoiNhan.sdt,
        diaChiNguoiNhan: diaChiDayDu,
        phiShip: shipFee,
        phiVanChuyen: shipFee,
      };
    }

    // CHUYỂN KHOẢN
    if (selectedPtttCode === "CHUYEN_KHOAN") {
      // Logic lưu tạm hóa đơn trước khi redirect (nếu cần)
      await handleVNPayPayment();
      return;
    }

    // TIỀN MẶT
    const idPhuongThuc = PHUONG_THUC_ID_MAP[selectedPtttCode];
    if (!idPhuongThuc) return notify.error("Phương thức thanh toán lỗi!");

    // POPUP XÁC NHẬN
    const shipDisplay = isBanGiaoHang.value ? Number(phiShip.value) || 0 : 0;

    const confirm = await Swal.fire({
      title: "Xác nhận Thanh toán?",
      html: `
        <div class="text-start" style="font-size: 1.1em;">
           <div style="display:flex; justify-content:space-between;">
              <span>Tiền hàng:</span>
              <strong>${(
                Number(tongTienSauGiam.value) || 0
              ).toLocaleString()} ₫</strong>
           </div>
           ${
             isBanGiaoHang.value
               ? `
           <div style="display:flex; justify-content:space-between;">
              <span>Phí ship:</span>
              <strong>${shipDisplay.toLocaleString()} ₫</strong>
           </div>`
               : ""
           }
           <hr style="margin: 8px 0;">
           <div style="display:flex; justify-content:space-between; font-size: 1.2em; color: #d33;">
              <span>Tổng thu:</span>
              <strong>${tongTienCanThanhToan.value.toLocaleString()} ₫</strong>
           </div>
           <div style="margin-top:10px; font-style: italic; font-size: 0.9em;">
              (${loaiHoaDonQuyetDinh})
           </div>
        </div>`,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Thanh toán",
      confirmButtonColor: "#28a745",
      cancelButtonText: "Hủy",
    });

    if (!confirm.isConfirmed) return;

    // GỌI API
    try {
      const requestData = {
        idPhuongThucThanhToan: idPhuongThuc,
        loaiHoaDon: loaiHoaDonQuyetDinh,
        tongTien: tongTienCanThanhToan.value,
        ...thongTinGiaoHang,
      };

      await thanhToan(hoaDon.value.id, requestData);

      // RESET SAU KHI THÀNH CÔNG
      const completedId = hoaDon.value.id;
      notify.success("Thanh toán thành công!");

      hoaDonChoList.value = hoaDonChoList.value.filter(
        (hd) => hd.id !== completedId
      );
      hoaDon.value = null;
      gioHang.value = [];
      selectedHoaDonId.value = null;

      router.push({ name: "ChiTietHD", params: { id: completedId } });
    } catch (err) {
      console.error(err);
      notify.error(err.response?.data?.message || "Thanh toán thất bại!");
    }
  };

  return {
    phuongThucThanhToan,
    isVnpayProcessing,
    tongTienCanThanhToan,
    handleVNPayPayment,
    handleThanhToan,
  };
}
