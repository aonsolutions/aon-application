import {  get } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { isSigGet, isSigPost, isSigRemove } from "./sigService.js";


//---------------------------TASK
export const getTasks = (data) =>  isSigGet("task", data);
export const getTasksOffice = (data) =>  isSigGet("task/office", data);
export const getTaskOne = (data) => isSigGet("task/one",data);
export const saveTask = (data) =>  isSigPost("task",data); 
export const deleteTask = (data) => isSigRemove("task",data); 
export const getTaskStatusCount = (data) => isSigGet("task/status/count",data);
export const getTaskCount = (data) => isSigGet("task/count",data);
export const getTaskNotice = (data) => isSigGet("task/notice",data);
export const taskHistoricSend = (data) => isSigPost(`task/historic-send`,data);
export const getCauInfo = (data) => get(`${API_URL}/task/cau`, data);

//----------------TASK WORKFLOW
export const getTaskWorkflow = (data) => isSigGet("task/workflow",data);
export const saveTaskWorkflow = (data) => isSigPost("task/workflow" ,data); 

//----------------TASK ATTACH
export const saveTaskAttach = (data) => isSigPost("task/attach",data);
export const getTaskAttach = (data) =>  isSigGet("task/attach",data);


//----------------TASK TAG
export const getTaskTag = (data) => isSigGet("task/tag",data);
export const getTaskTags = (data) => isSigGet("task/tags",data);
export const saveTaskTag = (data) => isSigPost("task/tag",data); 
export const deleteTaskTag = (data) => isSigRemove("task/tag",data); 





