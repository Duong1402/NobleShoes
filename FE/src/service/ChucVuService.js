import axios from "axios";

const API_URL = "http://localhost:8080/admin/chuc-vu";

export const getAllChucVu = () => axios.get(API_URL);
