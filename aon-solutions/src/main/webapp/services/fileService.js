
import { post, openFileMobile, openFileDesktop, webkitRequestMobile, actionRequestMobile, getToken, requestFile } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const uploadFile = (data) => post(`${API_URL}/file`, data);

export const getFileUrl = (data) =>{
  return new Promise((resolve, reject) => {
  requestFile("GET", '/ms/api/file', data, async(result, error) => {
    if (error) reject(error);
    else {
      const {blob, fileName} = result;
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

export const openFileTmp = async (url, data) => {
  const token = getToken();
  const domainId = localStorage.getItem("aon_domain_id");
  const domainName = localStorage.getItem("aon_domain_name");
  const datos = {
    ...data,
    domain_name: domainName,
    session_id: token,
    domain_id: domainId,
  };
  const json = btoa(JSON.stringify(datos));
  const newUrl = `${url}?json=${json}`;

  await openFileUrl(newUrl);
};
