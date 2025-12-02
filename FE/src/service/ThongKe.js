import api from "./axios.js"; 

const API_URL = "/api/thong-ke";

export const getOverviewData = (params) => {
    return api.get(`${API_URL}/overview`, { params });
};


export const getFilteredData = (params) => {
    return api.get(`${API_URL}/filtered`, { params });
};