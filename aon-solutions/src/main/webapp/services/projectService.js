
import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getOfficeProjects = (data) => get(`${API_URL}/project/office`, data);

export const getProjectsByRegistry = (data) => get(`${API_URL}/project/registry`, data);