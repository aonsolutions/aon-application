import { getPro, putPro, get, put, remove } from "./request.js";
import { PRO_URL, API_URL } from "../environments/environments.js";

//Quitar PRO_URL para pruebas en local
export const getDomains = (data) => getPro(`${PRO_URL}/${API_URL}/domain`, data);
// export const getDomains = (data) => get(`${API_URL}/domain`, data);
export const updateDomains = (data) => putPro(`${PRO_URL}/${API_URL}/domain`, data);
// export const updateDomains = (data) => put(`${API_URL}/domain`, data);

export const getCustomers = (data) => get(`${API_URL}/customer`, data);

// export const getBooking = (data) => get(`${API_URL}/booking`, data);
export const getBooking = (data) => getPro(`${PRO_URL}/${API_URL}/booking`, data);

export const saveDomainLinked = (data) => put(`${API_URL}/domain/link`, data);
export const deleteDomainLinked = (data) => remove(`${API_URL}/domain/link`, data);

export const updateCustomerBillable = (data) => put(`${API_URL}/customer/billable`, data);

export const updateDomainBooking = (data) => put(`${API_URL}/domain/booking`, data);

export const deleteDomainBooking = (data) => remove(`${API_URL}/domain/booking`, data);