import { get, post, put } from "./request.js";
import { API_URL } from "../environments/environments.js";

const CUSTOMERS = `${API_URL}/customers`;
const SEGMENTS = `${API_URL}/segments`;
const EMAILS = `emails`;

export const getRegistry = (data) => post(`${API_URL}/registry`, data);
export const saveRegistry = (data) => put(`${API_URL}/registry`, data);

export const getCustomers = (data) => post(CUSTOMERS, data);
export const getCustomer = (data) => post(`${CUSTOMERS}/${data.id}`, data);
export const getCustomerEmails = (data) => post(`${CUSTOMERS}/${data.id}/${EMAILS}`, data);
export const saveCustomer = (data) => put(`${CUSTOMERS}/${data.id}`, data);

export const getSegments = (data) => get(SEGMENTS, data);

export const getCreditor = (data) => post(`${API_URL}/creditor`, data);
export const saveCreditor = (data) => put(`${API_URL}/creditor`, data);

export const getSupplier = (data) => post(`${API_URL}/supplier`, data);
export const saveSupplier = (data) => put(`${API_URL}/supplier`, data);

export const getRegistries = (data) => get(`${API_URL}/suggestion/registry`, data);

export const getRegistryAddress = (data) => get(`${API_URL}/registry/address`, data);

export const getRegistryBanks = (id) => get(`${API_URL}/registry/banks`, {id});

export const getRegistryPaymethod = (data) => get(`${API_URL}/registry/paymethod`, data);

export const getGlobalRegistries = (data) => get(`${API_URL}/global/registry`, data);
