// src/composables/banHang/useGiaoHang.js
import { ref, onMounted, watch } from "vue";
import axios from "axios";

const GHN_TOKEN = "b5ee90fc-cb41-11f0-9a40-b6e8e0387d5a"; // Thay token của bạn
const GHN_SHOP_ID = 6136354; // Thay ShopID
const GHN_API_BASE = "https://online-gateway.ghn.vn/shiip/public-api";
const SHOP_DISTRICT_ID = 3440;

export function useGiaoHang(notify, tongTienHang, hoaDon) {
  const isBanGiaoHang = ref(false);
  const phiShip = ref(0);
  const provinces = ref([]);
  const districts = ref([]);
  const wards = ref([]);

  const provinceCode = ref(null);
  const districtCode = ref(null);
  const wardCode = ref(null);

  const showDiaChiModal = ref(false);

  const thongTinNguoiNhan = ref({
    hoTen: "",
    sdt: "",
    tinhThanh: "",
    quanHuyen: "",
    phuongXa: "",
    diaChiCuThe: "",
  });

  onMounted(async () => {
    try {
      const res = await axios.get(`${GHN_API_BASE}/master-data/province`, {
        headers: { token: GHN_TOKEN },
      });
      provinces.value = res.data.data;
    } catch (e) {
      console.error(e);
    }
  });

  const resetFormGiaoHang = () => {
    thongTinNguoiNhan.value = {
      hoTen: "",
      sdt: "",
      tinhThanh: "",
      quanHuyen: "",
      phuongXa: "",
      diaChiCuThe: "",
    };
    provinceCode.value = null;
    districtCode.value = null;
    wardCode.value = null;
    phiShip.value = 0;
    isBanGiaoHang.value = false;
  };

  const handleProvinceChange = async () => {
    districtCode.value = null;
    wardCode.value = null;
    phiShip.value = 0;
    const p = provinces.value.find((x) => x.ProvinceID === provinceCode.value);
    thongTinNguoiNhan.value.tinhThanh = p ? p.ProvinceName : "";

    if (provinceCode.value) {
      const res = await axios.get(`${GHN_API_BASE}/master-data/district`, {
        headers: { token: GHN_TOKEN },
        params: { province_id: provinceCode.value },
      });
      districts.value = res.data.data;
    }
  };

  const handleDistrictChange = async () => {
    wardCode.value = null;
    phiShip.value = 0;
    const d = districts.value.find((x) => x.DistrictID === districtCode.value);
    thongTinNguoiNhan.value.quanHuyen = d ? d.DistrictName : "";

    if (districtCode.value) {
      const res = await axios.get(`${GHN_API_BASE}/master-data/ward`, {
        headers: { token: GHN_TOKEN },
        params: { district_id: districtCode.value },
      });
      wards.value = res.data.data;
    }
  };

  const calculateShippingFee = async () => {
    if (!isBanGiaoHang.value || !districtCode.value || !wardCode.value) return;
    try {
      const serviceRes = await axios.get(
        `${GHN_API_BASE}/v2/shipping-order/available-services`,
        {
          headers: { token: GHN_TOKEN },
          params: {
            shop_id: GHN_SHOP_ID,
            from_district: SHOP_DISTRICT_ID,
            to_district: districtCode.value,
          },
        }
      );
      if (!serviceRes.data.data?.length)
        return notify.warning("Không hỗ trợ giao!");

      const feeRes = await axios.post(
        `${GHN_API_BASE}/v2/shipping-order/fee`,
        {
          service_id: serviceRes.data.data[0].service_id,
          insurance_value: Math.min(tongTienHang.value, 5000000), // Max bảo hiểm 5tr
          from_district_id: SHOP_DISTRICT_ID,
          to_district_id: districtCode.value,
          to_ward_code: wardCode.value,
          height: 15,
          length: 15,
          weight: 1000,
          width: 15,
        },
        { headers: { token: GHN_TOKEN, shop_id: GHN_SHOP_ID } }
      );

      phiShip.value = feeRes.data.data.total;
      notify.success(`Phí ship: ${phiShip.value.toLocaleString()} ₫`);
    } catch (e) {
      console.error(e);
      phiShip.value = 0;
    }
  };

  const handleWardChange = () => {
    const w = wards.value.find((x) => x.WardCode === wardCode.value);
    thongTinNguoiNhan.value.phuongXa = w ? w.WardName : "";
    calculateShippingFee();
  };

  // Helper helpers
  const checkNameMatch = (n1, n2) =>
    n1 &&
    n2 &&
    (n1.toLowerCase().includes(n2.toLowerCase()) ||
      n2.toLowerCase().includes(n1.toLowerCase()));

  const autoFillAddressFromNames = async (tinh, huyen, xa) => {
    if (!tinh) return;
    const p = provinces.value.find((x) => checkNameMatch(x.ProvinceName, tinh));
    if (p) {
      provinceCode.value = p.ProvinceID;
      await handleProvinceChange();
      if (huyen) {
        const d = districts.value.find((x) =>
          checkNameMatch(x.DistrictName, huyen)
        );
        if (d) {
          districtCode.value = d.DistrictID;
          await handleDistrictChange();
          if (xa) {
            const w = wards.value.find((x) => checkNameMatch(x.WardName, xa));
            if (w) {
              wardCode.value = w.WardCode;
              handleWardChange();
            }
          }
        }
      }
    }
  };
  const handleOpenModalDiaChi = () => {
    if (!hoaDon.value?.khachHang)
      return notify.warning("Vui lòng chọn khách hàng trước!");
    const listDiaChi = hoaDon.value.khachHang.danhSachDiaChi || [];
    if (listDiaChi.length === 0)
      return notify.warning("Khách hàng này chưa lưu địa chỉ nào!");
    showDiaChiModal.value = true;
  };

  // 2. Hàm chọn địa chỉ từ Modal (LOGIC BẠN GỬI Ở TRÊN)
  const handleChonDiaChiTuModal = async (diaChi) => {
    console.log("Chọn địa chỉ từ Modal:", diaChi);
    const dbTinh = diaChi.thanhPho || diaChi.tinhThanh || "";
    const dbHuyen = diaChi.huyen || diaChi.quanHuyen || "";
    const dbXa = diaChi.xa || diaChi.phuongXa || "";
    const dbCuThe = diaChi.diaChiCuThe || "";

    thongTinNguoiNhan.value.tinhThanh = dbTinh;
    thongTinNguoiNhan.value.quanHuyen = dbHuyen;
    thongTinNguoiNhan.value.phuongXa = dbXa;
    thongTinNguoiNhan.value.diaChiCuThe = dbCuThe;

    // Map lại ID GHN
    await autoFillAddressFromNames(dbTinh, dbHuyen, dbXa);

    showDiaChiModal.value = false;
    notify.success("Đã thay đổi địa chỉ giao hàng!");
  };

  watch(isBanGiaoHang, async (newVal) => {
    // Khi nút toggle được BẬT (newVal === true)
    if (newVal) {
      // Kiểm tra xem đã có sẵn địa chỉ (được map ID) chưa
      if (districtCode.value && wardCode.value) {
        console.log("Toggle bật -> Đã có sẵn địa chỉ -> Tính ship ngay!");
        await calculateShippingFee();
      } else {
        console.log("Toggle bật -> Chưa có địa chỉ -> Chờ người dùng chọn.");
      }
    } else {
      // Khi nút toggle TẮT -> Reset phí ship về 0 cho gọn
      phiShip.value = 0;
    }
  });

  watch([districtCode, wardCode], async ([newDist, newWard]) => {
    // Chỉ cần đang Bật giao hàng + Có đủ địa chỉ là tính lại ngay
    // (Bỏ điều kiện phiShip === 0 để khách đổi địa chỉ nó còn cập nhật giá mới)
    if (isBanGiaoHang.value && newDist && newWard) {
      console.log("📍 Địa chỉ đổi -> Tính lại ship...");
      await calculateShippingFee();
    }
  });

  return {
    isBanGiaoHang,
    phiShip,
    provinces,
    districts,
    wards,
    provinceCode,
    districtCode,
    wardCode,
    thongTinNguoiNhan,
    showDiaChiModal,
    calculateShippingFee,
    handleProvinceChange,
    handleDistrictChange,
    handleWardChange,
    handleOpenModalDiaChi,
    handleChonDiaChiTuModal,
    autoFillAddressFromNames,
    resetFormGiaoHang,
  };
}
