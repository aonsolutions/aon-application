import { post, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { isSigPost } from "./sigService.js";

export const saveApplicationParameter = (data) => post(`${API_URL}/application-parameter`, data);

export const deleteApplicationParameter = (data) => remove(`${API_URL}/application-parameter`, data);

export const getApplicationParameters = (data) => post(`${API_URL}/application-parameter/all`, data);

export const getApplicationParametersIsSig = (data) => isSigPost(`application-parameter/all`, data);