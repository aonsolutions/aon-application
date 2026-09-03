import { get, post, put, remove } from "./request.js";
import { API } from "../environments/environments.js";


export const getDelivery = (data) => get(API.DELIVERIES + '/' + data.id, data);
export const getDeliveries = (data) => get(API.DELIVERIES, data);
export const deleteDelivery = (data) => remove(API.DELIVERIES + '/' + data.id, data);

export const getWarehouses = (data) => get(API.WAREHOUSES, data);
export const getWarehouse = (id) => get(API.WAREHOUSES + '/' + id, {id});
export const saveWarehouse = (data) => put(API.WAREHOUSES, data);
export const deleteWarehouse = (id)  => remove(`${API.WAREHOUSES}/${id}`, {id});

export const getElaborations = (data) => get(API.ELABORATION, data);
export const getElaboration = (id) => get(API.ELABORATION, {id});
export const saveElaboration = (data) => put(API.ELABORATION, data);
export const deleteElaboration = (id)  => remove(API.ELABORATION, {id});
export const saveElaborationSerial = (data) => put(API.ELABORATION_SERIAL, data);

export const deleteElaborationPackage = (id)  => remove(`${API.ELABORATIONS}/packages/${id}`, {id});

export const deletePackage = (id)  => remove(`${API.PRODUCT}/package`, {id});

export const adjustComposition = (data) => post(`${API.PRODUCT}/package/adjustComposition`, data);

export const addPackageStock = (data) => put(`${API.PACKAGE}/stock/add`, data);
export const movePackageStock = (data) => put(`${API.PACKAGE}/stock/move`, data);

export const getItemStock = (itemId) => get(`${API.STOCK}/item/${itemId}`, {itemId});


export const getInventories = (data) => get(API.INVENTORIES, data);
export const getInventory = (id) => get(API.INVENTORIES + '/' + id, {id});
export const saveInventoryDetails = (data) => put(`${API.INVENTORIES}/${data.inventory}/detail`, data);