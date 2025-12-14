import axios from "axios";

const API_BASE = "http://localhost:8080/admin/chat-lieu";

export const getAllChatLieu = () =>
  axios.get(API_BASE);

export const getChatLieuById = (id) =>
  axios.get(`${API_BASE}/${id}`);

export const createChatLieu = (data) =>
  axios.post(API_BASE, data);

export const updateChatLieu = (id, data) =>
  axios.put(`${API_BASE}/${id}`, data);

export const deleteChatLieu = (id) =>
  axios.delete(`${API_BASE}/${id}`);
