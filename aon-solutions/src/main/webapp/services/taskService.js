import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getTasks = (data) => get(`${API_URL}/task`, data);

export const saveTask = (data) => post(`${API_URL}/task`, data);

export const deleteTask = (data) => remove(`${API_URL}/task`, data);

export const saveTaskAttach = (data) => post(`${API_URL}/task/attach`, data);

export const getTaskAttach = (data) => get(`${API_URL}/task/attach`, data);

export const getTaskWorkflow = (data) => get(`${API_URL}/task/workflow`, data);

export const saveTaskWorkflow = (data) => post(`${API_URL}/task/workflow`, data);

export const getTaskStatusCount = (data) => get(`${API_URL}/task/count-status-task`, data);

export const getCauInfo = (data) => get(`${API_URL}/task/cau`, data);

