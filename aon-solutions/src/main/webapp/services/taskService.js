import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getTasks = (data) => get(`${API_URL}/task`, data);

export const saveTask = (data) => post(`${API_URL}/task`, data);

export const saveTaskAttach = (data) => post(`${API_URL}/task/attach`, data);

export const getTaskAttach = (data) => get(`${API_URL}/task/attach`, data);

export const getTaskWorkflow = (data) => get(`${API_URL}/task/workflow`, data);

export const saveTaskWorkflow = (data) => post(`${API_URL}/task/workflow`, data);

/**
 * Gegt messenger chat data
 * @returns 
 */
export const getTaskChat = () => new Promise(resolve => {
    resolve({
        //No data available
        status : "Not implemented" 
    });
});
