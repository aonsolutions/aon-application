import { webkitRequestMobile } from "./request";

export const isMobile = () => {
    const reg = new RegExp(/mobile/i);
    return getPlatform().match(reg) || getUserAgent().match(reg) || isAndroidApp() || isAppMobile();
}

export const iOS = () => {
    const reg = new RegExp(/iphone|ipad|ipod/i);
    return getPlatform().match(reg) || getUserAgent().match(reg);
}

export const android = () => {
    const reg =  new RegExp(/android/i);
    return getPlatform().match(reg) || getUserAgent().match(reg);
}

export const blackBerry = () => {
    const reg =  new RegExp(/blackberry/i);
    return getPlatform().match(reg) || getUserAgent().match(reg);
}

export const windowsPhone = () => {
    const reg =  new RegExp(/windows phone/i);
    return getPlatform().match(reg) || getUserAgent().match(reg);
}

export const isAndroidApp = () => {
    return getUserAgent() === 'solutions.aon.android';
}

export const isAppMobile = () => {
    return webkitRequestMobile();
}

export const getPlatform = () => {
    return navigator.userAgent.toLowerCase();
}

export const getUserAgent = () => {
    return navigator.userAgent.toLowerCase();
}