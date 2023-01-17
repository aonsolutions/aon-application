
import { post, openFileMobile, openFileDesktop, webkitRequestMobile, sendActionMobile, requestFile, get, remove } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const uploadFile = (data) => post(`${API_URL}/file`, data);

export const getAttach = (data) => get(`${API_URL}/attach`, data);
export const uploadAttach = (data) => post(`${API_URL}/attach`, data);
export const deleteAttach = (data) => remove(`${API_URL}/attach`, data);


export const getFileUrl = (data) =>{
  return new Promise((resolve, reject) => {
  requestFile("GET", '/ms/api/file', data, async(result, error) => {
    if (error) reject(error);
    else {
      const {blob} = result;
      resolve(URL.createObjectURL(blob));
    }
  });
});
}

/**
 * 
 * @param {String} url 
 * @param {String or null} contentType 
 * @returns 
 */
export const openFileUrl = async (url, contentType=null) => {
  try {
    if (webkitRequestMobile()){
      await openFileMobile(url, contentType).then(async (obj) => await sendActionMobile(obj));
    } else {
      openFileDesktop(url);
    }
  } catch (error) {
    alert(error);
  }
  return;
};
