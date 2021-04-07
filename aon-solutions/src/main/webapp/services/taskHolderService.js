import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getTastHolders = (data) => get(`${API_URL}/taskholder`, data);

