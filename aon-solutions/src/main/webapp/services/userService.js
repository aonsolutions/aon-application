import {  post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getUsers = (data) => get(`${API_URL}/user`, data);

export const setUser = (data) => post(`${API_URL}/user`, data);

export const getUser = (data) => get(`${API_URL}/user/info`, data);

export const getUserAppRole = (data) => get(`${API_URL}/user/app`, data);

export const setUserAppRole = (data) => post(`${API_URL}/user/app`, data);

export const getUserNotice = (data) => get(`${API_URL}/user/notice`, data);

export const deleteUser = (data) => remove(`${API_URL}/user`, data);
