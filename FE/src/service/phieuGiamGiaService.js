import axios from "axios";

const API_URL = "http://localhost:8080/admin";

//Get all with pagination
export async function getAllPhieuGiamGia(page = 0, size = 10) {
  try {
    // Gọi 2 API song song (đều đang trả về dữ liệu có content)
    const [resChung, resCaNhan] = await Promise.all([
      axios.get(`${API_URL}/phieu-giam-gia`, {
        params: { page: 0, size: 1000 },
      }), // lấy full để gộp
      axios.get(`${API_URL}/phieu-giam-gia-ca-nhan`, {
        params: { page: 0, size: 1000 },
      }),
    ]);

    const chungData = resChung.data?.content ?? resChung.data ?? [];
    const caNhanData = resCaNhan.data?.content ?? resCaNhan.data ?? [];

    const chung = chungData.map((item) => ({
      id: item.id,
      ma: item.ma,
      ten: item.ten,
      ngayBatDau: item.ngayBatDau,
      ngayKetThuc: item.ngayKetThuc,
      trangThai: item.trangThai,
      loaiPhieu: "Thông thường",
      moTa: item.moTa,

      // ⚡ Thêm các trường quan trọng bị thiếu:
      hinhThucGiamGia: item.hinhThucGiamGia ?? false,
      giaTriGiam: item.giaTriGiam ?? 0,
      giaTriGiamToiThieu: item.giaTriGiamToiThieu ?? 0,
      giaTriGiamToiDa: item.giaTriGiamToiDa ?? 0,
    }));

    const caNhan = caNhanData.map((item) => ({
      id: item.id,
      ma: item.ma,
      ten: item.ten,
      ngayBatDau: item.phieuGiamGia?.ngayBatDau,
      ngayKetThuc: item.ngayHetHan,
      trangThai: item.trangThai,
      loaiPhieu: "Cá nhân",
      moTa: item.phieuGiamGia?.moTa,
      tenKhachHang: item.khachHang?.hoTen,

      // 🔹 Thêm các trường chi tiết để hiển thị đúng trong form sửa:
      hinhThucGiamGia: item.phieuGiamGia?.hinhThucGiamGia ?? false,
      giaTriGiam: item.phieuGiamGia?.giaTriGiam ?? 0,
      giaTriGiamToiThieu: item.phieuGiamGia?.giaTriGiamToiThieu ?? 0,
      giaTriGiamToiDa: item.phieuGiamGia?.giaTriGiamToiDa ?? 0,
    }));

    // 🔹 Gộp tất cả dữ liệu
    const all = [...chung, ...caNhan];

    // 🔹 Tính tổng phần tử và số trang
    const totalElements = all.length;
    const totalPages = Math.ceil(totalElements / size);

    // 🔹 Cắt dữ liệu theo trang hiện tại
    const start = page * size;
    const end = start + size;
    const content = all.slice(start, end);

    return {
      content,
      totalElements,
      totalPages,
      size,
      number: page,
    };
  } catch (error) {
    console.error("❌ Lỗi khi tải dữ liệu phiếu giảm giá:", error);
    throw error;
  }
}
export const getAllKhachHang = (page = 0, size = 10) => axios.get(`${API_URL}/khach-hang/page`, {params :{page, size}});


//Get by ID
export const getPhieuGiamGiaById = (id) => axios.get(`${API_URL}/${id}`);


//Create
export const createPhieuGiamGia = (data) => {
  return axios.post(`${API_URL}/phieu-giam-gia`, data, {
    headers: { "Content-Type": "application/json" },
  });
};
export const createPhieuGiamGiaCaNhan = (data) => {
  return axios.post(`${API_URL}/phieu-giam-gia-ca-nhan`, data, {
    headers: { "Content-Type": "application/json" },
  })
}


//Update
export async function updatePhieuGiamGia(id, payload) {
  try {
    return await axios.put(`${API_URL}/phieu-giam-gia/${id}`, payload);
  } catch (error) {
    console.error("❌ Lỗi khi cập nhật phiếu giảm giá:", error);
    throw error;
  }
}
export async function updatePhieuGiamGiaCaNhan(id, payload) {
  try {
    return await axios.put(`${API_URL}/phieu-giam-gia-ca-nhan/${id}`, payload);
  } catch (error) {
    console.error("❌ Lỗi khi cập nhật phiếu giảm giá cá nhân:", error);
    throw error;
  }
}

//Delete
export const deletePhieuGiamGiaByID = (id) => axios.delete(`${API_URL}/${id}`);


//Update trang thai
export async function updateTrangThaiPhieuGiamGia(id, trangThai) {
  return axios.patch(`${API_URL}/phieu-giam-gia/${id}/trang-thai`, {
    trangThai,
  });
}

export async function updateTrangThaiPhieuGiamGiaCaNhan(id, trangThai) {
  return axios.patch(`${API_URL}/phieu-giam-gia-ca-nhan/${id}/trang-thai`, {
    trangThai,
  });
}