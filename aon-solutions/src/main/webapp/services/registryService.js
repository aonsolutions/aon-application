import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getGlobalRegistries = (data) => get(`${API_URL}/global/registry`, data);
