import * as UA from './userAgentService.js';

export const openCamera = () => {
    if(UA.isAndroidApp()) {
        window.Android.openCamera();
    } else if(UA.isAppMobile()) {

    }
}