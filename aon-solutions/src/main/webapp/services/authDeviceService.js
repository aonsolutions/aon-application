import { post } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const saveAuthDevice = async (data) => {
    const device_type = await getDeviceType();
    return post(`${API_URL}/auth-device/save`, {...data, device_type})
};

export const deleteAuthDevice = (data) => post(`${API_URL}/auth-device/delete`, data);

export const getDeviceType =  ()=> new Promise(resolve=>{
    let device_type = undefined;
    const match = navigator.userAgent.match(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i);
    if(match && match[0]) device_type = match[0].toLocaleUpperCase();
    resolve(device_type);
});

