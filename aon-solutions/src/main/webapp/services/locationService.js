import { post, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const saveLocation = (data) => post(`${API_URL}/location`, data);
export const deleteLocation = (data) => remove(`${API_URL}/location`, data);
export const getLocation = (data) => get(`${API_URL}/location`, data);


//remove test sendNotificationTest
export const sendNotificationTest = () =>{
    const tokenFCM = window.tokenFCM;
    if(tokenFCM){
        return get(`${API_URL}/location/notification-test`, {tokenFCM});
    }
    console.log("tokenFCM", tokenFCM);
}