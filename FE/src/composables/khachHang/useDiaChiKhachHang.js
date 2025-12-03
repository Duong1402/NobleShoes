import { ref, reactive, computed, watch } from "vue";
import { useNotify } from "../useNotify";
import Swal from "sweetalert2";
import { getDiaChiByKhachHangId } from "@/service/DiaChiService";

export function useDiaChiKhachHang(provincesData) {
  const notify = useNotify();
  const newAddresses = ref([]);
  const currentAddressIndex = ref(0);
  const isAddingNewAddress = ref(false);
  const khachHangDiaChiMap = ref(new Map());

  const currentAddressForm = reactive({
    tinhCode: "",
    huyenCode: "",
    xaCode: "",
    chiTiet: "",
    macDinh: false,
    id: null,
  });

  const currentDraftAddress = computed(() => {
    if (isAddingNewAddress.value) return currentAddressForm;
    if (newAddresses.value.length === 0) return null;

    const index = Math.min(
      currentAddressIndex.value,
      newAddresses.value.length - 1
    );
    return newAddresses.value[index];
  });

  const currentProvince = computed(
    () =>
      provincesData.value.find(
        (p) => String(p.code) === String(currentAddressForm.tinhCode)
      ) || null
  );

  const currentDistrict = computed(() => {
    if (!currentProvince.value) return null;
    return (
      currentProvince.value.districts?.find(
        (d) => String(d.code) === String(currentAddressForm.huyenCode)
      ) || null
    );
  });

  const currentWard = computed(() => {
    if (!currentDistrict.value) return null;
    return (
      currentDistrict.value.wards?.find(
        (w) => String(w.code) === String(currentAddressForm.xaCode)
      ) || null
    );
  });

  const districts = computed(() => currentProvince.value?.districts ?? []);
  const wards = computed(() => currentDistrict.value?.wards ?? []);

  const resetAddressForm = (makeDefault = true) => {
    currentAddressForm.tinhCode = "";
    currentAddressForm.huyenCode = "";
    currentAddressForm.xaCode = "";
    currentAddressForm.chiTiet = "";
    currentAddressForm.macDinh = makeDefault && newAddresses.value.length === 0;
    currentAddressForm.id = null;
  };

  const findCodeByName = (name, type) => {
    if (!name) return "";
    let target = null;
    if (type === "tinh") {
      target = provincesData.value.find((p) => p.name === name);
    } else if (type === "huyen" && currentProvince.value) {
      target = currentProvince.value.districts.find((d) => d.name === name);
    } else if (type === "xa" && currentDistrict.value) {
      target = currentDistrict.value.wards.find((w) => w.name === name);
    }
    return target?.code || "";
  };

  const syncAddressToForm = (address) => {
    if (!address) {
      resetAddressForm();
      return;
    }
    currentAddressForm.tinhCode = findCodeByName(address.thanhPho, "tinh");
    currentAddressForm.huyenCode = findCodeByName(address.huyen, "huyen");
    currentAddressForm.xaCode = findCodeByName(address.xa, "xa");

    if (!currentAddressForm.tinhCode) {
      currentAddressForm.tinhCode = "";
    }

    currentAddressForm.chiTiet = address.diaChiCuThe;
    currentAddressForm.macDinh = address.macDinh;
    currentAddressForm.id = address.id;
  };

  watch(
    [currentDraftAddress, isAddingNewAddress, provincesData],
    () => {
      if (isAddingNewAddress.value) return;
      syncAddressToForm(currentDraftAddress.value);
    },
    { deep: true, immediate: true }
  );

  const openAddressForm = () => {
    isAddingNewAddress.value = true;
    currentAddressIndex.value = newAddresses.value.length;
    resetAddressForm();
  };

  const closeAddressForm = () => {
    isAddingNewAddress.value = false;
    currentAddressIndex.value = 0;
    syncAddressToForm(newAddresses.value[0]);
  };

  const saveAddressToDraft = () => {
    if (!currentAddressForm.tinhCode || !currentAddressForm.chiTiet) {
      notify.error("Vui lòng nhập đầy đủ địa chỉ");
      return;
    }

    const tinh = currentProvince.value?.name || "";
    const huyen = currentDistrict.value?.name || "";
    const xa = currentWard.value?.name || "";

    const updatedDraftAddress = {
      id: currentAddressForm.id || crypto.randomUUID(),
      thanhPho: tinh,
      huyen: huyen,
      xa: xa,
      diaChiCuThe: currentAddressForm.chiTiet.trim(),
      macDinh: currentAddressForm.macDinh,
    };

    if (updatedDraftAddress.macDinh) {
      newAddresses.value.forEach((addr) => (addr.macDinh = false));
    }

    if (isAddingNewAddress.value) {
      newAddresses.value.push(updatedDraftAddress);
      isAddingNewAddress.value = false;
      currentAddressIndex.value = newAddresses.value.length - 1;
    } else {
      const index = currentAddressIndex.value;
      if (index >= 0 && index < newAddresses.value.length) {
        newAddresses.value[index] = updatedDraftAddress;
      }
    }

    if (
      !newAddresses.value.some((a) => a.macDinh) &&
      newAddresses.value.length > 0
    ) {
      newAddresses.value[0].macDinh = true;
    }

    notify.success(
      currentAddressForm.id
        ? "Cập nhật địa chỉ thành công."
        : "Thêm địa chỉ thành công."
    );
  };

  const deleteAddress = async (addressId) => {
    const result = await Swal.fire({
      title: "Xóa địa chỉ?",
      text: "Bạn có chắc chắn muốn xóa địa chỉ này không?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Xóa",
      cancelButtonText: "Hủy",
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
    });

    if (!result.isConfirmed) return;

    const indexToDelete = newAddresses.value.findIndex(
      (a) => a.id === addressId
    );

    if (indexToDelete !== -1) {
      const isDefault = newAddresses.value[indexToDelete].macDinh;
      newAddresses.value.splice(indexToDelete, 1);

      if (newAddresses.value.length === 0) {
        openAddressForm();
        return;
      }

      if (isDefault) {
        newAddresses.value[0].macDinh = true;
      }

      if (
        currentAddressIndex.value > 0 &&
        currentAddressIndex.value >= newAddresses.value.length
      ) {
        currentAddressIndex.value = newAddresses.value.length - 1;
      }

      syncAddressToForm(newAddresses.value[currentAddressIndex.value]);

      Swal.fire({
        icon: "success",
        title: "Đã xóa!",
        timer: 1200,
        showConfirmButton: false,
      });
    }
  };

  const handleDefaultChange = () => {
    if (isAddingNewAddress.value) {
      if (currentAddressForm.macDinh) {
        newAddresses.value.forEach((addr) => (addr.macDinh = false));
      }
    } else if (currentDraftAddress.value && currentAddressForm.macDinh) {
      newAddresses.value.forEach((addr) => (addr.macDinh = false));
      currentDraftAddress.value.macDinh = true;
      notify.success("Địa chỉ đã được đặt làm mặc định.");
    } else if (currentDraftAddress.value && !currentAddressForm.macDinh) {
      if (currentDraftAddress.value.macDinh && newAddresses.value.length > 1) {
        currentAddressForm.macDinh = true;
        notify.warning(
          "Cần có ít nhất một địa chỉ mặc định. Vui lòng đặt mặc định cho địa chỉ khác trước khi bỏ chọn cái này."
        );
      } else if (newAddresses.value.length === 1) {
        currentAddressForm.macDinh = true;
      }
    }
  };

  const nextAddress = () => {
    if (currentAddressIndex.value < newAddresses.value.length - 1) {
      currentAddressIndex.value++;
      isAddingNewAddress.value = false;
    }
  };

  const prevAddress = () => {
    if (currentAddressIndex.value > 0) {
      currentAddressIndex.value--;
      isAddingNewAddress.value = false;
    }
  };

  const getDisplayAddress = (kh) => {
    return khachHangDiaChiMap.value.get(kh.id) || "— Chưa có địa chỉ —";
  };

  const loadDiaChiForKhachHang = async (khachHangList) => {
    const promises = khachHangList
      .filter((kh) => !khachHangDiaChiMap.value.has(kh.id))
      .map(async (kh) => {
        try {
          const res = await getDiaChiByKhachHangId(kh.id);
          const addresses = res?.data ?? res ?? [];
          if (addresses.length > 0) {
            const dc = addresses.find((a) => a.macDinh) || addresses[0];
            const fullAddress = [dc.diaChiCuThe, dc.xa, dc.huyen, dc.thanhPho]
              .filter(Boolean)
              .join(", ");
            khachHangDiaChiMap.value.set(kh.id, fullAddress);
          } else {
            khachHangDiaChiMap.value.set(kh.id, "—");
          }
        } catch (e) {
          console.warn(`Không thể lấy địa chỉ cho KH ID: ${kh.id}`, e);
          khachHangDiaChiMap.value.set(kh.id, "— Lỗi tải địa chỉ —");
        }
      });

    await Promise.all(promises);
  };

  return {
    // state
    newAddresses,
    currentAddressIndex,
    isAddingNewAddress,
    currentAddressForm,
    currentDraftAddress,

    khachHangDiaChiMap,
    loadDiaChiForKhachHang,
    getDisplayAddress,

    // computed
    currentProvince,
    currentDistrict,
    currentWard,
    districts,
    wards,

    // actions
    openAddressForm,
    closeAddressForm,
    saveAddressToDraft,
    deleteAddress,
    handleDefaultChange,
    nextAddress,
    prevAddress,

    // internal utils (nếu muốn dùng bên ngoài)
    resetAddressForm,
    syncAddressToForm,
  };
}
