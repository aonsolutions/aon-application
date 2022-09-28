import { request, post, get } from "./request.js";
import { clear } from "./service.js"
import { API_URL, TAG } from "../environments/environments.js";


let auth;

export const clearAuth = () => auth = undefined;

export const login = (data) => {
  return new Promise(async (resolve, reject) => {
    request("POST", `${API_URL}/login`, undefined, data, (token, error) => {
      if (error) {
        reject(error);
      } else {
        localStorage.setItem("aon_session_id", JSON.parse(token).session_id);
        resolve(token);
      }
    });
  });
};

export const closeSession = () => {
  localStorage.clear();
  clear();
  let module = document.querySelector(TAG.AON_MODULE);
  module.buildLogin();
};

export const rememberPassword = (email) => post(`${API_URL}/remember`, {email});

export const getManifest = (data) => get(`${API_URL}/manifest`, data);

export const getAuth = (data={}) => {
  return new Promise((resolve, reject) => {
    if (auth && !data.reload)
      resolve(auth);
    else 
      get(`${API_URL}/auth`, data).then(r => {
        auth = r;
        resolve(r);
      }).catch(e => reject(e));
  });
}


export const getAuthNoCache = (data) => get(`${API_URL}/auth`, data);

export const changePassword = (data) => post(`${API_URL}/auth/password`, data);

export const registerUser = (data) => post(`${API_URL}/register`, data);

export const insertAvatar = (data) => post(`${API_URL}/auth/avatar`, data);