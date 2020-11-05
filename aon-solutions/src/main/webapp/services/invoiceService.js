import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";


// INVOICE
export const invoiceSelection = (inv) => invoice = inv;

export const getInvoice = (id) => get(`${API_URL}/invoice`, { id });

export const getInvoices = (data) => get(`${API_URL}/invoice`, data);

export const insertInvoice = (data) => post(`${API_URL}/invoice`, data);

export const deleteInvoices = (invoiceIds) => remove(`${API_URL}/invoice`, { id: invoiceIds });