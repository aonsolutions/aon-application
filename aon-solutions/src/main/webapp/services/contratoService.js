
import { get, openPDF } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getContracts = (data) => get(`${API_URL}/contract`, data);

export const getContratoPdf = (data) => openPDF(`${API_URL}/comunica/pdf/get-contrato/sepe`, data);
export const getCopyBasicPdf = (data) => openPDF(`${API_URL}/comunica/pdf/get-copy-basic/sepe`, data);