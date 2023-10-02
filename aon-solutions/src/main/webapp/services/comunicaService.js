import { post, get, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { regimeType } from "./regimeType.js";
import { bajaType } from "./bajaType.js";
import { contractType } from "./contractType.js";

export const getWorkplaceCCCs = () => get(`${API_URL}/workplace_ccc`);

export const getMovements = (data) =>  get(`${API_URL}/comunica/movements`, data);

export const getMovementsCccs = (data) => get(`${API_URL}/comunica/movements-cccs`, data);

export const getIpfxnaf = (data) => get(`${API_URL}/comunica/ipfxnaf`, data);

export const getNafxipf = (data) => get(`${API_URL}/comunica/nafxipf`, data);

export const getAppParamComunica = (data) => get(`${API_URL}/comunica/app-param`, data);

export const getEmployee = (data) => get(`${API_URL}/comunica/get-employee`, data);

export const getRlce = (data) => get(`${API_URL}/comunica/rlce`, data);

export const getOccupation = (data) => get(`${API_URL}/comunica/occupation`, data);

export const getQuoteGroup = (data) => get(`${API_URL}/comunica/quote-group`, data);

export const getCno = (data) => get(`${API_URL}/comunica/cno`, data);

export const sendAlta = (data) => post(`${API_URL}/comunica/alta-directa`, data);

export const sendBaja = (data) => post(`${API_URL}/comunica/baja`, data);
  
export const updateContract = (data) => post(`${API_URL}/comunica/update-contrato`, data);

export const movDelete = (data) => post(`${API_URL}/comunica/delete-mov`, data); 

export const getTA = (data) => openFile(`${API_URL}/comunica/get-ta`, data);

export const getIDC = (data) => openFile(`${API_URL}/comunica/get-idc`, data);

export const getCertCorriente = (data) => openFile(`${API_URL}/comunica/cert-corriente`, data);

export const getIdcCcc = (data) => openFile(`${API_URL}/comunica/get-idc-ccc`, data);

export const getReportAffiliateInAlta = (data) => openFile(`${API_URL}/comunica/get-report-affiliate-in-alta`, data);

export const getReportAffiliateInMovPrev = (data) => openFile(`${API_URL}/comunica/get-report-affiliate-in-mov-prev`, data);

export const getContractSepe = (data) => openFile(`${API_URL}/comunica/get-contract-sepe`, data);

export const getCopyBasicSepe = (data) => openFile(`${API_URL}/comunica/get-copy-basic`, data);

export const getAllTipoCtz = () => Promise.resolve( regimeType ); 

export const getCodBaja = () => Promise.resolve( bajaType );  

export const getContractType = () => Promise.resolve( contractType ); 

export const getQuoteType = (data) => new Promise(async (resolve) => {
  const json = await getAllTipoCtz();
  resolve(json.find((r) => r.value == data));
});

export const getJourneyType = () =>[
  { id: 1, name: "Semanal", value: "semanal"},
  { id: 2, name: "Diaria", value: "diaria"}
];

export const getWorkersCollective = () => Promise.resolve([
  { name: "Producción", value: "967"},
  { name: "Previsibles", value: "968"}
]);
