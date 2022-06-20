import { post, put, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getWorkplaces = (data) => post(`${API_URL}/workplace`, data);

export const saveWorkplace = (data) => put(`${API_URL}/workplace`, data);

export const deleteWorkplace = (data) => remove(`${API_URL}/workplace`, data);




