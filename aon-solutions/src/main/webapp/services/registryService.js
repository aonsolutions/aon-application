import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";

let customers;
let domain;

export const clearRegistryService = () => {
  clearCustomers();
  clearDomain();
}

export const clearCustomers = () => customers = undefined;
export const clearDomain = () => domain = undefined;

export const getRegistries = (data) => get(`${API_URL}/suggestion/registry`, data);

export const getRegistryAddress = (data) => get(`${API_URL}/registry/address`, data);

export const getCustomers = (data) => {
  data = data || {};
  return new Promise((resolve, reject) => {
    if (customers && !data.reload && domain && domain === localStorage.getItem('aon_domain_id')) {
      resolve(customers);
    } else {
      get(`${API_URL}/customer`, data)
        .then(r => {
          domain = localStorage.getItem('aon_domain_id');
          customers = r;
          resolve(customers);
        }).catch(e => reject(e));
    }
  });
}

export const getGlobalRegistries = (data) => get(`${API_URL}/global/registry`, data);
