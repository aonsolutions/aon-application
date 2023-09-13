import { get, post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getSellers = (data) => get(`${API_URL}/seller`, data);
export const getRSellers = (data) => get(`${API_URL}/seller/rsellers`, data);
export const saveRsellers = (data) => post(`${API_URL}/seller/rsellers`, data);
export const updateRegistrySeller = (data) => post(`${API_URL}/seller/rseller/update`, data);
export const deleteRegistrySeller = (data) => remove(`${API_URL}/seller/rseller/`, data);