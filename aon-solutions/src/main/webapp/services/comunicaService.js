import { post, get, openFile, requestJsonAsset } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getWorkplaceCCCs = () => get(`${API_URL}/workplace_ccc`);

export const getMovements = (data) =>  get(`${API_URL}/comunica/movements`, data);

export const getIpfxnaf = (data) => get(`${API_URL}/comunica/ipfxnaf`, data);

export const getNafxipf = (data) => get(`${API_URL}/comunica/nafxipf`, data);

export const getTA = (data) => openFile(`${API_URL}/comunica/pdf/get-ta`, data);

export const getReportAffiliateInAlta = (data) => openFile(`${API_URL}/comunica/pdf/get-report-affiliate-in-alta`, data);

export const getReportAffiliateInMovPrev = (data) => openFile(`${API_URL}/comunica/pdf/get-report-affiliate-in-mov-prev`, data);

export const getIdcCcc = (data) => openFile(`${API_URL}/comunica/pdf/get-idc-ccc`, data);

export const getIDC = (data) =>
  openFile(`${API_URL}/comunica/pdf/get-idc`, data);

export const getCertCorriente = (data) =>
  openFile(`${API_URL}/comunica/pdf/cert-corriente`, data);


export const postAltaDirecta = (data) =>
  post(`${API_URL}/comunica/alta-directa`, data); //ALTA DIRECTA

export const postBaja = (data) =>
post(`${API_URL}/comunica/baja`, data); //BAJA
  
export const postUpdateCto = (data) =>
  post(`${API_URL}/comunica/update-contrato`, data); //ALTA DIRECTA
export const postDeleteMov = (data) =>
  post(`${API_URL}/comunica/delete-mov`, data); //DELETE MOV

export const getEmployee = (data) =>
  get(`${API_URL}/comunica/get-employee`, data);

export const getTipoContrato = () => requestJsonAsset("type_contract.json");
export const getTipoJornada = () => requestJsonAsset("type_jornada.json");

export const getGrupoCotizacion = () => requestJsonAsset("group_ctz.json"); 

export const getOcupacion = () => requestJsonAsset("occupation.json");

export const getConvenios = () => Promise.resolve([
  { id: "1", name: "- Sin convenio definido", value: "60888888888888" },
]);

export const getAllTipoCtz = () => requestJsonAsset("type_ctz.json"); 

export const getTipoCtz = (data) =>
  new Promise(async (resolve) => {
    const json = await getAllTipoCtz();
    resolve(json.find((r) => r.value == data));
  });