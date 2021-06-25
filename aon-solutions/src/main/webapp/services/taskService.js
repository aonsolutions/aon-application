import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getTasks = (data) => get(`${API_URL}/task`, data);

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

