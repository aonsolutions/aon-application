import { post, get, remove } from "./request.js";
import { openFileUrl } from "./fileService.js";
import { API_URL } from "../environments/environments.js";


// INVOICE
export const invoiceSelection = (inv) => invoice = inv;

export const getInvoice = (id) => get(`${API_URL}/invoice`, { id });

export const getInvoices = (data) => get(`${API_URL}/invoice`, data);

export const insertInvoice = (data) => post(`${API_URL}/invoice`, data);

export const sendInvoiceMail = (data) => post(`${API_URL}/send_mail/invoice`, data);

export const downloadInvoices = (data) => openFileUrl(`${API_URL}/multiple_download/invoice?json=${data}`);

export const deleteInvoices = (invoiceIds) => remove(`${API_URL}/invoice`, { id: invoiceIds });

export const getInvoiceAccounts = (data) => get(`${API_URL}/invoice/accounts`, data);

export const selfconta = () => post(`${API_URL}/invoice/selfconta_import`, {});
export const recordSelfconta = (data) => post(`${API_URL}/invoice/selfconta_record`, data);
