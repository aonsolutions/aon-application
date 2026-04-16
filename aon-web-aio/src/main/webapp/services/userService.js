import {  post, get, remove, put, getPro } from "./request.js";
import { API, API_URL, PRO_URL } from "../environments/environments.js";

export const getUserList = (data, sessionData) => get(`${API.USER}`, data, sessionData);
export const getUserListSpeed = (data, sessionData) => get(`${API.USER_LIST}`, data, sessionData);

export const getSigUserListSpeed = (data, sessionData) => getPro(`${PRO_URL}/${API.USER_LIST}`, data, sessionData);
// LOCAL //export const getSigUserListSpeed = (data, sessionData) => getPro(`${API.USER_LIST}`, data, sessionData);

export const saveUser = (data, sessionData) => post(`${API.USER}`, data, sessionData);
export const saveServiceAccount = (data, sessionData) => put(`${API.USER_SERVICE}`, data, sessionData); 

export const getUserRoles = (data, sessionData) => get(`${API.USER_ROLES}`, data, sessionData);
export const getUserDomainUserRoles = (data, sessionData) => get(API.USER_APPROLES, data, sessionData);

export const getUser = (data, sessionData) => get(`${API_URL}/user/info`, data, sessionData);

export const getUserNotice = (data, sessionData) => get(`${API_URL}/user/notice`, data, sessionData);

export const getUserEmail = (data, sessionData) => get(`${API_URL}/user/email`, data, sessionData);

export const deleteUser = (data, sessionData) => remove(`${API_URL}/user`, data, sessionData);

export const addUserScopes = (data, sessionData) => put(`${API_URL}/user/scope`, data, sessionData);
export const deleteUserScope = (data, sessionData) => remove(`${API_URL}/user/scope`, data, sessionData);

export const sendUserInfoEmail = (data, sessionData) => post(`${API_URL}/user/email`, data, sessionData);

export const assignUserWorkgroup = (data, sessionData) => put(`${API_URL}/user/workgroup`, data, sessionData);
export const removeUserWorkgroup = (data, sessionData) => remove(`${API_URL}/user/workgroup`, data, sessionData);

export const generateToken = (data) => open(`${API_URL}/generate_token?json=${data}`);

export const generateTokenJson = (data, sessionData) => get(`${API_URL}/generate_token/json`, data, sessionData);

export const generateTokenSig = (data) => get(`${API_URL}/generate_token/sig`, data);

export const assignUserAuth = (data, sessionData) => put(`${API_URL}/user/auth`, data, sessionData);
