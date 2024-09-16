import { API_URL, SIG_URL, SIG_DOMAIN_NAME, SIG_DOMAIN_ID } from "../environments/environments.js";
import { get, post, remove } from "./request.js";
import { generateTokenSig } from "./userService.js";


const isCau  = () => parseInt(localStorage.getItem("taskCau") || 0);

const setCauData = (data) => {
    return {
        ...data, 
        cau:isCau(),
        domain: {
            id:SIG_DOMAIN_ID,
            name:SIG_DOMAIN_NAME
        }
    }
}

export const isSigGet = async(url, data) => {
    if(data && isCau()){
        data = {
            ...data, 
            cau:isCau(),
            domainId: SIG_DOMAIN_ID,
            domainName: SIG_DOMAIN_NAME
        }   
    } 
    return isCau() 
        ? get(`${SIG_URL}/${API_URL}/${url}`, data, await generateTokenSig({})) 
        : get(`${API_URL}/${url}`, data);
}  
  
export const isSigPost = async(url, data) => {
    if(data && isCau()){
        data = setCauData(data);
    }
    
    return isCau() 
        ? post(`${SIG_URL}/${API_URL}/${url}`, data, await generateTokenSig({})) 
        : post(`${API_URL}/${url}`, data);
} 
    
export const isSigRemove = async(url, data) => {
    if(data && isCau()){
        data = setCauData(data);
    }

    return isCau() 
        ? remove(`${SIG_URL}/${API_URL}/${url}`, data, await generateTokenSig({})) 
        : remove(`${API_URL}/${url}`, data);
} 