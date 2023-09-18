
export const API_URL = 'ms/api';
export const API_URL_BIDOQ = "https://mispapeles.es/api/v2/index.php";

export const URL_PDF_VIEWER = "https://mozilla.github.io/pdf.js/build/pdf.js";

//----PROD
export const SIG_URL = "https://aonsolutions.org";
export const SIG_DOMAIN_NAME = "sig.aonsolutions.org";
export const SIG_DOMAIN_ID = 5;
export const PRO_URL = "https://aon.solutions";
//----TEST
// export const SIG_URL = "https://b4da-47-62-53-208.ngrok.io";
// export const SIG_DOMAIN_NAME = "b72384936-ayudat.rvasquez.net";
// export const SIG_DOMAIN_ID = 7138;


export const SIG_SESSION_ID = "SIGd95770f269e711eb94390242ac130002";

//FB
export const VAPIDKEY_FB = "BCH91WxACVIpylkYRMj3xSpIfrzjz7Ixnctcj25BPMBZKSbGeKjJFIdaRsJGQ3F-SXVFGD0cr4outWLCFwemIkE";
export const CONFIG_FB   = {
    apiKey: "AIzaSyDeUk7hHabedCzkA7qBoHRu43VC7N9CJOs",
    authDomain: "aon-solutions-d69a4.firebaseapp.com",
    databaseURL: "https://aon-solutions-d69a4.firebaseio.com",
    projectId: "aon-solutions-d69a4",
    storageBucket: "aon-solutions-d69a4.appspot.com",
    messagingSenderId: "292041697338",
    appId: "1:292041697338:web:81dbe6e044074cbc58ed03",
    measurementId: "G-7MQDKLET0Y",
};


import * as MSG from './msg.js';
import * as EVENT from './aonEvent.js';
import * as TAG from './aonTag.js';
import * as CSS from './css.js';
import * as COLORS from './colors.js';
import * as MATERIAL_ICONS from './materialIcons.js';
import * as AON_ICONS from './aonIcons.js';
import * as CONSTANT from './constants.js';
import * as API from './aonApi.js';

export {MSG, EVENT, TAG, CSS, COLORS, MATERIAL_ICONS, AON_ICONS, CONSTANT, API};
