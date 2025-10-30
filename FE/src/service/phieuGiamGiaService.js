import axios from "axios";

const API_URL = "http://localhost:8080/admin/phieu-giam-gia";

export const getAllPhieuGiamGia = () => axios.get(API_URL);
export const getPhieuGiamGiaById = (id) => axios.get(`${API_URL}/${id}`);
export const createPhieuGiamGia = (data) => {
  return axios.post(API_URL, data, {
    headers: { "Content-Type": "application/json" },
  })
}
export const updatePhieuGiamGia = (id, PhieuGiamGia) =>
  axios.put(`${API_URL}/${id}`, PhieuGiamGia);
export const deletePhieuGiamGiaByID = (id) => axios.delete(`${API_URL}/${id}`);
