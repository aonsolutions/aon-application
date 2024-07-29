import { get, post, remove, put } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const saveTastHolder = (data) => post(`${API_URL}/taskholder`, data);

export const getTastHolders = (data) => get(`${API_URL}/taskholder/enterprise`, data);

export const getTastHoldersList = (data) => get(`${API_URL}/taskholder/list`, data);
export const getTastHoldersWithWorkgroupsList = (data) => get(`${API_URL}/taskholder/fullList`, data);

export const getTaskHolderWorkGroups = (data) => get(`${API_URL}/taskholder/taskHolderWorkgroup`, data);
export const saveTaskHolderWorkGroups = (data) => put(`${API_URL}/taskholder/taskHolderWorkgroup`, data);
export const deleteTaskHolderWorkGroup = (data) => remove(`${API_URL}/taskholder/taskHolderWorkgroup`, data);

export const getTaskHolderNoCache = (data) => post(`${API_URL}/taskholder/nocache`, data);

export const assignTaskHolderWorkgroup = (data) => put(`${API_URL}/taskholder/workgroup`, data);
export const removeTaskHolderWorkgroup = (data) => remove(`${API_URL}/taskholder/workgroup`, data);

export const getTastHoldersWorkGroup = async (data) =>{
  const resp = await get(`${API_URL}/taskholder/workgroup`, data);
  return resp.filter( (v,i)=>resp.findIndex((m) => m.id === v.id) === i );
} 

let taskHoldersUser;
export const getTaskHoldersUser = (data) => {
  const newData = data || {};
  return new Promise((resolve, reject) => {
    if (taskHoldersUser && !newData.reload) {
      resolve(taskHoldersUser);
    } else {
      get(`${API_URL}/taskholder/user`, newData)
        .then(r => {
          taskHoldersUser = r;
          resolve(taskHoldersUser);
        }).catch(e => reject(e));
    }
  });
}


let taskHolder;
export const getTaskHolder = (data) => {
  const newData = data || {};
  return new Promise((resolve, reject) => {
    if (taskHolder && !newData.reload) {
      resolve(taskHolder);
    } else {
      get(`${API_URL}/taskholder`, newData)
        .then(r => {
          taskHolder = r;
          resolve(taskHolder);
        }).catch(e => reject(e));
    }
  });
}
