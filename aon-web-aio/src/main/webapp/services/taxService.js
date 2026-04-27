import { API_URL } from "../environments/environments";
import { get, put } from "./request";

export const getTaxes = (data) => get(`${API_URL}/taxes`, data || {});
export const getVats = (data) => get(`${API_URL}/taxes/vats`, data || {});
export const getWithholdings = (data) => get(`${API_URL}/taxes/withholdings`, data || {});

export const getTax = (id) => get(`${API_URL}/taxes/${id}`, {id});
export const saveTax = (data) => post(`${API_URL}/taxes`, data);
