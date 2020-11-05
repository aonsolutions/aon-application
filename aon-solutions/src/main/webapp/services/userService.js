import {  post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getUsers = (data) => get(`${API_URL}/user`, data);

export const setUser = (data) => post(`${API_URL}/user`, data);


export const getUserAppRole = (data) => get(`${API_URL}/user/app`, data);

export const setUserAppRole = (data) => post(`${API_URL}/user/app`, data);