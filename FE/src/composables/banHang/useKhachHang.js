import { ref } from "vue";
import {
  timKhachHangDaDangKy,
  capNhatKhachHang,
  themKhachHangMoi,
} from "@/service/BanHangService";
import Swal from "sweetalert2";

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

  // ID Khách lẻ mặc định (Thay bằng ID thật trong DB của bạn)
  const KHACH_LE_ID = "0F773ECB-16F4-4DE2-96F1-115BECAE963E";
  const khachLeMacDinh = {
    id: KHACH_LE_ID,
    hoTen: "Khách lẻ",
    sdt: "0000000000",
  };

  // 1. Hàm gán khách hàng (Core logic)
  const assignKhachHang = async (khachHang, keepInputEmpty = false) => {
    try {
      console.log("Chọn khách hàng:", khachHang);

      // Gọi API cập nhật khách cho hóa đơn
      await capNhatKhachHang(hoaDon.value.id, khachHang.id);

      // Cập nhật UI
      hoaDon.value.khachHang = khachHang;
      isGuestEditable.value = false;
      searchResults.value = [];

      // A. Xử lý ô Input tìm kiếm (Giữ rỗng hoặc điền tên)
      if (keepInputEmpty) {
        searchKeyword.value = "";
      } else {
        searchKeyword.value = khachHang.hoTen;
      }

      // B. Xử lý Form Giao Hàng

      // --- TRƯỜNG HỢP 1: LÀ KHÁCH LẺ (GUEST) ---
      if (khachHang.id === KHACH_LE_ID) {
        // FIX: Thay vì gọi resetFormGiaoHang() (làm tắt switch giao hàng),
        // ta chỉ xóa dữ liệu trong các ô input thôi.

        // 1. Reset thông tin cá nhân để nhập mới
        thongTinNguoiNhan.value.hoTen = "";
        thongTinNguoiNhan.value.sdt = "";

        // 2. Reset địa chỉ về rỗng (nếu muốn khách lẻ tự nhập lại từ đầu)
        thongTinNguoiNhan.value.tinhThanh = "";
        thongTinNguoiNhan.value.quanHuyen = "";
        thongTinNguoiNhan.value.phuongXa = "";
        thongTinNguoiNhan.value.diaChiCuThe = "";

        if (phiShip) phiShip.value = 0;

        notify.info("Đã chuyển về Khách lẻ.");
        return;
      }

      // TRƯỜNG HỢP 2: LÀ KHÁCH QUEN (Member)
      // B1. Luôn điền Tên và SĐT của khách vào form trước
      thongTinNguoiNhan.value.hoTen = khachHang.hoTen || "";
      thongTinNguoiNhan.value.sdt = khachHang.sdt || "";

      // B2. Xử lý Địa chỉ
      const listDiaChi = khachHang.danhSachDiaChi || khachHang.listDiaChi || [];

      if (listDiaChi.length > 0) {
        // --- Có địa chỉ lưu sẵn ---
        const dc = listDiaChi.find((d) => d.macDinh) || listDiaChi[0];

        const dbTinh = dc.thanhPho || dc.tinhThanh || "";
        const dbHuyen = dc.huyen || dc.quanHuyen || "";
        const dbXa = dc.xa || dc.phuongXa || "";
        const dbCuThe = dc.diaChiCuThe || "";

        thongTinNguoiNhan.value.tinhThanh = dbTinh;
        thongTinNguoiNhan.value.quanHuyen = dbHuyen;
        thongTinNguoiNhan.value.phuongXa = dbXa;
        thongTinNguoiNhan.value.diaChiCuThe = dbCuThe;

        // Gọi hàm tự động chọn Dropdown GHN
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

  // 2. Hàm tìm kiếm (Gắn vào @input)
  const handleTimKhachHang = async () => {
    const keyword = searchKeyword.value ? searchKeyword.value.trim() : "";

    // Trường hợp 1: Người dùng xóa trắng ô input
    if (!keyword) {
      // Nếu khách hiện tại KHÔNG PHẢI là Khách lẻ -> Reset về Khách lẻ
      if (hoaDon.value?.khachHang?.id !== KHACH_LE_ID) {
        // Truyền true để giữ ô input rỗng, không điền chữ "Khách lẻ" vào
        await assignKhachHang(khachLeMacDinh, true);
        notify.info("Đã chuyển về Khách lẻ.");
      }

      searchResults.value = [];
      showAddGuestButton.value = false;
      return; // Dừng lại, không gọi API
    }

    // Trường hợp 2: Từ khóa quá ngắn
    if (keyword.length < 2) {
      searchResults.value = [];
      return;
    }

    // Trường hợp 3: Gọi API tìm kiếm
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

  // 3. Hàm chọn từ danh sách gợi ý
  const handleSelectKhachHang = (khachHang) => {
    assignKhachHang(khachHang);
  };

  // 4. Hàm thêm nhanh khách hàng (Swal)
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
          await assignKhachHang(newKH); // Tự động chọn sau khi thêm
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
    khachLeMacDinh,
    assignKhachHang,
    handleTimKhachHang,
    handleSelectKhachHang,
    handleThemNhanhKhachHang,
    resetKhachHangState,
  };
}
