
import { post, openFileMobile, openFileDesktop, webkitRequestMobile, actionRequestMobile, requestFile } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const uploadFile = (data) => post(`${API_URL}/file`, data);

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

export const openFileUrl = async (url) => {
  if (webkitRequestMobile())
    await openFileMobile(url)
      .then(async (obj) => await actionRequestMobile(obj))
      .catch((e) => null);
  else openFileDesktop(url);
  return;
};