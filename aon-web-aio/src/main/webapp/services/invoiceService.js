import { post, get, remove, put } from "./request.js";
import { openFileUrl } from "./fileService.js";
import { API_URL, SIG_URL } from "../environments/environments.js";
import { generateTokenSig } from "./userService.js";

// PRINT CONFIGURATION
export const getApiConfiguration = (data) =>  get(`${API_URL}/invoice/api_configuration`, data);
export const getInvoiceConfiguration = (data) =>  get(`${API_URL}/invoice/configuration`, data);
export const saveInvoiceConfiguration = (data) =>  post(`${API_URL}/invoice/configuration`, data);

export const getPrintInvoiceConfiguration = (data) =>  get(`${API_URL}/invoice/print_configuration`, data);
export const savePrintInvoiceConfiguration = (data) =>  post(`${API_URL}/invoice/print_configuration`, data);

// INVOICE COMMUNICATION
export const getInvoiceCommunicationConfig  = (data) =>  get(`${API_URL}/invoice/communication_configuration`, data);
export const updateICC = (data) =>  post(`${API_URL}/invoice/updateICC`, data);

// INVOICE
export const getSalesSeries = (data) => get(`${API_URL}/invoice/series`, data)

export const invoiceSelection = (inv) => invoice = inv;

export const getInvoice = (id) => get(`${API_URL}/invoice`, { id });
export const getRawdocCount = (data) => get(`${API_URL}/invoices/count`, data);

export const getInvoices = (data) => get(`${API_URL}/invoice`, data);
export const getInvoiceCount = (data) => get(`${API_URL}/invoice/count`, data);

export const getSigInvoices = async(data) => get(`${SIG_URL}/${API_URL}/invoice`, data, await generateTokenSig({}));

export const insertInvoice = (data) => post(`${API_URL}/invoice`, data);

export const acceptInvoice = (data) => put(`${API_URL}/invoice/accept`, data);
export const rectifyInvoice = (data) => put(`${API_URL}/invoice/rectify`, data);

export const sendInvoiceMail = (data) => post(`${API_URL}/send_mail/invoice`, data);
export const sendInvoice2Mail = (data) => post(`${API_URL}/send_mail/invoice2`, data);
export const sendInvoiceRejectMail = (data) => post(`${API_URL}/send_mail/invoiceReject`, data);

export const downloadInvoices = (data) => openFileUrl(`${API_URL}/multiple_download/invoice?json=${data}`);
export const downloadInvoiceExcel = (data) => openFileUrl(`${API_URL}/downloadInvoiceExcel?json=${data}`);

export const deleteRawdocInvoices = (invoiceIds) => remove(`${API_URL}/invoice/rawdoc`, { id: invoiceIds });
export const deleteInvoice = (data) => remove(`${API_URL}/invoice`, data);

export const getInvoiceAccounts = (data) => get(`${API_URL}/invoice/accounts`, data);

export const selfconta = (year) => post(`${API_URL}/invoice/selfconta_import`, {year});
export const recordSelfconta = (data) => post(`${API_URL}/invoice/selfconta_record`, data);
export const recordInvoices = (data) => post(`${API_URL}/invoices/record`, data);

export const getPaymethods = (data) => get(`${API_URL}/paymethods`, data);
export const getPaymethod = (id) => get(`${API_URL}/paymethods/${id}`, {});

export const signInvoice = (data) => put(`${API_URL}/invoice/sign`, data);
export const downloadFacturae = (data) => 
    openFileUrl(`${API_URL}/face?id=${data.id}&domainName=${data.domainName}&domainId=${data.domainId}&domainLogin=${data.domainLogin}&cert=${data.cert}&period=${data.period}&legalLiterals=${data.legalLiterals}`);
export const getTbaiHistory = (invoice) => post(`${API_URL}/tbai/history`, {invoice});
export const getCommunicationHistory = (invoice) => post(`${API_URL}/communication/history`, {invoice});

export const getInvofoxTextContent = (id) => get(`${API_URL}/invofox/text_content`, {id: id});
export const getInvofoxConfiguration = (data) => get(`${API_URL}/invofox/configuration`, data);
export const saveInvofoxConfiguration = (data) => put(`${API_URL}/invofox/configuration`, data);

export const getChargePayments = (data) => get(`${API_URL}/charge_payments`, data);


export const invoiceDuplicateFix = (data) => post(`${API_URL}/invoices/invoiceduplicatefix`, data);

export const refreshProcessing = (data) => post(`${API_URL}/invofox/refresh_processing`, data);

export const getInvoiceClosing = (data) => get(`${API_URL}/invoiceClosing`, data);
export const saveInvoiceClosing = (data) => post(`${API_URL}/invoiceClosing`, data);

export const getTrailData = (data) => get(`${API_URL}/trial`, data);
export const getChartInvoices = (data) => get(`${API_URL}/invoices/chart`, data);
export const getChartInvoicesPeriod = (data) => get(`${API_URL}/invoices/chart/period`, data);
export const getBidoqToOCR = (data) => get(`${API_URL}/s3/bidoq_ocr`, data);
export const getBidoqToOCRCount = (data) => get(`${API_URL}/s3/bidoq_ocr_count`, data);

export const fixInvoice = (data) => post(`${API_URL}/invoice/fix`, data);

// COMMUNICATE INVOICE  
export const communicateInvoice = (data) => post(`${API_URL}/invoices/communicate`, data);