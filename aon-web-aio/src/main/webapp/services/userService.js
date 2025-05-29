import {  post, get, remove, put } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getUserList = (data, sessionData) => get(`${API_URL}/user`, data, sessionData);
export const getUserListSpeed = (data, sessionData) => get(`${API_URL}/user/list`, data, sessionData);
export const getUserRoles = (data, sessionData) => get(`${API_URL}/user/roles`, data, sessionData);
export const saveUser = (data, sessionData) => post(`${API_URL}/user`, data, sessionData);
export const saveServiceAccount = (data, sessionData) => put(`${API_URL}/user/service`, data, sessionData); 


export const getUser = (data, sessionData) => get(`${API_URL}/user/info`, data, sessionData);

export const getUserNotice = (data, sessionData) => get(`${API_URL}/user/notice`, data, sessionData);

export const deleteUser = (data, sessionData) => remove(`${API_URL}/user`, data, sessionData);

export const sendUserInfoEmail = (data, sessionData) => post(`${API_URL}/user/email`, data, sessionData);

export const assignUserWorkgroup = (data, sessionData) => put(`${API_URL}/user/workgroup`, data, sessionData);
export const removeUserWorkgroup = (data, sessionData) => remove(`${API_URL}/user/workgroup`, data, sessionData);

export const generateToken = (data) => open(`${API_URL}/generate_token?json=${data}`);

export const generateTokenSig = (data) => get(`${API_URL}/generate_token/sig`, data);
