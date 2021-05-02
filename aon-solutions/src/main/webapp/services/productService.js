import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getProducts = (data) => get(`${API_URL}/product`, data);
export const getItems = (data) => get(`${API_URL}/product/item`, data);
