import { get, post } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { openFileUrl } from "./fileService.js";

export const getModelsFiscal = (data) => get(`${API_URL}/fiscal/models`, data);
export const getModels390Fiscal = (data) => get(`${API_URL}/fiscal/models390`, data);
export const getEstimationModelsFiscal = (data) => get(`${API_URL}/fiscal/estimations`, data);

export const setModelStatus = (data) => post(`${API_URL}/fiscal/markAsFinished`, data);

export const getFiscalModelsInvoinces = (data) => get(`${API_URL}/fiscal/models/invoices`, data);
export const getEstimationModelsFiscalInvoinces = (data) => get(`${API_URL}/fiscal/estimations/invoices`, data);

export const getFiscalModelsInvoincesExcel = (data) => openFileUrl(`${API_URL}/fiscal/models/invoices/excel?json=${data}`);
export const getEstimationModelsFiscalInvoincesExcel = (data) => openFileUrl(`${API_URL}/fiscal/estimations/invoices/excel?json=${data}`);

export const getFiscalModelsInvoincesCount = (data) => get(`${API_URL}/fiscal/models/invoices/count`, data);
export const getEstimationModelsFiscalInvoincesCount = (data) => get(`${API_URL}/fiscal/estimations/invoices/count`, data);

export const getFiscalModelsSalaries = (data) => get(`${API_URL}/fiscal/models/salaries`, data);
export const getEstimationModelsFiscalSalaries = (data) => get(`${API_URL}/fiscal/estimations/salaries`, data);

export const getFiscalModelsSalariesCount = (data) => get(`${API_URL}/fiscal/models/salaries/count`, data);
export const getEstimationModelsFiscalSalariesCount = (data) => get(`${API_URL}/fiscal/estimations/salaries/count`, data);
