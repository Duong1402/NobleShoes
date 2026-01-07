// src/composables/banHang/useThanhToan.js
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  thanhToan,
  getAllPhuongThucThanhToan,
  themPhuongThucMoi,
  kiemTraVoucher,
} from "@/service/BanHangService";
import Swal from "sweetalert2";
import axios from "axios";

export function useThanhToan(
  notify,
  hoaDon,
  gioHang,
  hoaDonChoList,
  selectedHoaDonId,
  tongTienSauGiam,
  isBanGiaoHang,
  phiShip,
  thongTinNguoiNhan,
  handleSyncMoney
) {
  const dynamicPhuongThucMap = ref({
    TIEN_MAT: null,
    CHUYEN_KHOAN: null,
    CA_HAI: null,
  });

  const router = useRouter();
  const phuongThucThanhToan = ref("TIEN_MAT");
  const isVnpayProcessing = ref(false);

  const initPhuongThucThanhToan = async () => {
    try {
      console.log("🔄 Đang tải danh sách PTTT...");
      let res = await getAllPhuongThucThanhToan();
      let list = res.data || [];

      if (list.length === 0) {
        console.warn("⚠️ DB trống! Đang tự khởi tạo PTTT mặc định...");

        await themPhuongThucMoi({
          ma: "TIEN_MAT",
          ten: "Tiền mặt",
          trangThai: 1,
        });
        await themPhuongThucMoi({
          ma: "CHUYEN_KHOAN",
          ten: "Chuyển khoản",
          trangThai: 1,
        });
        await themPhuongThucMoi({
          ma: "CA_HAI",
          ten: "Tiền mặt & Chuyển khoản",
          trangThai: 1,
        });

        res = await getAllPhuongThucThanhToan();
        list = res.data || [];
      }

      console.log("✅ Danh sách PTTT từ API:", list);

      const tm = list.find(
        (p) => p.ma === "TIEN_MAT" || p.ten.toLowerCase().includes("tiền mặt")
      );
      const ck = list.find(
        (p) =>
          p.ma === "CHUYEN_KHOAN" ||
          p.ten.toLowerCase().includes("chuyển khoản")
      );
      const ch = list.find(
        (p) => p.ma === "CA_HAI" || p.ten.toLowerCase().includes("kết hợp")
      );

      if (tm) dynamicPhuongThucMap.value.TIEN_MAT = tm.id;
      if (ck) dynamicPhuongThucMap.value.CHUYEN_KHOAN = ck.id;
      if (ch) dynamicPhuongThucMap.value.CA_HAI = ch.id;

      console.log("✅ Map PTTT sau khi load:", dynamicPhuongThucMap.value);
    } catch (e) {
      console.error("❌ Lỗi load PTTT:", e);
    }
  };

  onMounted(() => {
    initPhuongThucThanhToan();
  });

  const tongTienCanThanhToan = computed(() => {
    // const tienHang = Number(tongTienSauGiam.value) || 0;
    // const phi = Number(phiShip.value) || 0;

    // console.log("Đang tính tiền:", {
    //   tienHang,
    //   phiShip: phi,
    //   isGiaoHang: isBanGiaoHang.value,
    // });

    // if (isBanGiaoHang.value) {
    //   return tienHang + phi;
    // }
    // return tienHang;
    return Number(tongTienSauGiam.value) || 0;
  });

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
          amount: tongTienCanThanhToan.value,
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
  const handleThanhToan = async () => {
    if (!hoaDon.value) return notify.warning("Chưa có hóa đơn!");
    if (gioHang.value.length === 0) return notify.warning("Giỏ hàng rỗng!");

    const selectedPtttCode = phuongThucThanhToan.value;

    let thongTinGiaoHang = {};
    let loaiHoaDonQuyetDinh = "Tại cửa hàng";
    let finalPhiShip = 0;

    if (isBanGiaoHang.value) {
      const nguoiNhan = thongTinNguoiNhan.value;
      finalPhiShip = Number(phiShip.value) || 0;

      if (!nguoiNhan.hoTen || !nguoiNhan.sdt) {
        return notify.warning("Vui lòng điền đủ Tên, SĐT ");
      }

      let diaChiCuTheFinal = nguoiNhan.diaChiCuThe;
      if (!diaChiCuTheFinal) {
        if (nguoiNhan.phuongXa && nguoiNhan.quanHuyen && nguoiNhan.tinhThanh) {
          diaChiCuTheFinal = nguoiNhan.phuongXa;
        } else {
          return notify.warning("Vui lòng nhập địa chỉ nhận hàng!");
        }
      }

      loaiHoaDonQuyetDinh = "Online";

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
        tenKhachHang: nguoiNhan.hoTen,
        sdt: nguoiNhan.sdt,
        diaChiGiaoHang: diaChiDayDu,

        tinhThanh: nguoiNhan.tinhThanh,
        quanHuyen: nguoiNhan.quanHuyen,
        phuongXa: nguoiNhan.phuongXa,
        diaChiCuThe: diaChiCuTheFinal,
      };
    }

    if (selectedPtttCode === "CHUYEN_KHOAN") {
      await handleVNPayPayment();
      return;
    }

    let idPhuongThuc = dynamicPhuongThucMap.value[selectedPtttCode];
    if (!idPhuongThuc) {
      await initPhuongThucThanhToan();
      idPhuongThuc = dynamicPhuongThucMap.value[selectedPtttCode];
      if (!idPhuongThuc) return notify.error(`Lỗi PTTT: ${selectedPtttCode}`);
    }

    const tongTienCuoiCung = Number(tongTienSauGiam.value) || 0;

    try {
      const resCheck = await kiemTraVoucher(hoaDon.value.id);
      const data = resCheck.data;

      if (data?.coMaTotHon && data?.phieuGiamGiaTotNhat) {
        const confirmVoucher = await Swal.fire({
          title: "Có mã giảm giá tốt hơn",
          html: `
        <div style="text-align:left; font-size: 1.05em">
          <p>
            Mã đang áp dụng giảm:
            <b>${(data.soTienGiamHienTai || 0).toLocaleString()} ₫</b>
          </p>
          <p>
            Mã tốt hơn giảm:
            <b style="color:#d33">
              ${(data.soTienGiamTotNhat || 0).toLocaleString()} ₫
            </b>
          </p>
          <hr/>
          <p>
            Bạn có muốn <b>tiếp tục thanh toán</b>
            hay quay lại để chọn mã tốt hơn?
          </p>
        </div>
      `,
          icon: "warning",
          showCancelButton: true,
          confirmButtonText: "Tiếp tục thanh toán",
          cancelButtonText: "Chọn mã khác",
          confirmButtonColor: "#28a745",
          cancelButtonColor: "#3085d6",
        });

        if (!confirmVoucher.isConfirmed) {
          notify.info("Vui lòng chọn mã giảm tốt hơn trước khi thanh toán.");
          return;
        }
      }
    } catch (e) {
      console.warn("⚠️ Không kiểm tra được voucher:", e);
    }

    const tienHang = Number(hoaDon.value?.tongTien || 0);
    const tienGiam = Number(hoaDon.value?.soTienGiamGia || 0);
    const phiVC = isBanGiaoHang.value ? finalPhiShip : 0;
    const tongThanhToan = tongTienCuoiCung;

    const confirm = await Swal.fire({
      title: "Xác nhận Thanh toán?",
      html: `
  <div style="font-size:1.05em; text-align:left">
    
    <div style="display:flex; justify-content:space-between;">
      <span>Tiền hàng:</span>
      <strong>${tienHang.toLocaleString()} ₫</strong>
    </div>

    ${
      hoaDon.value?.phieuGiamGia?.ma
        ? `
     <div style="display:flex; justify-content:space-between;">
       <span>Mã áp dụng:</span>
       <strong>- ${hoaDon.value.phieuGiamGia.giaTriGiam.toLocaleString()} ₫</strong>
     </div>`
        : ""
    }

    ${
      isBanGiaoHang.value
        ? `
        <div style="display:flex; justify-content:space-between;">
          <span>Phí vận chuyển:</span>
          <strong>${phiVC.toLocaleString()} ₫</strong>
        </div>
        `
        : ""
    }

    <hr style="margin:10px 0"/>

    <div style="
      display:flex;
      justify-content:space-between;
      font-size:1.25em;
      font-weight:bold;
      color:#d33;
    ">
      <span>TỔNG THANH TOÁN:</span>
      <span>${tongThanhToan.toLocaleString()} ₫</span>
    </div>

    <div style="margin-top:8px; font-size:0.9em; font-style:italic;">
      (${loaiHoaDonQuyetDinh})
    </div>
  </div>
  `,
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Thanh toán",
      confirmButtonColor: "#28a745",
      cancelButtonText: "Hủy",
    });

    if (!confirm.isConfirmed) return;

    try {
      const requestData = {
        idPhuongThucThanhToan: idPhuongThuc,
        loaiHoaDon: loaiHoaDonQuyetDinh,

        phiVanChuyen: finalPhiShip,

        ...thongTinGiaoHang,
      };

      await thanhToan(hoaDon.value.id, requestData);

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
      let errorMessage = "Thanh toán thất bại!";
      const resData = err.response?.data;
      if (resData) {
        if (typeof resData === "string") errorMessage = resData;
        else if (resData.message) errorMessage = resData.message;
        else if (resData.error) errorMessage = resData.error;
      }
      notify.error(errorMessage);
      const keyword = errorMessage.toLowerCase();
      if (
        keyword.includes("phiếu") ||
        keyword.includes("tổng tiền") ||
        keyword.includes("hết hạn")
      ) {
        if (typeof handleSyncMoney === "function") await handleSyncMoney();
      }
    }
  };

  const handleChonPhuongThuc = (code) => {
    phuongThucThanhToan.value = code;
  };

  return {
    phuongThucThanhToan,
    isVnpayProcessing,
    tongTienCanThanhToan,
    handleVNPayPayment,
    handleThanhToan,
    handleChonPhuongThuc,
  };
}
