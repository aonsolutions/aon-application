import { webkitRequestMobile, actionRequestMobile } from "./request.js";


export const webkitMobile = () => webkitRequestMobile();

export const actionMobile = (data) => actionRequestMobile(data);