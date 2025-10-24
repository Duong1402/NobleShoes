import axios from "axios";
const BASE_URL = "http://localhost:8080/admin";

export const getAllDanhMuc = () => axios.get(`${BASE_URL}/danh-muc`).then(res => res.data);
export const getAllThuongHieu = () => axios.get(`${BASE_URL}/thuong-hieu`).then(res => res.data);
export const getAllXuatXu = () => axios.get(`${BASE_URL}/xuat-xu`).then(res => res.data);
export const getAllDayGiay = () => axios.get(`${BASE_URL}/day-giay`).then(res => res.data);
export const getAllDeGiay = () => axios.get(`${BASE_URL}/de-giay`).then(res => res.data);
export const getAllMucDichSuDung = () => axios.get(`${BASE_URL}/muc-dich-su-dung`).then(res => res.data);
export const getAllMauSac = () => axios.get(`${BASE_URL}/mau-sac`).then(res => res.data);
export const getAllKichThuoc = () => axios.get(`${BASE_URL}/kich-thuoc`).then(res => res.data);
export const getAllChatLieu = () => axios.get(`${BASE_URL}/chat-lieu`).then(res => res.data);
