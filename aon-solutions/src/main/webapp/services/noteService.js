import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getNotes = (data) => get(`${API_URL}/note`, data);
export const getNote = (data) => get(`${API_URL}/note/one`, data);
export const saveNote = (data) => post(`${API_URL}/note`, data);
export const deleteNote = (data) => remove(`${API_URL}/note`, data);