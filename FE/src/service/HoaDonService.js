import axios from "axios";

const API_BASE_URL = "http://localhost:8080/admin/hoa-don"; 

export const searchHoaDon = (params) => {
  return axios.get(API_BASE_URL, { params });
};

export const getHoaDonById = (id) => {
  return axios.get(`${API_BASE_URL}/${id}`);
};

export const updateHoaDon = (id, data) => {
  return axios.put(`${API_BASE_URL}/${id}`, data);
};

export const getLichSuHoaDon = (id) => {
  return axios.get(`${API_BASE_URL}/${id}/lich-su`);
};
