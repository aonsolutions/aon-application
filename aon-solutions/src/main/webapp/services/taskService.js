import {  get } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { isSigGet, isSigPost, isSigRemove } from "./sigService.js";


//---------------------------TASK
export const getTasks = (data) =>  isSigGet(data, "task");
export const getTaskOne = (data) => isSigGet(data, "task/one");
export const saveTask = (data) =>  isSigPost(data, "task"); 
export const deleteTask = (data) => isSigRemove(data, "task"); 
export const saveTaskAttach = (data) => isSigPost(data, "task/attach");
export const getTaskAttach = (data) =>  isSigGet(data, "task/attach");
export const getTaskStatusCount = (data) => isSigGet(data, "task/status/count");
export const getTaskCount = (data) => isSigGet(data, "task/count");
export const getTaskNotice = (data) => isSigGet(data, "task/notice");
export const getCauInfo = (data) => get(`${API_URL}/task/cau`, data);

//----------------TASK WORKFLOW
export const getTaskWorkflow = (data) => isSigGet(data, "task/workflow");
export const saveTaskWorkflow = (data) => isSigPost(data, "task/workflow"); 


//----------------TASK TAG
export const getTaskTag = (data) => isSigGet(data, "task/tag");
export const getTaskTags = (data) => isSigGet(data, "task/tags");
export const saveTaskTag = (data) => isSigPost(data, "task/tag"); 
export const deleteTaskTag = (data) => isSigRemove(data, "task/tag"); 





