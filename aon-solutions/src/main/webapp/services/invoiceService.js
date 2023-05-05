import { post, get, remove, put } from "./request.js";
import { openFileUrl } from "./fileService.js";
import { API_URL } from "../environments/environments.js";

// PRINT CONFIGURATION
export const getInvoiceConfiguration = (data) =>  get(`${API_URL}/invoice/configuration`, data);
export const saveInvoiceConfiguration = (data) =>  post(`${API_URL}/invoice/configuration`, data);

export const getPrintInvoiceConfiguration = (data) =>  get(`${API_URL}/invoice/print_configuration`, data);
export const savePrintInvoiceConfiguration = (data) =>  post(`${API_URL}/invoice/print_configuration`, data);

// INVOICE
export const getSalesSeries = (data) => get(`${API_URL}/invoice/series`, data)

export const invoiceSelection = (inv) => invoice = inv;

export const getInvoice = (id) => get(`${API_URL}/invoice`, { id });

export const getInvoices = (data) => get(`${API_URL}/invoice`, data);

export const insertInvoice = (data) => post(`${API_URL}/invoice`, data);

export const acceptInvoice = (data) => put(`${API_URL}/invoice/accept`, data);
export const nullInvoice = (data) => remove(`${API_URL}/invoice/cancel`, data);

export const sendInvoiceMail = (data) => post(`${API_URL}/send_mail/invoice`, data);

export const sendInvoice2Mail = (data) => post(`${API_URL}/send_mail/invoice2`, data);

export const downloadInvoices = (data) => openFileUrl(`${API_URL}/multiple_download/invoice?json=${data}`);
export const downloadInvoiceExcel = (data) => openFileUrl(`${API_URL}/downloadInvoiceExcel?json=${data}`);

export const deleteRawdocInvoices = (invoiceIds) => remove(`${API_URL}/invoice/rawdoc`, { id: invoiceIds });
export const deleteInvoice = (data) => remove(`${API_URL}/invoice`, data);

export const getInvoiceAccounts = (data) => get(`${API_URL}/invoice/accounts`, data);

export const selfconta = (year) => post(`${API_URL}/invoice/selfconta_import`, {year});
export const recordSelfconta = (data) => post(`${API_URL}/invoice/selfconta_record`, data);

export const getPaymethods = (data) => get(`${API_URL}/paymethods`, data);
export const getPaymethod = (id) => get(`${API_URL}/paymethods/${id}`, {});

export const signInvoice = (id) => put(`${API_URL}/invoice/sign`, {id});
export const downloadFacturae = (data) => 
    openFileUrl(`${API_URL}/face?id=${data.id}&domainName=${data.domainName}&domainId=${data.domainId}&cert=${data.cert}`);
export const getTbaiHistory = (invoice) => post(`${API_URL}/tbai/history`, {invoice});