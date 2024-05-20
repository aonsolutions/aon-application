import { API_URL } from "../environments/environments.js";
import { postInvofox, getInvofox, post } from "./request.js";

export const getInvofoxToken = () => postInvofox("https://prod.kinequo.com/backends/midas/auth/login-token", {user:"$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi"});

export const invofoxLogin = (data) => post(`${API_URL}/invofox/login`, data);

// export const getInvofoxDocuments = (data) => getInvofox("https://prod.kinequo.com/backends/midas/documents", data);
