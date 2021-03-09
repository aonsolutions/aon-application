import { request, post, get } from "./request.js";
import { clear } from "./service.js"
import { API_URL } from "../environments/environments.js";

export const login = (data) => {
  return new Promise(async (resolve, reject) => {
    request("POST", `${API_URL}/login`, undefined, data, (token, error) => {
      if (error) {
        reject(error);
      } else {
        localStorage.setItem("aon_session_id", JSON.parse(token).session_id);
        document.getElementById("aonLogin").style.display = "none";
        document.getElementById("aonHome").style.display = "block";
        resolve(token);
      }
    });
  });
};

export const closeSession = () => {
  localStorage.clear();
  document.getElementById("aonLogin").style.display = "block";
  document.getElementById("aonHome").style.display = "none";
  clear();
};

export const rememberPassword = (email) => post(`${API_URL}/remember`, {email});

export const getManifest = (data) => get(`${API_URL}/manifest`, data);

export const getAuth = (data) => get(`${API_URL}/auth`, data);

export const changePassword = (data) => post(`${API_URL}/auth/password`, data);

export const registerUser = (data) => post(`${API_URL}/register`, data);
