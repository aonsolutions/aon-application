import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getWorkgroups = (data) => get(`${API_URL}/workgroup`, data);

export const saveWorkgroup = (data) => post(`${API_URL}/workgroup`, data);

export const deleteWorkgroup = (data) => remove(`${API_URL}/workgroup`, data);



export const getTaskHolderWorkGroup = (data) => {
    let domainName = localStorage.getItem("aon_domain_name");
    let user = localStorage.getItem('aon_domain_login');
    return post(`${API_URL}/work/${domainName}/${user}/workgroup`, data);
}

