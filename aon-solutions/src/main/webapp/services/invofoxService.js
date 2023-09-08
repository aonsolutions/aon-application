import { postInvofox, getInvofox } from "./request.js";

export const getInvofoxToken = () => postInvofox("https://prod.kinequo.com/backends/midas/auth/login-token", {user:"$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi"});


// export const getInvofoxDocuments = (data) => getInvofox("https://prod.kinequo.com/backends/midas/documents", data);
