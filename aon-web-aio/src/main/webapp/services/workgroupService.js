import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getWorkgroups = (data) => get(`${API_URL}/workgroup`, data);

export const saveWorkgroup = (data) => post(`${API_URL}/workgroup`, data);

export const deleteWorkgroup = (data) => remove(`${API_URL}/workgroup`, data);

export const getWorkgroupProjectsHolders = (data) => get(`${API_URL}/workgroup/projectsHolder`, data);

export const  getWorkgroupTasks = (data) => get(`${API_URL}/workgroup/task`, data);




