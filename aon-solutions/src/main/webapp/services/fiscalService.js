import { get, post } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getModelsFiscal = (data) => get(`${API_URL}/fiscal/models`, data);
export const getModels390Fiscal = (data) => get(`${API_URL}/fiscal/models390`, data);
export const getEstimationModelsFiscal = (data) => get(`${API_URL}/fiscal/estimations`, data);

export const setModelStatus = (data) => post(`${API_URL}/fiscal/markAsFinished`, data);
