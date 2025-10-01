import { get, getCustomFile } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getCustomViewConfiguration = () => get(`${API_URL}/customview`);
export const getCustomViewImage = (url) => { return new Promise((resolve) => {resolve(`${API_URL}/${url}`);});};
