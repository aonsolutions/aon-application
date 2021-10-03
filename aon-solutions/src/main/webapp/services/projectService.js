
import { get, post, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getProjects = (data) => get(`${API_URL}/project`, data);
export const getOfficeProjects = (data) => get(`${API_URL}/project/office`, data);
export const saveProject = (data) => post(`${API_URL}/project`, data);
export const deleteProject = (data) => remove(`${API_URL}/project`, data);

export const getProjectTypes = (data) => get(`${API_URL}/project/type`, data);
export const saveProjectType = (data) => post(`${API_URL}/project/type`, data);
export const deleteProjectType = (data) => remove(`${API_URL}/project/type`, data);