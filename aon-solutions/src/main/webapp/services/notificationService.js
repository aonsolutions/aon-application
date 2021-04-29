import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const sendNotification = (data) => post(`${API_URL}/notification/send`, data);

export const getTotalNotification = (data) => get(`${API_URL}/notification/total-notification`, data);

export const getNotification = (data) =>  get(`${API_URL}/notification`, data); 

export const markReadNotification = (data) => post(`${API_URL}/notification/mark-read-notification`, data);

//remove test sendNotificationTest
export const saveNotificationTest = (data) => post(`${API_URL}/notification/save-test`, data);
