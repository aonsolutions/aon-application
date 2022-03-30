import { API_URL, SIG_SESSION_ID, SIG_URL, SIG_DOMAIN_NAME, SIG_DOMAIN_ID } from "../environments/environments.js";
import { get, post, remove, requestSig } from "./request.js";


const isCau  = () => parseInt(localStorage.getItem("taskCau") || 0);

const setCauData = (data) => ({
    ...data, 
    cau:isCau(),
    domain: {
        id:SIG_DOMAIN_ID,
        name:SIG_DOMAIN_NAME
    }
});

export const isSigGet = (url, data) => {
    if(data && isCau()){
        data = setCauData(data);
    }

    return isCau() ? getSig(`${SIG_URL}/${url}`, data) : get(`${API_URL}/${url}`, data);
}  
  
export const isSigPost = (url, data) => {
    if(data && isCau()){
        data = setCauData(data);
    }
    
    return isCau() ? postSig(`${SIG_URL}/${url}`, data) : post(`${API_URL}/${url}`, data);
} 
    
export const isSigRemove = (url, data) => {
    if(data && isCau()){
        data = setCauData(data);
    }

    return isCau() ? removeSig(`${SIG_URL}/${url}`, data) : remove(`${API_URL}/${url}`, data);
} 

  
//--------------------------------------SIG REQUEST

const getSig = (url, data) =>  new Promise((resolve, reject) => {
    requestSig("GET", url, SIG_SESSION_ID, data, (result, error) => {
        try{
            if (error) reject(error);
            else resolve(JSON.parse(result));
        } catch(e){reject(e);}
    });
});

const postSig = (url, data) =>  new Promise((resolve, reject) => {
    requestSig("POST", url, SIG_SESSION_ID, data, (result, error) => {
        try{
            if (error) reject(error);
            else resolve(JSON.parse(result));
        } catch(e){reject(e);}
    });
});
//-------------------------------------- END SIG REQUEST
  

const removeSig = (url, data) => new Promise((resolve, reject) => {
    requestSig("DELETE", url, SIG_SESSION_ID, data, (result, error) => {
        try{
            if (error) reject(error);
            else resolve(JSON.parse(result));
        } catch(e){reject(e);}
    });
});