import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getHelpDatas = (data) => get(`${API_URL}/help`, data);
