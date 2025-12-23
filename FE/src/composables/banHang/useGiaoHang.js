// src/composables/banHang/useGiaoHang.js
import { ref, onMounted, watch } from "vue";
import axios from "axios";
import { capNhatThongTinHoaDon } from "@/service/BanHangService";

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

  const resetFormGiaoHang = (turnOffToggle = true) => {
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

    if (turnOffToggle) {
      isBanGiaoHang.value = false;
    }
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
            shop_id: Number(GHN_SHOP_ID),
            from_district: Number(SHOP_DISTRICT_ID),
            to_district: Number(districtCode.value),
          },
        }
      );
      if (!serviceRes.data.data?.length)
        return notify.warning("GHN không hỗ trợ tuyến này!");
      const serviceId = serviceRes.data.data[0].service_id;
      const giaTriHang = hoaDon.value?.tongTien || 0;

      const feeRes = await axios.post(
        `${GHN_API_BASE}/v2/shipping-order/fee`,
        {
          service_id: Number(serviceId),
          insurance_value: Math.min(giaTriHang, 5000000),
          from_district_id: Number(SHOP_DISTRICT_ID),
          to_district_id: Number(districtCode.value),
          to_ward_code: String(wardCode.value),
          height: 15,
          length: 15,
          weight: 1000,
          width: 15,
        },
        { headers: { token: GHN_TOKEN, shop_id: String(GHN_SHOP_ID) } }
      );

      if (feeRes.data.code === 200) {
        const shipPrice = feeRes.data.data.total;
        phiShip.value = shipPrice;

        if (hoaDon.value?.id) {
          const payload = {
            tenKhachHang: thongTinNguoiNhan.value.hoTen,
            sdt: thongTinNguoiNhan.value.sdt,
            diaChiGiaoHang: `${thongTinNguoiNhan.value.diaChiCuThe}, ${thongTinNguoiNhan.value.phuongXa}, ${thongTinNguoiNhan.value.quanHuyen}, ${thongTinNguoiNhan.value.tinhThanh}`,
            tinhThanh: thongTinNguoiNhan.value.tinhThanh,
            quanHuyen: thongTinNguoiNhan.value.quanHuyen,
            phuongXa: thongTinNguoiNhan.value.phuongXa,
            diaChiCuThe: thongTinNguoiNhan.value.diaChiCuThe,
            loaiHoaDon: "Giao hàng",

            phiVanChuyen: shipPrice,
          };

          console.log("💾 Cập nhật ship vào DB:", shipPrice);
          // Gọi API Backend
          const resBE = await capNhatThongTinHoaDon(hoaDon.value.id, payload);

          if (resBE.data) {
            const dataMoi = resBE.data;

            const khachHangCu = hoaDon.value.khachHang;

            hoaDon.value = {
              ...dataMoi,
              khachHang: dataMoi.khachHang || khachHangCu,
            };
          }
        }
        notify.success(`Phí ship: ${phiShip.value.toLocaleString()} ₫`);
      }
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

  //Hàm chuẩn hóa tiếng việt
  const removeVietnameseTones = (str) => {
    if (!str) return "";
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
    str = str.replace(/đ/g, "d");
    str = str.replace(/À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ/g, "A");
    str = str.replace(/È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ/g, "E");
    str = str.replace(/Ì|Í|Ị|Ỉ|Ĩ/g, "I");
    str = str.replace(/Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ/g, "O");
    str = str.replace(/Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ/g, "U");
    str = str.replace(/Ỳ|Ý|Ỵ|Ỷ|Ỹ/g, "Y");
    str = str.replace(/Đ/g, "D");
    str = str.replace(/[^a-zA-Z0-9 ]/g, "");
    return str.trim().toLowerCase();
  };

  const checkNameMatch = (n1, n2) => {
    if (!n1 || !n2) return false;

    const s1 = removeVietnameseTones(n1);
    const s2 = removeVietnameseTones(n2);
    if (s1.includes(s2) || s2.includes(s1)) return true;

    const getSkeleton = (str) => {
      return removeVietnameseTones(str)
        .replace(
          /[aeiouyàáạảãâầấậẩẫăằắặẳẵèéẹẻẽêềếệểễìíịỉĩòóọỏõôồốộổỗơờớợởỡùúụủũưừứựửữỳýỵỷỹđ?]/g,
          ""
        )
        .replace(/[^a-z0-9]/g, "");
    };

    const sk1 = getSkeleton(n1);
    const sk2 = getSkeleton(n2);

    if (sk1.length > 3 && sk2.length > 3) {
      return sk1.includes(sk2) || sk2.includes(sk1);
    }

    try {
      const pattern = n1
        .replace(/[.*+^${}()|[\]\\]/g, "\\$&")
        .replace(/\?/g, ".");
      const regex = new RegExp(pattern, "i");
      if (regex.test(n2)) return true;
    } catch (e) {}

    return false;
  };

  const autoFillAddressFromNames = async (tinh, huyen, xa) => {
    console.log("🔄 Bắt đầu map địa chỉ:", { tinh, huyen, xa });

    if (!tinh) return;

    if (provinces.value.length === 0) {
      try {
        const res = await axios.get(`${GHN_API_BASE}/master-data/province`, {
          headers: { token: GHN_TOKEN },
        });
        provinces.value = res.data.data;
      } catch (e) {
        console.error(e);
      }
    }

    const p = provinces.value.find((x) => checkNameMatch(x.ProvinceName, tinh));

    if (p) {
      console.log("✅ Khớp Tỉnh:", p.ProvinceName);
      provinceCode.value = p.ProvinceID;

      await handleProvinceChange();

      // 2. TÌM HUYỆN
      if (huyen) {
        const d = districts.value.find((x) =>
          checkNameMatch(x.DistrictName, huyen)
        );

        if (d) {
          console.log("✅ Khớp Huyện:", d.DistrictName);
          districtCode.value = d.DistrictID;

          await handleDistrictChange();

          if (xa) {
            const w = wards.value.find((x) => checkNameMatch(x.WardName, xa));
            if (w) {
              console.log("✅ Khớp Xã:", w.WardName);
              wardCode.value = w.WardCode;

              if (isBanGiaoHang.value) {
                await calculateShippingFee();
              }
            } else {
              console.warn("❌ Không tìm thấy Xã:", xa);
            }
          }
        } else {
          console.warn("❌ Không tìm thấy Huyện:", huyen);
        }
      }
    } else {
      console.warn("❌ Không tìm thấy Tỉnh:", tinh);
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

    isBanGiaoHang.value = true;

    showDiaChiModal.value = false;
    notify.success("Đã thay đổi địa chỉ giao hàng!");
  };

  watch(isBanGiaoHang, async (newVal) => {
    if (newVal) {
      if (districtCode.value && wardCode.value) await calculateShippingFee();
    } else {
      phiShip.value = 0;

      if (hoaDon.value?.id) {
        const payload = {
          phiVanChuyen: 0,
          loaiHoaDon: "Tại quầy",
        };

        try {
          const resBE = await capNhatThongTinHoaDon(hoaDon.value.id, payload);
          console.log("Đã tắt giao hàng, cập nhật lại hóa đơn từ BE");

          if (resBE.data) {
            const dataMoi = resBE.data;
            const khachHangCu = hoaDon.value.khachHang;

            hoaDon.value = {
              ...dataMoi,
              khachHang: dataMoi.khachHang || khachHangCu,
            };
          }
        } catch (e) {
          console.error("Lỗi cập nhật tắt giao hàng", e);
        }
      }
    }
  });

  watch([districtCode, wardCode], async ([newDist, newWard]) => {
    if (isBanGiaoHang.value && newDist && newWard) {
      await calculateShippingFee();
    }
  });

  watch(
    () => hoaDon.value?.id,
    (newId, oldId) => {
      if (!hoaDon.value) return;
      if (newId !== oldId) {
        if (hoaDon.value.phiVanChuyen && hoaDon.value.phiVanChuyen > 0) {
          phiShip.value = hoaDon.value.phiVanChuyen;
          isBanGiaoHang.value = true;
          thongTinNguoiNhan.value.hoTen = hoaDon.value.tenKhachHang || "";
          thongTinNguoiNhan.value.sdt = hoaDon.value.sdt || "";
          thongTinNguoiNhan.value.diaChiCuThe = hoaDon.value.diaChiCuThe || "";
        } else {
          resetFormGiaoHang(true);
        }
      }
    }
  );

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
