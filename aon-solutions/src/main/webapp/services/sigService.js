import { API_URL, SIG_SESSION_ID, SIG_URL } from "../environments/environments.js";
import { get, post, remove, requestSig } from "./request.js";


const isCau  = () => parseInt(localStorage.getItem("taskCau") || 0);

export const isSigGet = (url, data) => {
    if(data) data.cau = isCau();
    return isCau() ? getSig(`${SIG_URL}/${url}`, data) : get(`${API_URL}/${url}`, data);
}  
  
export const isSigPost = (url, data) => {
    if(data) data.cau = isCau();
    return isCau() ? postSig(`${SIG_URL}/${url}`, data) : post(`${API_URL}/${url}`, data);
} 
    
export const isSigRemove = (url, data) => {
    if(data) data.cau = isCau();
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