import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const sendNotification = (data) => post(`${API_URL}/notification/send`, data);

//remove test sendNotificationTest
export const sendNotificationTest = () =>{
    const tokenFCM = window.tokenFCM;
    if(tokenFCM){
        return get(`${API_URL}/notification/test`, {tokenFCM});
    }
    console.log("tokenFCM", tokenFCM);
}