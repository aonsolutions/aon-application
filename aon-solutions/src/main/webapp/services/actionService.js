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

export const getPosition = (ionicData, callback) => {
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

export const openFile = (file) => {
    let data = {action: 'openFile', url: file.url, title: file.title};
    if(UA.isAndroidApp()) {
        window.Android.openFile(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {

    }
}

export const changeStatusBarColor = (color) => {
    let data = {action: 'changeStatusBarColor', color};
    if(UA.isAndroidApp()) {
        window.Android.openFile(JSON.stringify(data));
    } else if (UA.isIosApp()) {
        window.webkit.messageHandlers.doStuffMessageHandler.postMessage(data);
    } else if(UA.isAppMobile()) {

    }
}