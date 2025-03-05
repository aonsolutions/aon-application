import { API_URL } from "../environments/environments.js";
import { post } from "./request.js";

export const invofoxLogin = (data) => post(`${API_URL}/invofox/login`, data);
