
import { post } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const uploadFile = (data) => post(`${API_URL}/file`, data);