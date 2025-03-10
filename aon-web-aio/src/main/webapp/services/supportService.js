import { put, get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const setSupport = (data) =>  put(`${API_URL}/aonsupport`, data);
export const getSupport = (data) =>  get(`${API_URL}/aonsupport`, data);