import { get, post, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getProducts = (data) => get(`${API_URL}/product`, data);
export const getItems = (data) => get(`${API_URL}/product/items`, data);
export const getItem = (data) => get(`${API_URL}/product/item`, data);
export const getPackagingInfo = (data) => get(`${API_URL}/packaging`, data);

export const getProductCategories = (data) =>  get(`${API_URL}/product/category`, data);

export const saveProduct = (data) => post(`${API_URL}/product`, data);
export const saveItem = (data) => post(`${API_URL}/product/item`, data);

export const getInvestAssets = (data)  => get(`${API_URL}/invest`, data);
export const saveInvestAsset = (data)  => post(`${API_URL}/invest`, data);
export const deleteInvestAsset = (data)  => remove(`${API_URL}/invest`, data);
