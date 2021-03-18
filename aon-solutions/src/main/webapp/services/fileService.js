
import { post, openFileMobile, openFileDesktop, webkitRequestMobile, actionRequestMobile } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const uploadFile = (data) => post(`${API_URL}/file`, data);

export const openFileUrl = async (url) => {
  if (webkitRequestMobile())
    await openFileMobile(url)
      .then(async (obj) => await actionRequestMobile(obj))
      .catch((e) => null);
  else openFileDesktop(url);
  return;
};
