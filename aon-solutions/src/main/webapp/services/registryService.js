import { get, post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

const CUSTOMERS = `${API_URL}/customers`;
const CARRIERS = `${API_URL}/carriers`;
const CREDITORS = `${API_URL}/creditors`;
const SUPPLIERS = `${API_URL}/suppliers`;
const SEGMENTS = `${API_URL}/segments`;
const RELATIONSHIP = `${API_URL}/relationship`;
const EMAILS = `emails`;
const TRANSACTION = `transaction`;

const SUGGESTED_ACCOUNT = `${API_URL}/registry/suggestedAccount`;

export const getRegistry = (data) => post(`${API_URL}/registry`, data);
export const saveRegistry = (data) => put(`${API_URL}/registry`, data);

export const getCustomers = (data) => post(CUSTOMERS, data);
export const getCustomer = (data) => post(`${CUSTOMERS}/${data.id}`, data);
export const getCustomerEmails = (data) => post(`${CUSTOMERS}/${data.id}/${EMAILS}`, data);
export const saveCustomer = (data) => put(`${CUSTOMERS}/${data.id}`, data);

export const getSegments = (data) => get(SEGMENTS, data);

export const getCreditor = (data) => post(`${API_URL}/creditor`, data);
export const getCreditorTransaction = (data) => get(`${CREDITORS}/${data.id}/${TRANSACTION}`, data);
export const saveCreditor = (data) => put(`${API_URL}/creditor`, data);

export const getSupplier = (data) => post(`${API_URL}/supplier`, data);
export const getSupplierTransaction = (data) => get(`${SUPPLIERS}/${data.id}/${TRANSACTION}`, data);
export const saveSupplier = (data) => put(`${API_URL}/supplier`, data);

export const getCarriers = (data) => get(CARRIERS, data);
export const getCarrier = (data) => get(`${CARRIERS}/${data.id}`, data);
export const getCarrierEmails = (data) => post(`${CARRIERS}/${data.id}/${EMAILS}`, data);
export const saveCarrier = (data) => put(`${CARRIERS}/${data.id}`, data);

export const getRegistries = (data) => get(`${API_URL}/suggestion/registry`, data);

export const getRegistryAddress = (data) => get(`${API_URL}/registry/address`, data);

export const getRegistryBanks = (id) => get(`${API_URL}/registry/banks`, {id});

export const getRegistryPaymethod = (data) => get(`${API_URL}/registry/paymethod`, data);

export const getGlobalRegistries = (data) => get(`${API_URL}/global/registry`, data);

// REGISTRY RELATIONSHIP
export const getRelationShips = (data) => get(RELATIONSHIP, data);
export const getRelationShip = (data) => get(`${RELATIONSHIP}/${data.registry}`, data);
export const saveRelationShip = (data) => put(`${RELATIONSHIP}/${data.registry}`, data);
export const removeRelationShip = (data) => remove(`${RELATIONSHIP}/${data.registry}`, data);

// SUGGESTED ACCOUNT

export const getRegistrySuggestedAccount = (data) => get(SUGGESTED_ACCOUNT, data)