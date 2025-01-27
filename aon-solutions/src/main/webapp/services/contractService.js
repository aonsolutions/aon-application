
import { get, openFile, post } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getContracts = (data) => get(`${API_URL}/contract`, data);

export const addContract = (data) => post(`${API_URL}/contract/add`, data);

export const getCompanyCosts = (data) => get(`${API_URL}/contract/company/costs`, data);

export const getCompanyCostsExcel = (data) => openFile(`${API_URL}/contract/company/costs/excel`, data);

export const getCccForActivity = (data) => get(`${API_URL}/contract/ccc/activity`, data);

export const saveVacation = (data) => post(`${API_URL}/contract/save/vacation`, data);

export const getConvenios = async (data) => {
    let convenios = [
        { id: "1", name: "- Sin convenio definido", value: "60888888888888" },
    ];

    let resp = await get(`${API_URL}/contract/agreements`, data);
    resp.map(c =>  convenios.push({ id: c.ssNumber, name: `${c.ssNumber} - ${c.description}`, value: c.ssNumber }) );

    return convenios;
}

export const getAllContracts = (data) => get(`${API_URL}/contract-api/all`, data );