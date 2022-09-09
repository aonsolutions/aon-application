import { webkitRequestMobile, sendActionMobile } from "./request.js";

export const webkitMobile = () => webkitRequestMobile();

export const mobileAction = (data) => sendActionMobile(data);

export const MOBILE_ACTION = {
    CAMERA: 'camera', //OPEN CAMERA
    BARCODE:'barcode', // BARCODE (function barcodeData)
    FILE_DOWNLOAD: 'fileDownload', // DOWNLOAD FILE OR OPEN PARAMS(BASE64, FILENAME, CONTENT_TYPE)
    SET_CURRENT_POSITION: 'setCurrentPosition', // SET POSITION (window.setCurrentPosition)
    SET_WATCH: 'setWatch',// SET POSITION (window.setWatch)
    CLEAR_WATCH: 'clearWatch',// STOP WATCH POSITION
    SET_POSITION: 'setPosition', //OLD SET POSITION  (window.setPosition) params times,
    SET_WATCH_POSITION:'setWatchPosition', //SET POSITION  (window.setPosition)
    SET_BASE_URL: 'setBaseUrl', //CHANGE URL BASE APP
    CLOSE_APP: 'closeAPP', //CLOSE APP
    REMOVE_SESSION_BIDOQ: 'removeSessionBidoq', //CLOSE APP
}