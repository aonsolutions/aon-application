import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getCustomViewConfiguration = () => get(`${API_URL}/customview`);
export const getCustomViewImage = (url) => get(`${API_URL}/${url}`);
