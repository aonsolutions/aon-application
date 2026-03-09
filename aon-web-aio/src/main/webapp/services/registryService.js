import { get, post, put, remove, removePro, getPro, putPro } from "./request.js";
import { API_URL, PRO_TEST_URL, PRO_URL } from "../environments/environments.js";
import { openFileUrl } from "./fileService.js";

const CUSTOMERS = `${API_URL}/customers`;
const TARGETS = `${API_URL}/target`;
const CARRIERS = `${API_URL}/carriers`;
const CREDITORS = `${API_URL}/creditors`;
const SUPPLIERS = `${API_URL}/suppliers`;
const SEGMENTS = `${API_URL}/segments`;
const RELATIONSHIP = `${API_URL}/relationship`;
const RELATIONSHIP_COMPANY = `${API_URL}/relationship/company`;
const SIBLINGS_OFFICE = `${API_URL}/relationship/siblingsOffice`;
const REGISTRY_NOTES = `${API_URL}/registryNotes`;
const EMAILS = `emails`;
const TRANSACTION = `transaction`;

const SUGGESTED_ACCOUNT = `${API_URL}/registry/suggestedAccount`;

export const getRegistry = (data) => post(`${API_URL}/registry`, data);
export const saveRegistry = (data) => put(`${API_URL}/registry`, data);

export const getCustomers = (data) => post(CUSTOMERS, data);
export const getCustomer = (data) => post(`${CUSTOMERS}/${data.id}`, data);
export const getCustomerEmails = (data) => post(`${CUSTOMERS}/${data.id}/${EMAILS}`, data);
export const saveCustomer = (data) => put(`${CUSTOMERS}/${data.id}`, data);
//export const saveCustomerNote = (data) => putPro(`${PRO_URL}/${CUSTOMERS}/note`, data);
// LOCAL
export const saveCustomerNote = (data) => put(`${CUSTOMERS}/note`, data);
export const saveCustomerNotePro = (data, headers) => putPro(`${CUSTOMERS}/note`, data, headers);

export const getTarget = (data) => get(`${TARGETS}/${data.id}`, data);
export const saveTarget = (data) => post(`${TARGETS}`, data);

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

// REGISTRY RELATIONSHIP
export const getRelationShips = (data) => get(RELATIONSHIP, data);
export const getRelationShip = (data) => get(`${RELATIONSHIP}/${data.registry}`, data);
export const saveRelationShip = (data) => put(`${RELATIONSHIP}/${data.registry}`, data);
export const removeRelationShip = (data) => remove(`${RELATIONSHIP}/${data.registry}`, data);
export const getRelationShipCompany = (data) => get(RELATIONSHIP_COMPANY, data);
export const getSiblingsOffice = (data) => get(SIBLINGS_OFFICE, data);

// REGISTRY RADDINFO
export const getCustomerDomainAddInfo = (data, headers) => getPro(`${PRO_URL}/${RELATIONSHIP}/raddinfo/${data.registry}`, data, headers);
// LOCAL //export const getCustomerDomainAddInfo = (data) => getPro(`${RELATIONSHIP}/raddinfo/${data.registry}`, data);

export const removeCustomerDomainAddInfo = (data) => remove(`${RELATIONSHIP}/raddinfo/${data.registry}`, data);

export const removeAonCustomerDomain = (data, headers) => removePro(`${PRO_URL}/${RELATIONSHIP}/aonCustomer`, data, headers);
// LOCAL //export const removeAonCustomerDomain = (data, headers) => removePro(`${RELATIONSHIP}/aonCustomer`, data, headers);

// SUGGESTED ACCOUNT

export const getRegistrySuggestedAccount = (data) => get(SUGGESTED_ACCOUNT, data);

export const downloadRegistryExcel = (data) => openFileUrl(`${API_URL}/downloadRegistryExcel?json=${data}`);

// CUSTOMER NOTES

export const getRegistryNotes = (data) => get(REGISTRY_NOTES, data);



// RECORD DATA

export const saveRecordData = (data) => put(`${API_URL}/registry/recordData`, data);
export const deleteRecordData = (id) => remove(`${API_URL}/registry/recordData`, {id});