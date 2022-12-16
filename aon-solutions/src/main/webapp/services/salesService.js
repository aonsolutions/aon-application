import { get, post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

const SALES = `${API_URL}/sales`;

export const getSales = (data) => get(SALES, data);
export const getSale = (data) => get(`${SALES}/${data.id}`, data);
export const createSale = (data) => post(SALES, data);
export const saveSale = (data) => put(`${SALES}/${data.id}`, data);
export const deleteSale = (data)  => remove(`${SALES}/${data.id}`, data);


