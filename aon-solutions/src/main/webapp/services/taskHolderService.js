import { get } from "./request.js";
import { API_URL } from "../environments/environments.js";


export const getTastHolders = (data) => get(`${API_URL}/taskholder/enterprise`, data);

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
