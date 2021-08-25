import { post, get, remove, requestSig } from "./request.js";
import { API_URL, SIG_URL } from "../environments/environments.js";

// SIG_URL

export const getTasks = (data) =>  isSigGet(data, "task");

export const getTaskOne = (data) => isSigGet(data, "task/one");

export const saveTask = (data) =>  isSigPost(data, "task"); 

export const deleteTask = (data) => remove(`${API_URL}/task`, data);

export const saveTaskAttach = (data) => isSigPost(data, "task/attach");

export const getTaskAttach = (data) =>  isSigGet(data, "task/attach");

export const getTaskWorkflow = (data) => isSigGet(data, "task/workflow");

export const saveTaskWorkflow = (data) => isSigPost(data, "task/workflow"); 

export const getTaskStatusCount = (data) => isSigGet(data, "task/count-status-task");

export const getCauInfo = (data) => get(`${API_URL}/task/cau`, data);


const isSigGet = (data, url) => {
  const isCau = parseInt(localStorage.getItem("taskCau"));
  data.cau = isCau;
  return isCau ?  getSig(`${SIG_URL}/${url}`, data) : get(`${API_URL}/${url}`, data);
}  

const isSigPost = (data, url) => {
  const isCau = parseInt(localStorage.getItem("taskCau"));
  data.cau = isCau;
  return isCau ? postSig(`${SIG_URL}/${url}`, data) : post(`${API_URL}/${url}`, data);
} 



//--------------------------------------SIG REQUEST
const SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";

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