import axios from "axios";
const API_BASE = "http://localhost:8080/admin/day-giay";

export const getAllDayGiay = () => axios.get(API_BASE);
export const createDayGiay = (data) => axios.post(API_BASE, data);
export const updateDayGiay = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteDayGiay = (id) => axios.delete(`${API_BASE}/${id}`);
