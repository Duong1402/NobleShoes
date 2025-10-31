import axios from "axios";

const API_URL = "http://localhost:8080/admin/dot-giam-gia";
const API_BASE_URL = "http://localhost:8080/admin/san-pham";

export const getAllDotGiamGia = (page = 0, size = 10, sortBy = "ma") => axios.get(API_URL, {params :{page, size, sortBy}});
// Lấy tất cả sản phẩm 
export const getAllSanPham = async () => {
  const res = await axios.get(`${API_BASE_URL}/all`);
  return res.data;
};

// Cập nhật sản phẩm
export const updateSanPham = (id, data) =>
  axios.put(`${API_BASE_URL}/${id}`, data);


export const getDotGiamGiaById = (id) => axios.get(`${API_URL}/${id}`);
export const createDotGiamGia = (data) => {
  return axios.post(API_URL, data, {
    headers: { "Content-Type": "application/json" },
  })
}
export const updateDotGiamGia = (id, DotGiamGia) =>
  axios.put(`${API_URL}/${id}`, DotGiamGia);
export const deleteDotGiamGiaByID = (id) => axios.delete(`${API_URL}/${id}`);
