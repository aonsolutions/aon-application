import { get, post, put, remove } from "./request.js";
import { API } from "../environments/environments.js";


export const getDelivery = (data) => get(API.DELIVERIES + '/' + data.id, data);
export const getDeliveries = (data) => get(API.DELIVERIES, data);

export const getWarehouses = (data) => get(API.WAREHOUSES, data);
export const saveWarehouse = (data) => put(API.WAREHOUSES, data);
export const deleteWarehouse = (id)  => remove(`${API.WAREHOUSES}/${id}`, {id});

export const getElaborations = (data) => post(API.ELABORATION, data);
export const getElaboration = (id) => post(API.ELABORATION, {id});
export const saveElaboration = (data) => put(API.ELABORATION, data);
export const deleteElaboration = (id)  => remove(API.ELABORATION, {id});