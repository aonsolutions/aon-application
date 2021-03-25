
import { get, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getContracts = (data) => get(`${API_URL}/contract`, data);

export const getContratoPdf = (data) => openFile(`${API_URL}/comunica/pdf/get-contrato`, data);
export const getCopyBasicPdf = (data) => openFile(`${API_URL}/comunica/pdf/get-copy-basic`, data);

export const getCompanyCosts = (data) => get(`${API_URL}/contract/company/costs`, data);

export const getCompanyCostsExcel = (data) => openFile(`${API_URL}/contract/company/costs/excel`, data);