import axios from "axios";

const API_URL = "http://localhost:8080/admin/dot-giam-gia";

export const getAllDotGiamGia = (page = 0, size = 10, sortBy = "ma") => axios.get(API_URL, {params :{page, size, sortBy}});
export const getDotGiamGiaById = (id) => axios.get(`${API_URL}/${id}`);
export const createDotGiamGia = (data) => {
  return axios.post(API_URL, data, {
    headers: { "Content-Type": "application/json" },
  })
}
export const updateDotGiamGia = (id, DotGiamGia) =>
  axios.put(`${API_URL}/${id}`, DotGiamGia);
export const deleteDotGiamGiaByID = (id) => axios.delete(`${API_URL}/${id}`);
