import axios from "axios";
const API_BASE = "http://localhost:8080/admin/de-giay";

export const getAllDeGiay = () => axios.get(API_BASE);
export const createDeGiay = (data) => axios.post(API_BASE, data);
export const updateDeGiay = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteDeGiay = (id) => axios.delete(`${API_BASE}/${id}`);
