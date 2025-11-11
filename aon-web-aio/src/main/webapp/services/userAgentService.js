import { webkitRequestMobile } from "./request";

export const isMobileResolution = () => {
  // Tener en cuenta esta resolucion en el sass (menu)
  return window.matchMedia("(max-width: 768px)").matches;
}

export const isMobile = () => {
    const reg = new RegExp(/mobile/i);
    return getPlatform().match(reg) || getUserAgent().match(reg) || isAndroidApp()  || isAndroid35App() || isIosApp() || isAppMobile();
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
    return isAndroidOldApp() || isAndroid35App() || isAndroidPaturpatApp() ;
}

export const isAndroidOldApp = () => {
    return getUserAgent() === 'solutions.aon.android';
}

export const isAndroid35App = () => {
    return getUserAgent() === 'solutions.aon.android.35';
}

export const isAndroidPaturpatApp = () => {
    return getUserAgent() === 'solutions.aon.android.paturpat';
}

export const isIosApp = () => {
    return getUserAgent() === 'solutions.aon.ios';
}

export const isAppMobile = () => {
    return webkitRequestMobile();
}

export const isApp = () => {
    return isAndroidApp() || isIosApp() || isAppMobile();
}

export const getPlatform = () => {
    return navigator.userAgent.toLowerCase();
}

export const getUserAgent = () => {
    return navigator.userAgent.toLowerCase();
}