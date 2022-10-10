
import { get, post, remove } from "./request.js";
import {API} from "../environments/environments.js";

export const getProjects = (data) => get(`${API.PROJECTS}`, data);
export const getProjectsHolders = (data) => get(`${API.PROJECTS}/${data.id}/holders`, data);
export const getOfficeProjects = (data) => get(`${API.PROJECTS}/office`, data);
export const saveProject = (data) => post(`${API.PROJECTS}`, data);
export const deleteProject = (data) => remove(`${API.PROJECTS}`, data);

export const getProjectTypes = (data) => get(`${API.PROJECTS}/type`, data);
export const saveProjectType = (data) => post(`${API.PROJECTS}/type`, data);
export const deleteProjectType = (data) => remove(`${API.PROJECTS}/type`, data);