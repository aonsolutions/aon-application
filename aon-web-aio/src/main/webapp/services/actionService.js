import * as UA from './userAgentService.js';
import { mobileAction } from './mobileService.js';


export const openCamera = (ionicData, callback) => {
    window.receiveImage = callback;
    let data = {action: 'openCamera'};
    if(UA.isAndroidApp()) {
        window.Android.openCamera(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {
		mobileAction(ionicData);
    }
}

export const getPositionMobile = (ionicData, callback) => {
    window.receivePosition = callback;
    let data = {action: 'getPosition'};
    if(UA.isAndroidApp()) {
        window.Android.getPosition(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {
		mobileAction(ionicData);
    }
}

export const openBarcode = (callback) => {
    window.receiveBarcode = callback;
    let data = {action: 'openBarcode'};
    if(UA.isAndroidApp()) {
        window.Android.openBarcode(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {

    }
}

export const printFile = (file) => {
    let data = {action: 'printFile', url: file.url, title: file.title};
    if(UA.isAndroidApp()) {
        window.Android.printFile(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {

    }
}

export const openFileApp = (file) => {
    let data = {action: 'openFile', url: file.url, title: file.title, content: file.content, mimeType: file.mimeType};
    if(UA.isAndroidApp()) {
        window.Android.openFile(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {

    }
}

const VAR_COLOR = /^var\(\s*(--[\w-]+)\s*(?:,\s*(.+))?\)$/;
const HEX_COLOR = /^#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})$/i;

const toHex = (value) => Number(value).toString(16).padStart(2, '0');

// Color.parseColor does not understand the #rgb shorthand
const expandHex = (hex) => hex.length === 4
    ? '#' + hex[1] + hex[1] + hex[2] + hex[2] + hex[3] + hex[3]
    : hex;

/**
 * The native apps parse the status bar color with Color.parseColor, which only
 * understands literal colors. A CSS variable such as var(--aonHeaderBackgroundColor)
 * has to be resolved to a #rrggbb value before sending it to them.
 */
export const resolveColor = (color, fallback) => {
    fallback = fallback || '#000000';
    let value = (color || '').toString().trim();
    if(HEX_COLOR.test(value)) return expandHex(value);

    let variable = VAR_COLOR.exec(value);
    for(let i = 0; variable && i < 10; i++) {
        let computed = getComputedStyle(document.documentElement).getPropertyValue(variable[1]).trim();
        value = computed || (variable[2] || '').trim();
        if(!value) return fallback;
        if(HEX_COLOR.test(value)) return expandHex(value);
        variable = VAR_COLOR.exec(value);
    }
    if(variable) return fallback;

    // color keyword or rgb()/hsl() notation: let the browser normalize it
    if(!window.CSS || !window.CSS.supports('color', value)) return fallback;
    let probe = document.createElement('span');
    probe.style.display = 'none';
    probe.style.color = value;
    document.body.appendChild(probe);
    let rgb = getComputedStyle(probe).color.match(/\d+(?:\.\d+)?/g);
    probe.remove();

    return rgb && rgb.length >= 3 ? '#' + toHex(rgb[0]) + toHex(rgb[1]) + toHex(rgb[2]) : fallback;
}

export const changeStatusBarColor = (ionicData, color, dark) => {
    let data = {action: 'changeStatusBarColor', color: resolveColor(color), dark};
    if(UA.isAndroidApp()) {
        window.Android.changeStatusBarColor(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {
        mobileAction(ionicData);
    }
}

export const changeUrl = (ionicData, url) => {
    let data = {action: 'changeUrl', url};
    if(UA.isAndroidApp()) {
        window.Android.changeUrl(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {
        mobileAction(ionicData);
    }
}