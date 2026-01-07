import axios from "axios";

const API_URL = "http://localhost:8080/admin/phieu-giam-gia-ca-nhan";

export const getAllPhieuGiamGiaCaNhan = () => axios.get(API_URL);
export const getPhieuGiamGiaCaNhanById = (id) => axios.get(`${API_URL}/${id}`);
export const createPhieuGiamGiaCaNhan = (data) => {
  return axios.post(API_URL, data, {
    headers: { "Content-Type": "application/json" },
  })
}
export const updatePhieuGiamGiaCaNhan = (id, PhieuGiamGiaCaNhan) =>
  axios.put(`${API_URL}/${id}`, PhieuGiamGiaCaNhan);
export const deletePhieuGiamGiaCaNhanByID = (id) => axios.delete(`${API_URL}/${id}`);