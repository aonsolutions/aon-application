import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const saveLocation = (data) => post(`${API_URL}/location/save`, data);
export const deleteLocation = (data) => post(`${API_URL}/location/delete`, data);
export const getLocation = (data) => get(`${API_URL}/location/list`, data);


//remove test sendNotificationTest
export const sendNotificationTest = () =>{
    const tokenFCM = window.tokenFCM;
    if(tokenFCM){
        get(`${API_URL}/location/notification-test`, {tokenFCM});
    }
    console.log("tokenFCM", tokenFCM);
}