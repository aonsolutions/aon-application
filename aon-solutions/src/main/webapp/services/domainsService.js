import { getPro, putPro, get } from "./request.js";
import { PRO_URL, API_URL } from "../environments/environments.js";

//Quitar PRO_URL para pruebas en local
export const getDomains = (data) => getPro(`${PRO_URL}/` + `${API_URL}/domain`, data);
export const updateDomains = (data) => putPro(`${PRO_URL}/domain`, data);

export const getCustomers = (data) => get(`${API_URL}/customer`, data);
