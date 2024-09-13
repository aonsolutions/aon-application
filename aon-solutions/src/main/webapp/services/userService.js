import {  post, get, remove, put } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getUserList = (data) => get(`${API_URL}/user`, data);
export const getUserListSpeed = (data) => get(`${API_URL}/user/list`, data);
export const getUserRoles = (data) => get(`${API_URL}/user/roles`, data);
export const saveUser = (data) => post(`${API_URL}/user`, data);
export const saveServiceAccount = (data) => put(`${API_URL}/user/service`, data); 


export const getUser = (data) => get(`${API_URL}/user/info`, data);

export const getUserNotice = (data) => get(`${API_URL}/user/notice`, data);

export const deleteUser = (data) => remove(`${API_URL}/user`, data);

export const sendUserInfoEmail = (data) => post(`${API_URL}/user/email`, data);

export const assignUserWorkgroup = (data) => put(`${API_URL}/user/workgroup`, data);
export const removeUserWorkgroup = (data) => remove(`${API_URL}/user/workgroup`, data);

export const generateToken = (data) => open(`${API_URL}/generate_token?json=${data}`);

export const generateTokenSig = (data) => get(`${API_URL}/generate_token/sig`, data);
