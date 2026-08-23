import { get, post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getProducts = (data) => get(`${API_URL}/product`, data);
export const getItems = (data) => get(`${API_URL}/product/items`, data);
export const getItemsByBarcode = (data) => get(`${API_URL}/product/barcode/items`, data);
export const getRItems = (data) => get(`${API_URL}/product/ritem`, data);
export const getItem = (data) => get(`${API_URL}/product/item`, data);
export const getPackage = (data) => get(`${API_URL}/product/package`, data);

export const getProductCategories = (data) =>  get(`${API_URL}/product/category`, data);

export const saveProduct = (data) => post(`${API_URL}/product`, data);
export const saveItem = (data) => post(`${API_URL}/product/item`, data);

// REGISTRY ITEM

export const saveRegistryItem = (data) => post(`${API_URL}/product/ritem`, data);
export const updateRegistryItem = (data) => post(`${API_URL}/product/ritem/update`, data);
export const updateAllTargetItem = (data) => put(`${API_URL}/product/update-all-target-item`, data);

export const getInvestAssets = (data)  => get(`${API_URL}/invest`, data);
export const saveInvestAsset = (data)  => post(`${API_URL}/invest`, data);
export const deleteInvestAsset = (data)  => remove(`${API_URL}/invest`, data);

export const getPackaging = (data) => post(`${API_URL}/packaging`, data);
export const savePackaging = (data) => put(`${API_URL}/packaging`, data);

export const getDeliveryPackaging = (data) => get(`${API_URL}/packaging/deliveryPackaging`, data);
export const saveDeliveryPackaging = (data) => put(`${API_URL}/packaging/deliveryPackaging`, data);
export const acceptDeliveryPackaging = (data) => put(`${API_URL}/packaging/deliveryPackaging/accept`, data);
export const deleteDeliveryPackaging = (data) => remove(`${API_URL}/packaging/deliveryPackaging`, data);
export const subtractDeliveryPackagingComposition = (data) => remove(`${API_URL}/packaging/deliveryPackagingComposition`, data);
export const addDeliveryPackagingComposition = (data) => put(`${API_URL}/packaging/deliveryPackagingComposition/add`, data);